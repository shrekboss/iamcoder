## 文件IO：实现高效正确的文件读写并非易事

### 1. 文件读写需要确保字符编码一致

参考代码：[badencodingissue](badencodingissue)

- 在处理文件读写的时候，如果是在字节层面进行操作，那么不会涉及字符编码问题；而如果需要在字符层面进行读写的话，就需要明确字符的编码方式也就是字符集了。
- FileReader 是以当前机器的默认字符集来读取文件的，如果希望指定字符集的话，需要直接使用 InputStreamReader 和
  FileInputStream。

- JDK1.7 推出的 Files 类的 readAllLines 方法，可以很方便地用一行代码完成文件内容读取，但是读取超出内存大小的大文件时会出现
  OOM
    - readAllLines 读取文件所有内容后，放到一个 List 中返回，如果内存无法容纳这个 List，就会 OOM

      ```java
      public static List<String> readAllLines(Path path, Charset cs) throws IOException {
          try (BufferedReader reader = newBufferedReader(path, cs)) {
              List<String> result = new ArrayList<>();
              for (;;) {
                  String line = reader.readLine();
                  if (line == null)
                      break;
                  result.add(line);
              }
              return result;
          }
      }
      ```

### 2. 使用Files类静态方法进行文件操作注意释放文件句柄：filestreamoperationneedclose

参考代码：[filestreamoperationneedclose](filestreamoperationneedclose)

与 Files.readAllLines 方法返回 List 不同，lines 方法返回的是 Stream。这在需要时可以不断读取、使用文件中的内容，而不是一次性地把所有内容都读取到内存中，因此避免了
OOM。

> 案例：程序在生产上运行一段时间后就会出现 too many files 的错误，我们想当然地认为是 OS 设置的最大
> 文件句柄太小了，就让运维放开这个限制，但放开后还是会出现这样的问题。经排查发现，其实是文件句柄没有释放导致的，问题就出在
> Files.lines 方法上。

一个很容易被忽略的严重问题: 读取完文件后没有关闭。通常会认为静态方法的调用不涉及资源释放，因为方法调用结束自然代表资源使用完成，由
API 释放资源，但对于 Files 类的一些返回 Stream 的方法并不是这样。

- 使用 Files 类的一些流式处理操作，注意使用 try-with-resources 包装 Stream，确保底层文件资源可以释放，避免产生 too many
  open files 的问题

```txt
java.nio.file.FileSystemException: demo.txt: Too many open files
at sun.nio.fs.UnixException.translateToIOException(UnixException.java:91)
at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:102)
at sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:107)
```

```
lsof -p 63937
...
java    63902 crayzer *238r   REG                1,4      370         12934160647 /Users/crayzer/Documents/common-mistakes/demo.txt
java    63902 crayzer *239r   REG                1,4      370         12934160647 /Users/crayzer/Documents/common-mistakes/demo.txt
...
lsof -p 63937 | grep demo.txt | wc -l
10007
```

查看 lines 方法源码可以发现，Stream 的 close 注册了一个回调，来关闭 BufferedReader 进行资源释放

```java
public static Stream<String> lines(Path path, Charset cs) throws IOException {
    BufferedReader br = Files.newBufferedReader(path, cs);
    try {
        return br.lines().onClose(asUncheckedRunnable(br));
    } catch (Error | RuntimeException e) {
        try {
            br.close();
        } catch (IOException ex) {
            try {
                e.addSuppressed(ex);
            } catch (Throwable ignore) {
            }
        }
        throw e;
    }
}

private static Runnable asUncheckedRunnable(Closeable c) {
    return () -> {
        try {
            c.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    };
}
```

### 3. 注意读写文件要考虑设置缓冲区

参考代码：[filebufferperformance](filebufferperformance)

| ns         | %    | Task name                     |
|------------|------|-------------------------------|
| 1424649223 | 086% | bufferedStreamByteOperation   |
| 117807808  | 007% | bufferedStreamBufferOperation |
| 112153174  | 007% | largerBufferOperation         |

- 第一种方式虽然使用了缓冲流，但逐字节的操作因为方法调用次数实在太多还是慢，耗时 1.4 秒；后面两种方式的性能差不多，耗时 110
  毫秒左右。虽然第三种方式没有使用缓冲流，但使用了 8KB 大小的缓冲区，和缓冲流默认的缓冲区大小相同

- 在实际代码中每次需要读取的字节数很可能不是固定的，有的时候读取几个字节，有的时候读取几百字节，这个时候有一个固定大小较大的缓冲，也就是使用
  BufferedInputStream 和 BufferedOutputStream 做为后备的稳定的二次缓冲，就非常有意义了
- 文件操作因为涉及操作系统和文件系统的实现，JDK 并不能确保所有 IO API 在所有平台的逻辑一致性，代码迁移到新的操作系统或文件系统时，要重新进行功能测试和性能测试。

| ns           | %    | Task name                     |
|--------------|------|-------------------------------|
| 244489554362 | 098% | perByteOperation              |
| 2881307696   | 001% | bufferOperationWith100Buffer  |
| 974143806    | 000% | bufferedStreamByteOperation   |
| 095795414    | 000% | bufferedStreamBufferOperation |
| 103223408    | 000% | largerBufferOperation         |
| 039724047    | 000% | fileChannelOperation          |

结论：

- 每读取一个字节、每写入一个字节都进行一次 IO 操作，代价太大了
- 在进行文件 IO 处理的时候，使用合适的缓冲区可以明显提高性能
- BufferedInputStream | BufferedOutputStream 内部默认实现了一个 8KB 大小的缓冲区。但是，在使用 BufferedInputStream 和
  BufferedOutputStream 时，还是建议你再使用一个缓冲进行读写，不要因为它们实现了内部缓冲就进行逐字节的操作。
- 对于类似的文件复制操作，希望有更高性能，可以使用 FileChannel 的 transfreTo 方法进行流的复制。实
  现 DMA（直接内存访问），也就是数据从磁盘经过总线直接发送到目标文件，无需经过内存和 CPU
  进行数据中转。[transferTo 方法的更多细节](https://developer.ibm.com/articles/j-zerocopy/)

### 4. 问题

#### Files.lines 方法进行流式处理，需要使用 try-with-resources 进行资源释放。那么，使用 Files 类中其他返回 Stream 包装对象的方法进行流式处理，比如 newDirectoryStream 方法返回 DirectoryStream，list、walk 和 find 方法返回 Stream，也同样有资源释放问题吗？

使用 Files 类中其他返回 Stream 包装对象的方法进行流式处理，也同样会有资源释放问题。因为，这些接口都需要使用
try-with-resources 模式来释放。正如文中所说，如果不显式释放，那么可能因为底层资源没有及时关闭造成资源泄露。

#### Java 的 File 类和 Files 类提供的文件复制、重命名、删除等操作，是原子性的吗？

Java 的 File 和 Files 类的文件复制、重命名、删除等操作，都不是原子性的。原因是，文件类操作基本都是调用操作系统本身的
API，一般来说这些文件 API 并不像数据库有事务机制（也很难办到），即使有也很可能有平台差异性。