package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * (what) StackProfiler 不仅可以输出线程堆栈的信息，还能统计程序在执行的过程中线程状态的数据
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
@State(Scope.Group)
public class JMHExample21 {
    private BlockingQueue<Integer> queue;
    private final static int VALUE = Integer.MAX_VALUE;

    @Setup
    public void init() {
        queue = new ArrayBlockingQueue<>(10);
    }

    @GroupThreads(5)
    @Group("blockingQueue")
    @Benchmark
    public void put() throws InterruptedException {
        queue.put(VALUE);
    }

    @GroupThreads(5)
    @Group("blockingQueue")
    @Benchmark
    public int take() throws InterruptedException {
        return queue.take();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder()
                .include(JMHExample21.class.getSimpleName())
                .timeout(TimeValue.seconds(10))
                .addProfiler(StackProfiler.class)
                .result("JMHExample21.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(opts).run();
    }
}
