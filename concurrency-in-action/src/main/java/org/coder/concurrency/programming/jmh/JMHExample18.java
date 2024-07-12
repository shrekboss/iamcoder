package org.coder.concurrency.programming.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * (what) Asymmetric Benchmark
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
public class JMHExample18 {
    private AtomicInteger counter;

    @Setup
    public void init(){
        this.counter = new AtomicInteger();
    }

    @GroupThreads(5)
    @Group("q")
    @Benchmark
    public void inc(){
        this.counter.incrementAndGet();
    }

    @GroupThreads(5)
    @Group("q")
    @Benchmark
    public int get(){
        return this.counter.get();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder().include(JMHExample18.class.getSimpleName()).build();
        new Runner(opts).run();
    }
}