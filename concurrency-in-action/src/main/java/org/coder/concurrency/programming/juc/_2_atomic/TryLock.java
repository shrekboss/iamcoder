package org.coder.concurrency.programming.juc._2_atomic;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 2.2.3 Try Lock显式锁的实现
 * 在《Java高并发编程详解：多线程与架构设计》一书的第4章和第5章两个章节中，我们分别详细介绍了synchronized关键字的使用以及synchronized关键字存在的缺陷，
 * 其中，当某个线程在争抢对象监视器(object monitor)的时候将会进入阻塞状态，并且是无法被中断的，也就是说synchronized关键字并未提供一种获取monitor锁失败的通知机制，
 * 执行线程只能等待其他线程释放该monitor的锁进而得到一次机会，本节将借助于AtomicBoolean实现一个可立即返回并且退出阻塞的显式锁Lock。
 * <p>
 * 下述代码虽然非常简短，但是其借助于AtomicBoolean的原子性布尔值更新操作的方法compareAndSet可以以Lock Free的方式进行方法同步操作。
 * 下面就来简单分析一下操作的过程。
 * 1.在注释①处，我们定义了一个AtomicBoolean类型的属性ab，其初始值为false，表明当前的锁未被任何线程获得，也就是说某线程可以成功获得对该锁的持有。
 * 2.在注释②处，我们定义了一个ThreadLocal<Boolean>，并且重写其初始化方法返回false，该ThreadLocal的使用在TryLock中非常关键，
 * 我们都知道显式锁为了确保锁能够被正确地释放，一般会借助于try..finally语句块以确保release方法能够被执行，因此为了防止某些未能成功获取锁的线程在执行release方法的时候改变ab的值，
 * 我们需要借助于ThreadLocal<Boolean>中的数据副本进行标记和判断。
 * 3.在注释③处，我们使用AtomicBoolean的compareAndersonSet方法对ab当前的布尔值进行CAS操作，当预期值与ab当前值一致时操作才能成功，否则操作将直接失败，
 * 因此执行该方法的线程不会进入阻塞，这一点很关键。
 * 4.如果某线程成功执行了对ab当前布尔值的修改，那么我们需要将其在(注释④处)ThreadLocal<Boolean>关联的数据副本标记为true，以标明当前线程成功获取了对TryLock的持有。
 * 5.release方法需要秉承一个原则，那就是只有成功获得该锁的线程才有资格对其进行释放，反映到我们的代码中就是执行对ab当前值布尔值的更新动作，详见注释⑤。
 * 6.在注释⑥处确认当前有资格进行锁的释放以后，就可以对ab当前布尔值进行更新操作了，并且标记当前线程以将锁释放。
 * <p>
 * 完成了TryLock代码的开发及详细分析之后，我们就需要使用它了，并且能够验证在同一时刻是否只有一个线程才能成功获得TryLock显式锁。
 */
public class TryLock {
    //①在TryLock内部，我们借助于AtomicBoolean的布尔原子性操作方法
    //因此需要先定义一个AtomicBoolean并且使其初始值为false
    private final AtomicBoolean ab = new AtomicBoolean(false);
    //②线程保险箱，用于存放与线程上下文关联的数据副本
    private final ThreadLocal<Boolean> threadLocal = ThreadLocal.withInitial(() -> false);

    //可立即返回的lock方法
    public boolean tryLock() {
        //③借助于AtomicBoolean的CAS操作对布尔值进行修改
        boolean result = ab.compareAndSet(false, true);
        if (result) {
            //④当修改成功时，同步更新threadLocal的数据副本值
            threadLocal.set(true);
        }
        return result;
    }

    //锁的释放
    public boolean release() {
        //⑤判断调用release方法的线程是否成功获得了该锁
        if (threadLocal.get()) {
            //⑥标记锁被释放，并且原子性地修改布尔值为false
            threadLocal.set(false);
            return ab.compareAndSet(true, false);
        } else {
            //直接返回
            return false;
        }
    }
}