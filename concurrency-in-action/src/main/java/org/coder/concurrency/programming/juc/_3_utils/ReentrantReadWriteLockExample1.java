package org.coder.concurrency.programming.juc._3_utils;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 3.7 ReadWriteLock&ReentrantReadWriteLock详解
 * 对共享资源的访问一般包括两种类型的动作，读和写(修改、删除等会引起资源发生变化的动作)，
 * 当多线程同时对某个共享资源进行读取操作时，并不会引起共享资源数据不一致情况的发生（如表3-2所示），
 * 因此这个时候如果仍旧让资源的访问互斥，就显得有些不合情理了，
 * Doug Lea在JDK1.5版本引入了读写锁类，旨在允许某个特定时刻多线程并发读取共享资源，提高系统性能和访问吞吐量。
 * 表3-2 多线程操作共享资源冲突表
 * ——————————————————————————
 * 线程	|	读		|	写
 * ——————————————————————————
 * 读	|	不冲突	|	冲突
 * ——————————————————————————
 * 写	|	冲突		|	冲突
 * ——————————————————————————
 * 在笔者的《Java高并发编程详解：多线程与架构设计》一书中第17章“读写锁分离设计模式”介绍了读写锁的实现原理和设计技巧，
 * 读写锁的分离可以提供多线程同时进行读操作时应用程序的性能。
 * <p>
 * 3.7.1 读写锁的基本使用方法
 * 与ReentrantLock一样，ReentrantReadWriteLock的使用方法也是非常简单的，只不过在使用的过程中需要分别派生出“读锁”和“写锁”，
 * 在进行共享资源读取操作时，需要使用读锁进行数据同步，在对共享资源进行写操作时，需要使用写锁进行数据一致性的保护，下面的示例代码是对读写锁的简单应用。
 * <p>
 * 1.在下述代码中，首先创建了一个ReentrantReadWriteLock锁，然后根据该锁分别创建了读锁和写锁。
 * 2.读锁和写锁都是Lock接口的实现，因此具有Lock接口所定义的所有方法，比如lock()、unlock()等方法。
 * 3.若某个线程获取了写锁进行数据写操作，那么此时其他线程对共享资源的读写操作都会被阻塞直到锁被释放。
 * 4.若某个线程获取了读锁进行数据读操作，那么此时其他线程对共享资源的写操作会进入阻塞直到锁被释放，但如果是其他线程对共享资源进行读操作则不会被阻塞。
 * <p>
 * 3.7.2 读写锁的方法
 * ReadWriteLock接口只有两个方法用于创建读锁和写锁，ReentrantReadWriteLock实现自ReadWriteLock接口并且提供了一些ReadWriteLock监控查询方法。
 * <p>
 * ReentrantReadWriteLock、ReadLock以及WriteLock的方法与3.6节中介绍的方法非常类似，为了节约篇幅，本节将不会对每一个方法逐一进行解释，读者可以参考3.6节中的内容。
 */
public class ReentrantReadWriteLockExample1 {
    //定义ReadWriteLock锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    //创建读锁
    private final Lock readLock = readWriteLock.readLock();
    //创建写锁
    private final Lock writeLock = readWriteLock.writeLock();
    //共享数据
    private final LinkedList<String> list = new LinkedList<>();

    //使用写锁进行数据同步
    public void add(String element) {
        writeLock.lock();
        try {
            list.addLast(element);
        } finally {
            writeLock.unlock();
        }
    }

    //使用写锁进行数据同步
    public String take() {
        writeLock.lock();
        try {
            return list.removeFirst();
        } finally {

        }
    }

    //使用读锁进行数据同步
    public String get(int index) {
        readLock.lock();
        try {
            return list.get(index);
        } finally {
            readLock.unlock();
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}