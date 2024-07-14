package org.coder.concurrency.programming.juc.atomic;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java 并发包之原子类型详解
 * <p>
 * 在笔者的第一本书《Java高并发编程详解：多线程与架构设计》中详细分析了关键字volatile，无论是基本数据类型还是引用类型的变量，只要被volatile关键字修饰，
 * 从JMM(Java Memory Model)的角度分析，该变量就具备了有序性和可见性这两个语义特质，但是它还是无法保证原子性。那么，什么是原子性呢？
 * 原子性是指某个操作或者一系列操作要么都成功，要么都失败，不允许出现因中断而导致的部分成功或部分失败的情况。
 * <p>
 * 比如，对int类型的加法操作就是原子性的，如x+1。但是我们在使用的过程中往往会将x+1的结果赋予另一个变量甚至是x变量本身，即进行x=x+1或者x++这样的操作，
 * 而这样的语句事实上是由若干个原子性的操作组合而来的，因此它们就不具备原子性。这样的语句的具体实现步骤如下。
 * 1) 将主内存中x的值读取到CPU Cache中。
 * 2) 对x进行加一运算。
 * 3) 将结果写回到CPU Cache中。
 * 4) 将x的值刷新到主内存中。
 * <p>
 * 再比如，long类型的加法x+1的操作就不是原子性的。在Brian Goetz、Tim Peierls、Joshua Bloch、Joseph Bowbeer、David Holmes、Doug Lea合著的
 * 《Java Concurrency in Practice》一书的Nonatomic 64-bit operations章节中提到过：“a 64-bit write operation is basically performed
 * as two separate 32-bit operations. This behavior can result in indeterminate values being read in code and that lacks
 * atomicity.”(一个64位写操作实际上将会被拆分为2个32位的操作，这一行为的直接后果将会导致最终的的结果是不确定的并且缺少原子性的保证。)
 * 在Java虚拟机规范中同样也有类似的描述：“For the purposes of the Java programming language memory model, a single write to
 * a non-volatile long or double value is treated as two separate writes: one to each 32-bit half. This can result
 * in a situation where a thread sees the first 32 bits of a 64-bit value from one write, and the second 32 bits from another write.”
 * 详见虚拟机官方网址，地址如下：http://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.7
 * <p>
 * 在JDK1.5版本之前，为了确保在多线程下对某个基本数据类型或者引用数据类型运算的原子性，必须依赖于关键字synchronized，但是自JDK1.5版本以后这一情况发生了改变，
 * JDK官方为开发者提供了原子类型的工具集，比如AtomicInteger、AtomicBoolean等，这些原子类型都是Lock-Free及线程安全的，开发者将不再为一个数据类型的自增
 * 运算而增加synchronized的同步操作。本章将为大家详细介绍Java的各种原子类型(实际上在Java推出原子工具集之前，很多第三方库也提供了类似的解决方案，比如Google的
 * Guava，甚至于JDK自身的原子类工具集也是来自Doug Lea的个人项目)。
 * <p>
 * 在本章乃至本书中关于性能基准测试的所有方式都将依赖于JMH这一基准测试工具，因此建议读者认真阅读JMH的相关章节，并且掌握如何使用JMH进行基准测试。
 * <p>
 * 2.1 AtomicInteger详解
 * 本节首先对比一下被synchronized关键字和显式锁Lock(将在2.2节详细讲解)进行同步的int类型和AtomicInteger类型在多线程场景下的性能表现，然后再介绍AtomicInteger的内部原理和使用方法。
 * <p>
 * 2.1.1 性能测试对比
 * 任何新工具的出现，都是为了解决某个具体问题而诞生的，否则就没有存在的必要了，原子类型就是一种无锁的、线程安全的、使用基本数据类型和引用类型的很好的解决方案。
 * 在学习使用它之前，我们先来对比一下不同同步手段的性能表现。
 * <p>
 * 运行下面的基准测试方法将很容易对比出哪种解决方案的效率 更高。
 * Benchmark                                        Mode  Cnt  Score   Error  Units
 * SynchronizedVsLockVsAtomicInteger.atomic         avgt   10  0.254 ± 0.010  us/op
 * SynchronizedVsLockVsAtomicInteger.atomic:·stack  avgt         NaN            ---
 * SynchronizedVsLockVsAtomicInteger.lok            avgt   10  0.308 ± 0.005  us/op
 * SynchronizedVsLockVsAtomicInteger.lok:·stack     avgt         NaN            ---
 * SynchronizedVsLockVsAtomicInteger.sync           avgt   10  0.510 ± 0.263  us/op
 * SynchronizedVsLockVsAtomicInteger.sync:·stack    avgt         NaN            ---
 * <p>
 * AtomicInteger > 显式锁 Lock > synchronized 关键字
 * <p>
 * 从基准测试的结果不难看出，AtomicInteger的表现更优，在该基准测试的配置中，我们增加了StackProfiler，因此很容易窥探出AtomicInteger表现得优异的原因。
 * synchronized关键字的线程堆栈
 * 86.8%         BLOCKED
 * 11.9%         RUNNABLE
 * 1.3%         WAITING
 * 显式锁Lock的线程堆栈
 * 86.7%         WAITING
 * 13.3%         RUNNABLE
 * AtomicInteger的线程堆栈
 * 97.3%         RUNNABLE
 * 2.7%         WAITING
 * <p>
 * AtomicInteger线程的RUNNABLE状态高达97.3%，并且没有BLOCKED状态，而synchronized关键字则相反，BLOCKED状态高达86.8%，
 * 因此AtomicInteger高性能的表现也就不足为奇了。
 */
