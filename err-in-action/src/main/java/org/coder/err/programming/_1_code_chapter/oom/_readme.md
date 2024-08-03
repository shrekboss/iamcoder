## 别以为“自动挡”就不可能出现OOM

### 1. 太多份相同的对象导致OOM

参考代码：[usernameautocomplete](usernameautocomplete)

在进行容量评估时，我们不能认为一份数据在程序内存中也是一份

- 100M 的数据加载到程序内存中，变为 Java 的数据结构就已经占用了 200M 堆内存；
- 这些数据经过 JDBC、MyBatis 等框架其实是加载了 2 份，然后领域模型、DTO 再进行转换可能又加载了 2 次；
- 最终，占用的内存达到了 200M*4=800M

### 2. 使用WeakHashMap不等于不会OOM

参考代码：[weakhashmapoom](weakhashmapoom)

#### Java 中引用类型和垃圾回收的关系

- 垃圾回收器不会回收有强引用的对象；
- 在内存充足时，垃圾回收器不会回收具有软引用的对象；
- 垃圾回收器只要扫描到了具有弱引用的对象就会回收，WeakHashMap 就是利用了这个特点。

#### WeakHashMap

分析一下 WeakHashMap 的源码，你会发现 WeakHashMap 和 HashMap 的最大区别，是 Entry 对象的实现。

```JAVA
private static class Entry<K, V> extends WeakReference<Object> {
    // ...

    /**
     * Creates new entry.
     */
    Entry(Object key, V value,
          ReferenceQueue<Object> queue,
          int hash, Entry<K, V> next) {
        super(key, queue);
        this.value = value;
        this.hash = hash;
        this.next = next;
    }
}
```

queue 是一个 ReferenceQueue，被 GC 的对象会被丢进这个 queue 里面。

被丢进 queue 后是如何被销毁的：

```JAVA
public V get(Object key) {
    Object k = maskNull(key);
    int h = hash(k);
    Entry<K, V>[] tab = getTable();
    int index = indexFor(h, tab.length);
    Entry<K, V> e = tab[index];
    while (e != null) {
        if (e.hash == h && eq(k, e.get()))
            return e.value;
        e = e.next;
    }
    return null;
}

private Entry<K, V>[] getTable() {
    expungeStaleEntries();
    return table;
}

/**
 * Expunges stale entries from the table.
 */
private void expungeStaleEntries() {
    for (Object x; (x = queue.poll()) != null; ) {
        synchronized (queue) {
            @SuppressWarnings("unchecked")
            Entry<K, V> e = (Entry<K, V>) x;
            int i = indexFor(e.hash, table.length);

            Entry<K, V> prev = table[i];
            Entry<K, V> p = prev;
            while (p != null) {
                Entry<K, V> next = p.next;
                if (p == e) {
                    if (prev == e)
                        table[i] = next;
                    else
                        prev.next = next;
                    // Must not null out e.next;
                    // stale entries may be in use by a HashIterator
                    e.value = null; // Help GC
                    size--;
                    break;
                }
                prev = p;
                p = next;
            }
        }
    }
}
```

可以看到，每次调用 get、put、size 等方法时，都会从 queue 里拿出所有已经被 GC 掉的 key 并删除对应的 Entry 对象。回顾下这个逻辑：

- put 一个对象进 Map 时，它的 key 会被封装成弱引用对象；
- 发生 GC 时，弱引用的 key 被发现并放入 queue；
- 调用 get 等方法时，扫描 queue 删除 key，以及包含 key 和 value 的 Entry 对象

WeakHashMap 的 Key 虽然是弱引用，但如果 Value 持有 Key 中对象的强引用，Value 被 Entry 引用，Entry 被 WeakHashMap 引用，最终导致
Key 无法回收

#### ConcurrentReferenceHashMap

Spring 提供的ConcurrentReferenceHashMap类可以使用弱引用、软引用做缓存，Key 和 Value 同时被软引用或弱引用包装，也能解决相互引用导致的数据不能释放问题。与
WeakHashMap 相比，
ConcurrentReferenceHashMap 不但性能更好，还可以确保线程安全。

软引用和弱引用的区别在于：若一个对象是弱引用可达，无论当前内存是否充足它都会被回收，而软引用可达的对象在内存不充足时才会被回收。因此，软引用要比弱引用“强”一些。那么，使用弱引用作为缓存就会让缓存的生命周期过短，所以软引用更适合作为缓存。

### 3. Tomcat参数配置不合理导致OOM

参考代码：[impropermaxheadersize](impropermaxheadersize)

- 不合理的资源需求配置，在业务量小的时候可能不会出现问题，但业务量一大可能很快就会撑爆内存。比如，随意配置 Tomcat 的
  max-http-header-size 参数，会导致一个请求使用过多的内存，请求量大的时候出现
  OOM。在进行参数配置的时候，我们要认识到，很多限制类参数限制的是背后资源的使用，资源始终是有限的，需要根据实际需求来合理设置参数。
- 建议生产系统的程序配置 JVM 参数启用详细的 GC 日志，方便观察垃圾收集器的行为，并开启
  HeapDumpOnOutOfMemoryError，以便在出现 OOM 时能自动 Dump 留下第一问题现场。
  对于 JDK8，你可以这么设置：

```properties
XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=. -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M
```

一定要根据实际需求来修改参数配置，可以考虑预留 2 到 5 倍的量。容量类的参数背后往往代表了资源，设置超大的参数就有可能占用不必要的资源，在并发量大的时候因为资源大量分配导致
OOM。