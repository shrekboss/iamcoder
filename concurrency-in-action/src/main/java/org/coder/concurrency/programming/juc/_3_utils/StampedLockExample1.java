package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.locks.StampedLock;

/**
 * 3.9.2 StampedLock的使用
 * StampedLock被JDK1.8版本引入之后，成为了Lock家族的新宠，它几乎具备了ReentrantLock、ReentrantReadWriteLock这两种类型锁的所有功能(性能表现要看不同的使用场景)，
 * 因此本节中列举的使用示例将主要针对StampedLock如何替代前两者来展开。
 * 1.替代ReentrantLock
 * 在ReentrantLock锁中不存在读写分离锁，因此下面代码示例中的读写方法都是使用lock.writeLock()方法进行锁的获取，
 * 该方法会返回一个数据戳，在稍后的锁释放过程中需要用到该数据戳(stamp)。
 */
public class StampedLockExample1 {
    //共享数据
    private static int shareData = 0;
    //定义StampedLock锁
    private static final StampedLock lock = new StampedLock();

    public static void inc() {
        //调用WriteLock方法返回一个数据stamp
        long stamp = lock.writeLock();
        try {
            //修改共享数据
            shareData++;
        } finally {
            //释放锁
            lock.unlockWrite(stamp);
        }
    }

    public static int get() {
        //获取锁并记录数据戳
        long stamp = lock.writeLock();
        try {
            //返回数据
            return shareData;
        } finally {
            //释放锁
            lock.unlockWrite(stamp);
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}