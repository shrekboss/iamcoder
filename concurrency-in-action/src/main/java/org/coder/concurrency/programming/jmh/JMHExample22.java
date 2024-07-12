package org.coder.concurrency.programming.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * (what) GcProfiler 可以用于分析在测试方法中垃圾回收器在 JVM 每个内存空间上所花费的时间
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample22 {
    private byte[] alexBytes;
    private AlexClassLoader classLoader;

    @Setup
    public void init() throws IOException {
        //alexByte = Files.readAllBytes(Paths.get("L:\\Alex.class"));
        alexBytes = Files.readAllBytes(Paths.get("/Users/crayzer/workspaces/iamcoder/concurrency-in-action/target/classes/org/coder/concurrency/programming/jmh/Alex.class"));
        classLoader = new AlexClassLoader(alexBytes);
    }

    @Benchmark
    public Object testLoadClass() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> alexClass = Class.forName("Alex", true, classLoader);
        return alexClass.newInstance();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder()
                .include(JMHExample22.class.getSimpleName())
                .addProfiler(GCProfiler.class)
                .jvmArgsAppend("-Xmx128M")
                .result("JMHExample22.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opts).run();
    }
}