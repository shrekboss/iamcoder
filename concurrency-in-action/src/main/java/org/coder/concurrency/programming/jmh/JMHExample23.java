package org.coder.concurrency.programming.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.ClassloaderProfiler;
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
 * (what) ClassLoaderProfiler 可以帮助我们看到在基准方法的执行过程中有多少类被加载和卸载
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
@Warmup(iterations = 0)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample23 {
    private byte[] alexBytes;
    private AlexClassLoader classLoader;
    @Setup
    public void init() throws IOException {
        alexBytes = Files.readAllBytes(Paths.get("/Users/crayzer/workspaces/iamcoder/concurrency-in-action/target/classes/org/coder/concurrency/programming/jmh/Alex.class"));
        classLoader = new AlexClassLoader(alexBytes);
    }
    @Benchmark
    public Object testLoadClass() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> alexClass = Class.forName("Alex", true, classLoader);
        return alexClass.newInstance();
    }
    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder()
                .include(JMHExample23.class.getSimpleName())
                .addProfiler(ClassloaderProfiler.class)
                .result("JMHExample23.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opts).run();
    }
}
