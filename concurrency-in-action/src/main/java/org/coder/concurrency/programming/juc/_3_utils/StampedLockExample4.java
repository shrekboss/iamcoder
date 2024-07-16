package org.coder.concurrency.programming.juc._3_utils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * 3.9.3 与其他锁的性能对比
 * StampedLock的方法虽然比较多，但是使用起来还是相对比较简单的。本节已经详细介绍了几个主要方法的使用，由于篇幅的关系此处再进行每一个方法的讲解，
 * 作为Lock界的新宠，我们还是要本着半信半疑的态度去验证它是否真的带来了革命性的变化，同样我们也会使用基准测试“大杀器”JHM对其进行测试，
 * 本节所做的基准测试将比较ReentrantLock、ReentrantReadWriteLock、StampedLock读写分离以及乐观读分别在不同读写线程数量下的性能表现，
 * 同时我们还将采用方法调用吞吐量的统计方式。
 * <p>
 * 基准测试的代码足够简单，此处就不做过多解释了，我们将通过修改@GroupThreads(n)的n值进行不同情况下的基准测试性能对比。
 * (1)5个读 5个写线程
 * 读写基准测试方法设置@GroupThreads(5)，运行基准测试。
 * 1.10个线程(5个读线程，5个写线程)基准测试下的总体性能表现。
 * Optimistic > ReentrantLock > StampedLock > ReentrantRWLock
 * 2.10个线程(5个读线程，5个写线程)基准测试下的读性能表现。
 * Optimistic > ReentrantLock > ReentrantRWLock > StampedLock
 * 3.10个线程(5个读线程，5个写线程)基准测试下的写性能表现。
 * StampedLock > ReentrantLock > Optimistic > ReentrantRWLock
 * ReentrantLock的成绩很稳定，无论是总体情况还是读写情况都位居第二的位置，读写锁的表现就稍微差一些了。
 * <p>
 * (2)10个读 10个写线程
 * 读写基准测试方法设置@GroupThreads(10)，运行基准测试。
 * 1.20个线程(10个读线程，10个写线程)基准测试下的总体性能表现。
 * Optimistic > ReentrantLock > StampedLock > ReentrantRWLock
 * 2.20个线程(10个读线程，10个写线程)基准测试下的读性能表现。
 * Optimistic > ReentrantLock > ReentrantRWLock > StampedLock
 * 3.20个线程(10个读线程，10个写线程)基准测试下的写性能表现。
 * StampedLock > ReentrantLock > ReentrantRWLock > Optimistic
 * 除了写性能的排名发生变化以外，总体上来说这四种锁在20个线程的情况下与10个线程的情况下，它们的性能表现比较结果基本上是一致的。
 * <p>
 * (3)16个读 14个写线程
 * 读基准测试方法设置@GroupThreads(16)，写基准测试方法设置@GroupThreads(4)，运行基准测试。
 * 1.20个线程(16个读线程，4个写线程)基准测试下的总体性能表现。
 * Optimistic > ReentrantLock > StampedLock > ReentrantRWLock
 * 2.20个线程(16个读线程，4个写线程)基准测试下的读性能表现。
 * Optimistic > ReentrantLock > ReentrantRWLock > StampedLock
 * 3.20个线程(16个读线程，4个写线程)基准测试下的写性能表现。
 * StampedLock > ReentrantLock > Optimistic > ReentrantRWLock
 * 总体排名似乎变化不大，但是通过基准测试数据我们不难看出，当读线程是写线程数量4倍的时候，读写锁表现出来的写性能与其他锁完全不在一个量级，读写锁写饥饿的问题也越发明显。
 * <p>
 * (4)19个读 1个写线程
 * 读基准测试方法设置@GroupThreads(19)，写基准测试方法设置@GroupThreads(1)，运行基准测试。
 * 1.20个线程(19个读线程，1个写线程)基准测试下的总体性能表现。
 * Optimistic > ReentrantLock > StampedLock > ReentrantRWLock
 * 2.20个线程(19个读线程，1个写线程)基准测试下的读性能表现。
 * Optimistic > ReentrantLock > ReentrantRWLock > StampedLock
 * 3.20个线程(19个读线程，1个写线程)基准测试下的写性能表现。
 * StampedLock > ReentrantLock > Optimistic > ReentrantRWLock
 * 当读写线程的比例为19:1的时候，读写锁的饥饿写问题越发严重，对共享资源的写操作吞吐量也变成了16000多次每秒，读写吞吐量的比例几乎成了228:1的比例，
 * 也就是说对资源每进行229次的操作，写线程只能抢到一次机会。
 * <p>
 * 3.9.3 StampedLock总结
 * StampedLock的引入并不是要横扫锁的世界成为“武林至尊”，它更多地是提供了一种乐观读的方式供我们选择，同时又解决了读写锁中“饥饿写”的问题。
 * 作为开发人员要能够根据应用程序的特点来判断应该怎样的锁进行贡献资源数据的同步，以确保数据的一致性，如果你无法明确地了解读写线程的分布情况，
 * 那么请使用ReentrantLock，因为通过本节所做的基准测试不能发现，它的表现始终非常稳定，无论是读线程还是写线程。如果你的应用程序中，
 * 读操作远远多于写操作，那么为了提供数据读取的并发量，StampedLock的乐观读将是一个不错的选择，同时它又不会引起饥饿写的问题。
 */
