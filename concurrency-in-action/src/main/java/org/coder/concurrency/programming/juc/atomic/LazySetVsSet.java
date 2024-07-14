package org.coder.concurrency.programming.juc.atomic;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 6.其他方法
 * 1.void set(int newValue): 为了AtomicInteger的value设置一个新值，通过对前面内容的学习，我们知道在AtomicInteger中有一个被volatile关键字修饰的value成员属性，
 * 因此调用set方法为value设置新值后其他线程就会立即看见。
 * 2.void lazySet(int newValue): set方法修饰被volatile关键字修饰的value值会被强制刷新到主内存中，从而立即被其他线程看到，这一切都应该归功于volatile关键字底层的内存屏障。
 * 内存屏障虽然足够轻量，但是毕竟还是会带来性能上的开销，比如，在单线程中对AtomicInteger的value进行修改时，没有必要保留内存屏障，而value又是被volatile关键字修饰的，这似乎是无法调和的矛盾。
 * 幸好追求性能极致的JVM开发者们早就考虑到了这一点，lazySet方法的作用正在于此。
 * <p>
 * 当对性能有异议的时候，JMH这把瑞士军刀总能帮我们找到答案，在该类中，我们写了两个基准测试方法用于对比set方法和lazyset方法的性能表现。
 * <p>
 * 运行上面的基准测试代码，我们很容易就能得到合理的判断，运行结果如下。
 * Benchmark                 Mode  Cnt  Score    Error  Units
 * LazySetVsSet.testLazySet  avgt   10  0.001 ±  0.001  us/op
 * LazySetVsSet.testSet      avgt   10  0.011 ±  0.001  us/op
 */
@Measurement(iterations = 10)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class LazySetVsSet {

    private AtomicInteger ai;

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(LazySetVsSet.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opts).run();
    }

    @Setup(Level.Iteration)
    public void setUp() {
        this.ai = new AtomicInteger(0);
    }

    @Benchmark
    public void testSet() {
        this.ai.set(10);
    }

    @Benchmark
    public void testLazySet() {
        this.ai.lazySet(10);
    }
}

//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 10 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: org.coder.concurrency.programming.juc.atomic.LazySetVsSet.testLazySet
//
//# Run progress: 0.00% complete, ETA 00:00:40
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.001 us/op
//# Warmup Iteration   2: 0.001 us/op
//# Warmup Iteration   3: 0.001 us/op
//# Warmup Iteration   4: 0.001 us/op
//# Warmup Iteration   5: 0.001 us/op
//# Warmup Iteration   6: 0.001 us/op
//# Warmup Iteration   7: 0.001 us/op
//# Warmup Iteration   8: 0.001 us/op
//# Warmup Iteration   9: 0.001 us/op
//# Warmup Iteration  10: 0.001 us/op
//Iteration   1: 0.001 us/op
//Iteration   2: 0.001 us/op
//Iteration   3: 0.001 us/op
//Iteration   4: 0.001 us/op
//Iteration   5: 0.001 us/op
//Iteration   6: 0.001 us/op
//Iteration   7: 0.001 us/op
//Iteration   8: 0.001 us/op
//Iteration   9: 0.001 us/op
//Iteration  10: 0.001 us/op
//
//
//Result "org.coder.concurrency.programming.juc.atomic.LazySetVsSet.testLazySet":
//  0.001 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.001, 0.001, 0.001), stdev = 0.001
//  CI (99.9%): [0.001, 0.001] (assumes normal distribution)
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 10 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 min per iteration
//# Threads: 1 thread, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: org.coder.concurrency.programming.juc.atomic.LazySetVsSet.testSet
//
//# Run progress: 50.00% complete, ETA 00:00:20
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.012 us/op
//# Warmup Iteration   2: 0.012 us/op
//# Warmup Iteration   3: 0.012 us/op
//# Warmup Iteration   4: 0.011 us/op
//# Warmup Iteration   5: 0.011 us/op
//# Warmup Iteration   6: 0.011 us/op
//# Warmup Iteration   7: 0.011 us/op
//# Warmup Iteration   8: 0.010 us/op
//# Warmup Iteration   9: 0.011 us/op
//# Warmup Iteration  10: 0.011 us/op
//Iteration   1: 0.011 us/op
//Iteration   2: 0.011 us/op
//Iteration   3: 0.011 us/op
//Iteration   4: 0.011 us/op
//Iteration   5: 0.010 us/op
//Iteration   6: 0.010 us/op
//Iteration   7: 0.011 us/op
//Iteration   8: 0.011 us/op
//Iteration   9: 0.011 us/op
//Iteration  10: 0.011 us/op
//
//
//Result "org.coder.concurrency.programming.juc.atomic.LazySetVsSet.testSet":
//  0.011 ±(99.9%) 0.001 us/op [Average]
//  (min, avg, max) = (0.010, 0.011, 0.011), stdev = 0.001
//  CI (99.9%): [0.010, 0.011] (assumes normal distribution)
//
//
//# Run complete. Total time: 00:00:41
//
//Benchmark                 Mode  Cnt  Score    Error  Units
//LazySetVsSet.testLazySet  avgt   10  0.001 ±  0.001  us/op
//LazySetVsSet.testSet      avgt   10  0.011 ±  0.001  us/op
