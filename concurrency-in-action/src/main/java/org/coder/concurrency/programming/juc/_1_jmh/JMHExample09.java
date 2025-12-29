package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
// 5 个线程同时对共享资源进行操作
@Threads(5)
// 设置为线程间共享的资源
@State(Scope.Benchmark)
public class JMHExample09 {

    private Map<Long, Long> concurrentMap;
    private Map<Long, Long> synchronizedMap;

    @Setup
    public void setup() {
        this.concurrentMap = new ConcurrentHashMap<>();
        this.synchronizedMap = Collections.synchronizedMap(new HashMap<>());
    }

    @Benchmark
    public void testConcurrentMap() {
        this.concurrentMap.put(System.nanoTime(), System.nanoTime());
    }

    @Benchmark
    public void testSynchronizedMap() {
        this.synchronizedMap.put(System.nanoTime(), System.nanoTime());
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder().include(JMHExample09.class.getSimpleName()).build();
        new Runner(opts).run();
    }
}