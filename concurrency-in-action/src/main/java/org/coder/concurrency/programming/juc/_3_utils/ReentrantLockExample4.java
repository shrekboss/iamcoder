package org.coder.concurrency.programming.juc._3_utils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3.6.3 ReentrantLock VS. Synchronized关键字
 * 在本章中，我们学习了ReentrantLock具备Synchronized关键字全部的 功能特性，
 * 从使用的角度来看，ReentrantLock相比于Synchronized关键字提供 了更加灵活和丰富的操作方式，
 * 但是它们的性能对比会是怎样的呢?本节将使用基准测试工具JMH对两者进行比较。
 * <p>
 * 1.单线程读操作性能对比
 * 线程安全的方法或者线程安全的类未必总是会在使用多线程的情况下运行，比如hashtable、StringBuffer等，
 * 在本节中，我们将对比一下使用lock和synchronized关键字进行同步的方法在单线程下的性能表现。
 * <p>
 * 运行上面的基准测试，我们会惊奇地发现在单线程访问的情况下，synchronized关键字的线程性能要远高于lock锁，这主要得益于JDK内部对于synchronized关键字的不断优化升级，
 * 另外在单线程的情况下，synchronized关键字的jvm指令在运行期间也会被优化。
 * Benchmark                             Mode  Cnt  Score    Error  Units
 * ReentrantLockExample4.base            avgt   10  0.002 ±  0.001  us/op
 * ReentrantLockExample4.testLockMethod  avgt   10  0.019 ±  0.001  us/op
 * ReentrantLockExample4.testSyncMethod  avgt   10  0.004 ±  0.001  us/op
 */
//基准测试的设定，10批次Warmup，10批次Measurement
@Measurement(iterations = 10)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
//单线程
@Threads(1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
//每个线程一个实例
@State(Scope.Thread)
public class ReentrantLockExample4 {

    public static class Test {
        private int x = 10;
        private final Lock lock = new ReentrantLock();

        //基准方法
        public int baseMethod() {
            return x;
        }

        //使用lock进行方法同步
        public int lockMethod() {
            lock.lock();
            try {
                return x;
            } finally {
                lock.unlock();
            }
        }

        //使用关键字synchronized进行方法同步
        public int syncMethod() {
            synchronized (this) {
                return x;
            }
        }
    }

    private Test test;

    //每一个批次都会产生一个新的test实例
    @Setup(Level.Iteration)
    public void setUp() {
        this.test = new Test();
    }

    @Benchmark
    public void base(Blackhole hole) {
        hole.consume(test.baseMethod());
    }

    @Benchmark
    public void testLockMethod(Blackhole hole) {
        hole.consume(test.lockMethod());
    }

    @Benchmark
    public void testSyncMethod(Blackhole hole) {
        hole.consume(test.syncMethod());
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder().include(ReentrantLockExample4.class.getSimpleName()).forks(1).build();
        new Runner(opts).run();
    }

}