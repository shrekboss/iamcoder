package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 3.9 StampedLock详解
 * 3.6节学习了Lock接口以及ReentrantLock的使用，相比较传统的同步方式synchronized关键字，ReentrantLock除了具备synchronized关键字所有的功能和语义之外，
 * 还提供了更好的灵活性和可扩展性以及对锁的监控方法等，从基准测试的结果来看，在多线程高并发的情况下，ReentrantLock的性能表现也要优于synchronized关键字，
 * 因此大多数情况下使用前者替换后者并没有太大的问题。
 * 抛开性能问题不说，3.7节中学习到的读写分离锁ReentrantReadWriteLock提供了很好的思路，旨在提高多线程同时读的并发处理速度，
 * 因为在某个时刻，如果所有线程对共享资源都是读操作，那么锁的排他性就显得没有意义了。比如在下面的代码片段中，虽然匿名线程获取了读锁并且进入了长时间的休眠，
 * 但是这并不影响其他线程对读锁的获取。
 * <p>
 * 3.9.1 读写锁的饥饿写问题
 * 我们曾经在3.7节中进行过基准测试，发现读写锁的性能并不是最佳的，当然更有甚者，如果对读写锁使用不得当，则还会引起饥饿写的情况发生，那么什么是饥饿写呢？
 * 所谓的饥饿写是指在使用读写锁的时候，读线程的数量远远大于写线程的数量，导致锁长期被读线程霸占，写线程无法获得对数据进行写操作的权限从而进入饥饿的状态
 * (当然可以在构造读写锁时指定其为公平锁，读写线程获得执行权限得到的机会相对公平，但是当读线程大于写线程时，性能效率会比较低下)。因此在使用读写锁进行
 * 数据一致性保护时请务必做好线程数量的评估(包括线程操作的任务类型)。
 * 针对这样的问题，JDK1.8版本引入了StampedLock，该锁有一个long型的数据戳(stamp)和三种模型构成，当获取锁的时候会返回一个long型的数据戳(stamp)，
 * 该数据戳将被用于进行稍后的锁释放参数。如果返回的数据戳为0，则表示获取锁失败，同时StampedLock还提供 了一种乐观读的操作方式，稍后会有相关的示例。
 * <p>
 * 需要注意的一点是，StampedLock是不可重入的，不像前文中介绍的两种锁类型(ReentrantLock、ReentrantReadWriteLock)都有hold计数器，
 * 每一次对StampedLock锁的获取都会生成一个数据戳，即使当前线程在获得了该锁的情况下再次获取也会返回一个全新的数据戳，因此如果使用不当则会出现死锁的问题。
 */
public class StampedLockTest {

    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        final Lock readLock = readWriteLock.readLock();
        new Thread(() -> {
            readLock.lock();
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }
        }).start();
        TimeUnit.SECONDS.sleep(1);
        readLock.lock();
        assert readWriteLock.getReadLockCount() == 2;
        System.out.println("main thread can hold the read lock still.");
        readLock.unlock();
    }

}