package org.coder.err.programming._1_code_chapter.io.filestreamoperationneedclose;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

@Slf4j
public class CommonMistakesApplication {

    // 设置 -Xmx512m -Xms512m
    public static void main(String[] args) throws IOException {
        // init();

        // File.lines 方法并不是一次性读取整个文件的，而是按需读取。
        CommonMistakesApplication.linesTest();

        // readLargeFileRight();
        // readLargeFileWrong();

        /* FileSystemException：too many files*/
        // wrong(); 执行需谨慎
        // right();
    }

    private static void readLargeFileWrong() throws IOException {
        log.info("lines {}", Files.readAllLines(Paths.get("large.txt")).size());
    }

    private static void readLargeFileRight() throws IOException {
        AtomicLong atomicLong = new AtomicLong();
        Files.lines(Paths.get("large.txt")).forEach(line -> atomicLong.incrementAndGet());
        log.info("lines {}", atomicLong.get());
    }

    private static void linesTest() throws IOException {

        //输出文件大小
        log.info("file size:{}", Files.size(Paths.get("large.txt")));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("read 200000 lines");
        //使用Files.lines方法读取20万行数据
        log.info("lines {}", Files.lines(Paths.get("large.txt")).limit(200_000).collect(Collectors.toList()).size());
        stopWatch.stop();
        stopWatch.start("read 500_000 lines");
        //使用Files.lines方法读取50万行数据
        log.info("lines {}", Files.lines(Paths.get("large.txt")).limit(500_000).collect(Collectors.toList()).size());
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        AtomicLong atomicLong = new AtomicLong();
        //使用Files.lines方法统计文件总行数
        Files.lines(Paths.get("large.txt")).forEach(line -> atomicLong.incrementAndGet());
        log.info("total lines {}", atomicLong.get());
    }

    /**
     * 生成 5G 大小的文件
     */
    private static void init() throws IOException {
        String payload = IntStream.rangeClosed(1, 1000)
                .mapToObj(__ -> "a")
                .collect(Collectors.joining("")) + UUID.randomUUID().toString();
        Files.deleteIfExists(Paths.get("large.txt"));
        IntStream.rangeClosed(1, 10).forEach(__ -> {
            try {
                Files.write(Paths.get("large.txt"),
                        IntStream.rangeClosed(1, 500_000).mapToObj(i -> payload).collect(Collectors.toList())
                        , UTF_8, CREATE, APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Files.write(Paths.get("demo.txt"),
                IntStream.rangeClosed(1, 10).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toList())
                , UTF_8, CREATE, TRUNCATE_EXISTING);
    }

    private static void wrong() {
        //ps aux | grep CommonMistakesApplication
        //lsof -p 63937
        LongAdder longAdder = new LongAdder();
        IntStream.rangeClosed(1, 1_000_000).forEach(i -> {
            try {
                Files.lines(Paths.get("demo.txt")).forEach(line -> longAdder.increment());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        log.info("total : {}", longAdder.longValue());
    }

    private static void right() {
        //https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html
        LongAdder longAdder = new LongAdder();
        IntStream.rangeClosed(1, 1_000_000).forEach(i -> {
            try (Stream<String> lines = Files.lines(Paths.get("demo.txt"))) {
                lines.forEach(line -> longAdder.increment());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        log.info("total : {}", longAdder.longValue());
    }
}

