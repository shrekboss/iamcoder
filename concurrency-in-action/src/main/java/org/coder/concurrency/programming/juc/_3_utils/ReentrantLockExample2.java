package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * (2)避免锁的交叉使用引起死锁
 * 在笔者的《Java高并发编程详解：多线程与架构设计》一书的4.3.3节中介绍了交叉使用关键字synchronized可能会引起死锁的情况发生，
 * 同样，在使用lock锁的时候也会出现类似的情况，示例代码如下所示。
 * <p>
 * 运行下面的程序会出现死锁的问题，当死锁出现的时候，JVM进程是正常运行的，但是工作线程会因为进入阻塞而不能继续工作。
 * <p>
 * 我们可以借助于JVM工具诊断到死锁的情况。
 */
public class ReentrantLockExample2 {
    //分别定义两个lock
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();

    private static void m1() {
        lock1.lock();
        System.out.println(Thread.currentThread() + " get lock1.");
        try {
            lock2.lock();
            System.out.println(Thread.currentThread() + "get lock2.");
            try {
                //...
            } finally {
                lock2.unlock();
                System.out.println(Thread.currentThread() + " release lock2.");
            }
        } finally {
            lock1.unlock();
            System.out.println(Thread.currentThread() + " release lock1.");
        }
    }

    private static void m2() {
        lock2.lock();
        System.out.println(Thread.currentThread() + " get lock2.");
        try {
            lock1.lock();
            System.out.println(Thread.currentThread() + "get lock1.");
            try {
                //...
            } finally {
                lock1.unlock();
                System.out.println(Thread.currentThread() + " release lock1.");
            }
        } finally {
            lock2.unlock();
            System.out.println(Thread.currentThread() + " release lock2.");
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                m1();
            }
        }).start();
        new Thread(() -> {
            while (true) {
                m2();
            }
        }).start();
    }

}