@Measurement(iterations = 20)
@Warmup(iterations = 20)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class StampedLockExample4 {

    @State(Scope.Group)
    public static class Test {
        private int x = 10;
        private final Lock lock = new ReentrantLock();
        private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private final Lock readLock = readWriteLock.readLock();
        private final Lock writeLock = readWriteLock.writeLock();
        private final StampedLock stampedLock = new StampedLock();

        public void stampedLockInc() {
            long stamped = stampedLock.writeLock();
            try {
                x++;
            } finally {
                stampedLock.unlockWrite(stamped);
            }
        }

        public int stampedReadLockGet() {
            long stamped = stampedLock.readLock();
            try {
                return x;
            } finally {
                stampedLock.unlockRead(stamped);
            }
        }

        public int stampedOptimisticReadLockGet() {
            long stamped = stampedLock.tryOptimisticRead();
            if (!stampedLock.validate(stamped)) {
                stamped = stampedLock.readLock();
                try {
                    return x;
                } finally {
                    stampedLock.unlockRead(stamped);
                }
            }
            return x;
        }

        public void lockInc() {
            lock.lock();
            try {
                x++;
            } finally {
                lock.unlock();
            }
        }

        public int lockGet() {
            lock.lock();
            try {
                return x;
            } finally {
                lock.unlock();
            }
        }

        public void writeLockInc() {
            writeLock.lock();
            try {
                x++;
            } finally {
                writeLock.unlock();
            }
        }

        public int readLockGet() {
            readLock.lock();
            try {
                return x;
            } finally {
                readLock.unlock();
            }
        }
    }

    @GroupThreads(5)
    @Group("lock")
    @Benchmark
    public void lockInc(Test test) {
        test.lockInc();
    }

    @GroupThreads(5)
    @Group("lock")
    @Benchmark
    public void lockGet(Test test, Blackhole blackhole) {
        blackhole.consume(test.lockGet());
    }

    @GroupThreads(5)
    @Group("rwlock")
    @Benchmark
    public void writeLockInc(Test test) {
        test.writeLockInc();
    }

    @GroupThreads(5)
    @Group("rwlock")
    @Benchmark
    public void readLockGet(Test test, Blackhole blackhole) {
        blackhole.consume(test.readLockGet());
    }

    @GroupThreads(5)
    @Group("stampedLock")
    @Benchmark
    public void writeStampedLockInc(Test test) {
        test.stampedLockInc();
    }

    @GroupThreads(5)
    @Group("stampedLock")
    @Benchmark
    public void readStampedReadLockGet(Test test, Blackhole blackhole) {
        blackhole.consume(test.stampedReadLockGet());
    }

    @GroupThreads(5)
    @Group("stampedOptimisticReadLockGet")
    @Benchmark
    public void writeStampedLockInc2(Test test) {
        test.stampedLockInc();
    }

    @GroupThreads(5)
    @Group("stampedOptimisticReadLockGet")
    @Benchmark
    public void readStampedReadLockGet2(Test test, Blackhole blackhole) {
        blackhole.consume(test.stampedOptimisticReadLockGet());
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(StampedLockExample4.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opts).run();
    }

}