package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.locks.StampedLock;

/**
 * 2.替代ReentrantReadWriteLock
 * 与ReentrantReadWriteLock锁一样，StampedLock也提供了读锁和写锁这两种模式，因此StampedLock天生就支持读写分离锁的使用方式，
 * 下面的示例代码只是在Example1的基础上对get()方法稍作修改即可完成读写锁的实现方式。
 * <p>
 * 使用StampedLock锁不需要额外创建出不同类型的Lock(ReadLock或WriteLock)就可以很轻易地完成读写锁的分离，提高并发情况下的数据读取性能。
 */
public class StampedLockExample2 {

    private static int shareData = 0;
    private static final StampedLock lock = new StampedLock();

    public static void inc() {
        long stamp = lock.writeLock();
        try {
            shareData++;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public static int get() {
        //获取读锁，并且记录数据戳stamp
        long stamp = lock.readLock();
        try {
            return shareData;
        } finally {
            //使用stamp释放读锁
            lock.unlockRead(stamp);
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}