//度量批次为10次
@Measurement(iterations = 10)
//预热批次为10次
@Warmup(iterations = 10)
//采取平均响应时间作为度量方式
@BenchmarkMode(Mode.AverageTime)
//时间单位为微秒
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class SynchronizedVsLockVsAtomicInteger {

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(SynchronizedVsLockVsAtomicInteger.class.getSimpleName())
                .forks(1)
                .timeout(TimeValue.seconds(10))
                .addProfiler(StackProfiler.class)
                .build();
        new Runner(opts).run();

    }

    //基准测试方法
    @GroupThreads(10)
    @Group("sync")
    @Benchmark
    public void syncInc(IntMonitor monitor) {
        monitor.synInc();
    }

    //基准测试方法
    @GroupThreads(10)
    @Group("lock")
    @Benchmark
    public void lockInc(IntMonitor monitor) {
        monitor.lockInc();
    }

    //基准测试方法
    @GroupThreads(10)
    @Group("atomic")
    @Benchmark
    public void atomicIntegerInc(AtomicIntegerMonitor monitor) {
        monitor.inc();
    }

    @State(Scope.Group)
    public static class IntMonitor {
        private final Lock lock = new ReentrantLock();
        private int x;

        //使用显式锁Lock进行共享资源同步
        public void lockInc() {
            lock.lock();
            try {
                x++;
            } finally {
                lock.unlock();
            }
        }

        //使用synchronized关键字进行共享资源同步
        public void synInc() {
            synchronized (this) {
                x++;
            }
        }
    }

    //直接采用AtomicInteger
    @State(Scope.Group)
    public static class AtomicIntegerMonitor {
        private AtomicInteger x = new AtomicInteger();

        public void inc() {
            x.incrementAndGet();
        }
    }

}

