package org.coder.concurrency.programming.juc._3_utils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 3.7.3 基准测试性能对比
 * 3.6.3节对比了ReentrantLock和synchronized关键字在不同场景下的性能表现，本节也将使用JMH基准工具对读写锁进行性能比较。
 * (1)10个线程的只读性能比较
 * 基准测试代码在3.6.3节的基础之上增加了读锁的操作，代码片段如下。
 * <p>
 * 根据基准测试的结果来看，在没有任何写操作的情况下，读锁的效率反倒是最差的，这的确令人感到失望和惊讶，
 * 实际上，ReadWriteLock的性能表现确实不尽如人意，这也是在JDK1.8版本中引入StampedLock的原因之一，
 * 后文的3.9节中将会详细说明。
 * <p>
 * 通过与基准数据的对比，不难看出在10个线程并发只读情况下，性能表现的好坏程度依次如下：
 * ReentrantLock > synchronized关键字 > ReentrantReadWriteLock
 * <p>
 * Benchmark                                Mode  Cnt  Score   Error  Units
 * ReentrantReadWriteLockExample2.base      avgt   10  0.006 ± 0.001  us/op
 * ReentrantReadWriteLockExample2.lock      avgt   10  0.288 ± 0.004  us/op
 * ReentrantReadWriteLockExample2.readlock  avgt   10  2.275 ± 0.025  us/op
 * ReentrantReadWriteLockExample2.sync      avgt   10  0.404 ± 0.026  us/op
 */
//基准测试的设定，10批次Warmup，10批次Measurement
@Measurement(iterations = 10)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ReentrantReadWriteLockExample2 {

    @State(Scope.Group)
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

        private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private final Lock readLock = readWriteLock.readLock();

        public int readLockMethod() {
            readLock.lock();
            try {
                return x;
            } finally {
                readLock.unlock();
            }
        }
    }

    //10个线程进行测试
    @GroupThreads(10)
    @Group("base")
    @Benchmark
    public void base(Test test, Blackhole hole) {
        hole.consume(test.baseMethod());
    }

    //10个线程进行测试
    @GroupThreads(10)
    @Group("lock")
    @Benchmark
    public void testLockMethod(Test test, Blackhole hole) {
        hole.consume(test.lockMethod());
    }

    //10个线程进行测试
    @GroupThreads(10)
    @Group("sync")
    @Benchmark
    public void testSyncMethod(Test test, Blackhole hole) {
        hole.consume(test.syncMethod());
    }

    //10个线程进行测试
    @GroupThreads(10)
    @Group("readlock")
    @Benchmark
    public void testReadLockMethod(Test test, Blackhole hole) {
        hole.consume(test.readLockMethod());
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(ReentrantReadWriteLockExample2.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opts).run();
    }

}