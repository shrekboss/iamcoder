package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
@Measurement(iterations = 5)
@Warmup(iterations = 2)
public class JMHExample03 {

    @Benchmark
    public void test() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(10);
    }

    /**
     * 预热5个批次
     * 度量10个批次
     */
    @Measurement(iterations = 10)
    @Warmup(iterations = 5)
    @Benchmark
    public void test2() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1);
    }

    public static void main(String[] args) throws RunnerException {

        final Options opts = new OptionsBuilder()
                .include(JMHExample03.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opts).run();
    }
}