//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 10 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 s per iteration
//# Threads: 10 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.atomic
//
//# Run progress: 0.00% complete, ETA 00:01:00
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.274 ±(99.9%) 0.068 us/op
//# Warmup Iteration   2: 0.255 ±(99.9%) 0.059 us/op
//# Warmup Iteration   3: 0.249 ±(99.9%) 0.050 us/op
//# Warmup Iteration   4: 0.246 ±(99.9%) 0.046 us/op
//# Warmup Iteration   5: 0.268 ±(99.9%) 0.063 us/op
//# Warmup Iteration   6: 0.252 ±(99.9%) 0.052 us/op
//# Warmup Iteration   7: 0.252 ±(99.9%) 0.037 us/op
//# Warmup Iteration   8: 0.254 ±(99.9%) 0.063 us/op
//# Warmup Iteration   9: 0.252 ±(99.9%) 0.056 us/op
//# Warmup Iteration  10: 0.251 ±(99.9%) 0.063 us/op
//Iteration   1: 0.253 ±(99.9%) 0.064 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   2: 0.272 ±(99.9%) 0.091 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   3: 0.256 ±(99.9%) 0.076 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   4: 0.254 ±(99.9%) 0.058 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   5: 0.251 ±(99.9%) 0.042 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   6: 0.250 ±(99.9%) 0.051 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   7: 0.252 ±(99.9%) 0.057 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   8: 0.252 ±(99.9%) 0.058 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   9: 0.251 ±(99.9%) 0.053 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  10: 0.251 ±(99.9%) 0.058 us/op
//                 ·stack: <delayed till summary>
//
//
//
//Result "org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.atomic":
//  0.254 ±(99.9%) 0.010 us/op [Average]
//  (min, avg, max) = (0.250, 0.254, 0.272), stdev = 0.006
//  CI (99.9%): [0.245, 0.264] (assumes normal distribution)
//
//Secondary result "org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.atomic:·stack":
//Stack profiler:
//
//....[Thread state distributions]....................................................................
// 97.3%         RUNNABLE
//  2.7%         WAITING
//
//....[Thread state: RUNNABLE]........................................................................
// 93.6%  96.3% org.coder.concurrency.programming.juc.atomic.generated.SynchronizedVsLockVsAtomicInteger_atomic_jmhTest.atomicIntegerInc_avgt_jmhStub
//  2.6%   2.7% org.coder.concurrency.programming.juc.atomic.generated.SynchronizedVsLockVsAtomicInteger_atomic_jmhTest.atomic_AverageTime
//  0.2%   0.2% java.lang.Thread.isInterrupted
//  0.2%   0.2% java.util.concurrent.locks.AbstractQueuedSynchronizer.releaseShared
//  0.1%   0.2% sun.misc.Unsafe.unpark
//  0.1%   0.1% java.lang.Thread.currentThread
//  0.1%   0.1% org.openjdk.jmh.runner.InfraControl.preTearDown
//  0.1%   0.1% sun.misc.Unsafe.compareAndSwapInt
//  0.0%   0.0% java.util.concurrent.locks.AbstractQueuedSynchronizer.doReleaseShared
//  0.0%   0.0% sun.misc.Unsafe.park
//  0.1%   0.1% <other>
//
//....[Thread state: WAITING].........................................................................
//  2.7% 100.0% sun.misc.Unsafe.park
//
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 10 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 s per iteration
//# Threads: 10 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.lok
//
//# Run progress: 33.33% complete, ETA 00:00:46
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.342 ±(99.9%) 0.057 us/op
//# Warmup Iteration   2: 0.338 ±(99.9%) 0.034 us/op
//# Warmup Iteration   3: 0.310 ±(99.9%) 0.026 us/op
//# Warmup Iteration   4: 0.307 ±(99.9%) 0.020 us/op
//# Warmup Iteration   5: 0.308 ±(99.9%) 0.010 us/op
//# Warmup Iteration   6: 0.313 ±(99.9%) 0.015 us/op
//# Warmup Iteration   7: 0.681 ±(99.9%) 0.055 us/op
//# Warmup Iteration   8: 0.326 ±(99.9%) 0.019 us/op
//# Warmup Iteration   9: 0.304 ±(99.9%) 0.023 us/op
//# Warmup Iteration  10: 0.305 ±(99.9%) 0.019 us/op
//Iteration   1: 0.305 ±(99.9%) 0.015 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   2: 0.306 ±(99.9%) 0.021 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   3: 0.305 ±(99.9%) 0.012 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   4: 0.305 ±(99.9%) 0.027 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   5: 0.305 ±(99.9%) 0.019 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   6: 0.308 ±(99.9%) 0.019 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   7: 0.309 ±(99.9%) 0.016 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   8: 0.312 ±(99.9%) 0.016 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   9: 0.311 ±(99.9%) 0.013 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  10: 0.313 ±(99.9%) 0.016 us/op
//                 ·stack: <delayed till summary>
//
//
//
//Result "org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.lok":
//  0.308 ±(99.9%) 0.005 us/op [Average]
//  (min, avg, max) = (0.305, 0.308, 0.313), stdev = 0.003
//  CI (99.9%): [0.303, 0.313] (assumes normal distribution)
//
//Secondary result "org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.lok:·stack":
//Stack profiler:
//
//....[Thread state distributions]....................................................................
// 86.7%         WAITING
// 13.3%         RUNNABLE
//
//....[Thread state: WAITING].........................................................................
// 86.7% 100.0% sun.misc.Unsafe.park
//
//....[Thread state: RUNNABLE]........................................................................
//  8.3%  62.1% org.coder.concurrency.programming.juc.atomic.generated.SynchronizedVsLockVsAtomicInteger_lok_jmhTest.lockInc_avgt_jmhStub
//  3.1%  23.1% sun.misc.Unsafe.unpark
//  1.8%  13.3% sun.misc.Unsafe.park
//  0.1%   0.7% org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.lockInc
//  0.0%   0.3% org.coder.concurrency.programming.juc.atomic.generated.SynchronizedVsLockVsAtomicInteger_lok_jmhTest.lok_AverageTime
//  0.0%   0.2% java.lang.Thread.currentThread
//  0.0%   0.2% sun.reflect.Reflection.getClassAccessFlags
//
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 10 iterations, 1 s each
//# Measurement: 10 iterations, 1 s each
//# Timeout: 10 s per iteration
//# Threads: 10 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.sync
//
//# Run progress: 66.67% complete, ETA 00:00:22
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.375 ±(99.9%) 0.089 us/op
//# Warmup Iteration   2: 0.382 ±(99.9%) 0.029 us/op
//# Warmup Iteration   3: 0.429 ±(99.9%) 0.071 us/op
//# Warmup Iteration   4: 0.376 ±(99.9%) 0.009 us/op
//# Warmup Iteration   5: 0.379 ±(99.9%) 0.015 us/op
//# Warmup Iteration   6: 0.373 ±(99.9%) 0.011 us/op
//# Warmup Iteration   7: 0.371 ±(99.9%) 0.031 us/op
//# Warmup Iteration   8: 0.383 ±(99.9%) 0.045 us/op
//# Warmup Iteration   9: 0.376 ±(99.9%) 0.048 us/op
//# Warmup Iteration  10: 0.366 ±(99.9%) 0.034 us/op
//Iteration   1: 0.371 ±(99.9%) 0.024 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   2: 0.376 ±(99.9%) 0.012 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   3: 0.504 ±(99.9%) 0.058 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   4: 0.686 ±(99.9%) 0.500 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   5: 0.824 ±(99.9%) 0.161 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   6: 0.744 ±(99.9%) 0.043 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   7: 0.391 ±(99.9%) 0.029 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   8: 0.380 ±(99.9%) 0.015 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   9: 0.388 ±(99.9%) 0.030 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  10: 0.436 ±(99.9%) 0.020 us/op
//                 ·stack: <delayed till summary>
//
//
//
//Result "org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.sync":
//  0.510 ±(99.9%) 0.263 us/op [Average]
//  (min, avg, max) = (0.371, 0.510, 0.824), stdev = 0.174
//  CI (99.9%): [0.247, 0.773] (assumes normal distribution)
//
//Secondary result "org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger.sync:·stack":
//Stack profiler:
//
//....[Thread state distributions]....................................................................
// 86.8%         BLOCKED
// 11.9%         RUNNABLE
//  1.3%         WAITING
//
//....[Thread state: BLOCKED].........................................................................
// 86.8% 100.0% org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger$IntMonitor.synInc
//
//....[Thread state: RUNNABLE]........................................................................
//  9.9%  83.0% org.coder.concurrency.programming.juc.atomic.SynchronizedVsLockVsAtomicInteger$IntMonitor.synInc
//  1.8%  15.3% org.coder.concurrency.programming.juc.atomic.generated.SynchronizedVsLockVsAtomicInteger_sync_jmhTest.syncInc_avgt_jmhStub
//  0.1%   0.6% sun.misc.Unsafe.unpark
//  0.0%   0.4% java.lang.Thread.isInterrupted
//  0.0%   0.1% java.lang.Thread.currentThread
//  0.0%   0.1% java.util.Collections$SynchronizedMap.get
//  0.0%   0.1% java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireSharedInterruptibly
//  0.0%   0.1% java.lang.reflect.Method.invoke
//  0.0%   0.1% org.openjdk.jmh.runner.InfraControlL2.announceWarmdownReady
//
//....[Thread state: WAITING].........................................................................
//  1.3% 100.0% sun.misc.Unsafe.park
//
//
//
//# Run complete. Total time: 00:01:05
//
//Benchmark                                        Mode  Cnt  Score   Error  Units
//SynchronizedVsLockVsAtomicInteger.atomic         avgt   10  0.254 ± 0.010  us/op
//SynchronizedVsLockVsAtomicInteger.atomic:·stack  avgt         NaN            ---
//SynchronizedVsLockVsAtomicInteger.lok            avgt   10  0.308 ± 0.005  us/op
//SynchronizedVsLockVsAtomicInteger.lok:·stack     avgt         NaN            ---
//SynchronizedVsLockVsAtomicInteger.sync           avgt   10  0.510 ± 0.263  us/op
//SynchronizedVsLockVsAtomicInteger.sync:·stack    avgt         NaN            ---
