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
 * 3.多线程下读写操作性能对比
 * 虽然在单线程下，synchronized关键字的表现要远远优于显式锁Lock，但是在多线程的情况下，显式锁Lock的优势就体现出来了，
 * 当然，不同的环境、不同的JDK版本，测试效果可能会存在差异，在本节中，我们将针对共享资源的并发读写操作进行基准测试，
 * 以对比显式锁Lock和synchronized关键字的性能表现。
 * <p>
 * 同样保持了10个数量的线程，其中5个线程并发地去修改共享资源x，5个线程并发地去读取共享资源x，
 * 运行上面的基准测试，不难发现显式锁Lock的表现仍旧比关键字synchronized要好。
 * <p>
 * 同样将基准测试结果做成图标的形式，大家可以直观地感受到它们之间性能的差异。
 * <p>
 * 3.6.4 显式锁Lock总结
 * 本节学习了显式锁Lock接口以及该接口最常用的一个实现ReentrantLock方法的使用，显式锁Lock接口自JDK1.5版本引入以来非常受欢迎，
 * 在绝大多数情况下完全可以替代synchronized关键字进行共享资源的同步和数据一致性的保护。
 * <p>
 * 由于显式锁Lock将锁的控制权完全交给了程序员自己，因此在锁的使用过程中需要非常慎重，如果使用错误或者不得当将会引起比较严重的后果，
 * 本节也对不同的场景进行了讨论和分析。
 * <p>
 * 最后，我们使用基准测试工具JMH对synchronized关键字和显式锁Lock在不同场合下的性能表现进行了对比，
 * 通过对比我们可以发现，在多线程的情况下显式锁的表现要优于关键字synchronized，除了性能上的优越表现之外，
 * 显式锁Lock具备更加灵活和丰富的API。
 */
//基准测试的设定，10批次Warmup，10批次Measurement
@Measurement(iterations = 10)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ReentrantLockExample6 {

    @State(Scope.Group)
    public static class Test {
        private int x = 10;
        private final Lock lock = new ReentrantLock();

        //基准方法
        public int baseGet() {
            return x;
        }

        public void baseInc() {
            x++;
        }

        //使用lock进行方法同步
        public int lockGet() {
            lock.lock();
            try {
                return x;
            } finally {
                lock.unlock();
            }
        }

        public void lockInc() {
            lock.lock();
            try {
                x++;
            } finally {
                lock.unlock();
            }
        }

        //使用关键字synchronized进行方法同步
        public int syncGet() {
            synchronized (this) {
                return x;
            }
        }

        public void syncInc() {
            synchronized (this) {
                x++;
            }
        }
    }

    //5个线程进行测试
    @GroupThreads(5)
    @Group("base")
    @Benchmark
    public void baseGet(Test test, Blackhole hole) {
        hole.consume(test.baseGet());
    }

    @GroupThreads(5)
    @Group("base")
    @Benchmark
    public void baseInc(Test test) {
        test.baseInc();
    }

    //5个线程进行测试
    @GroupThreads(5)
    @Group("lock")
    @Benchmark
    public void testLockGet(Test test, Blackhole hole) {
        hole.consume(test.lockGet());
    }

    @GroupThreads(5)
    @Group("lock")
    @Benchmark
    public void testLockInc(Test test, Blackhole hole) {
        test.lockInc();
    }

    //5个线程进行测试
    @GroupThreads(5)
    @Group("sync")
    @Benchmark
    public void testSyncGet(Test test, Blackhole hole) {
        hole.consume(test.syncGet());
    }

    @GroupThreads(5)
    @Group("sync")
    @Benchmark
    public void testSyncInc(Test test, Blackhole hole) {
        test.syncInc();
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(ReentrantLockExample6.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opts).run();
    }

}