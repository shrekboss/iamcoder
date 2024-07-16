package org.coder.concurrency.programming.juc._3_utils;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3.8.2 Condition接口方法详解
 * 体验了Condition的基本用法之后，我们来看看Condition还提供了哪些方法，以及这些方法该如何使用。
 * 1.void await() throws InterruptedException：当前线程调用该方法会进入阻塞状态直到有其他线程对其进行唤醒，或者对当前线程执行中断操作。
 * 当线程执行了await()方法进入阻塞时；当前线程会被加入到阻塞队列中，并且释放对显式锁的持有，object monitor的wait()方法被执行后同样会加入一个虚拟的容器waitset(线程休息室)中，
 * waitset是一个虚拟的概念，JVM(虚拟机)规范并未强制要求其采用什么样的数据结构，Condition的wait队列则是由Java程序实现的FIFO队列。
 * 2.void awaitUninterruptibly()：该方法与await()方法类似，只不过该方法比较固执，它会忽略对它的中断操作，一直等待有其他线程将它唤醒。
 * 3.long awaitNanos(long nanosTimeout)throws InterruptedException：调用该方法同样会使得当前线程进入阻塞状态，
 * 但是可以设定阻塞的最大等待时间，如果在设定的时间内没有其他线程将它唤醒或者被中断操作，那么当前线程将会等到设定的纳秒时间后退出阻塞状态。
 * 4.boolean await(long time, TimeUnit unit) throws InterruptedException：执行方法awaitNanos()，如果到达设定的纳秒数则当前线程会退出阻塞，
 * 并且返回实际等待的纳秒数，但是程序很难判断线程是否被正常唤醒，因此该方法的作用除了可以指定等待的最大的单位时间，另外，还可以返回是在单位时间内被正常唤醒还是由于超时而退出的阻塞。
 * 5.boolean awaitUntil(Date deadline) throws InterruptedException：调用该方法同样会导致当前线程进入阻塞状态直到被唤醒、被中断或者到达指定的Date。
 * 6.void signal()：唤醒Condition阻塞队列中的一个线程，Condition的wait队列采用FIFO的方式，因此在wait队列中，第一个进入阻塞队列的线程将会被首先唤醒，下面我们来设计一个case对其进行测试。
 * 7.void signalAll()：唤醒Condition wait队列中的所有线程。
 * <p>
 * 针对Condition接口提供的方法，前文基本上已经做了比较细致的讲解，但是我们似乎遗漏了在显式锁ReentrantLock、ReentrantReadWriteLock中与Condition有关的方法，现在就来逐一解释一下。
 * 8.hasWaiters(Condition condition)：该方法的作用是查询是否有线程由于执行了await方法而进入了与condition关联的wait队列之中，若有线程在wait队列中则返回true，否则返回false。
 * 9.getWaitQueueLength(Condition condition)：该方法的作用是查询与condition关联的wait队列数量。
 * <p>
 * 通过下述代码片段的运行，相信大家应该能够正确使用显式锁Lock与Condition有关的这两个监控方法了，
 * 另外，笔者在进行互联网授课的过程中发现有些人对condition的await()方法以及signal()、signalAll()方法的使用也存在问题，
 * 使用await()、signal()、signalAll()方法或者同步代码块中一样，否则也会出现运行时异常。
 */
public class ConditionTest {

    @Test
    public void await() throws InterruptedException {
        //定义显式锁Lock
        final ReentrantLock lock = new ReentrantLock();
        //通过lock创建一个Condition并且与之关联
        final Condition condition = lock.newCondition();
        //创建匿名线程
        new Thread(() -> {
            //获取锁
            lock.lock();
            try {
                //一开始就直接调用await()方法，使得匿名线程进入wait队列进而阻塞
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
        //休眠2秒的时间，以确保匿名线程启动并且进入阻塞状态
        TimeUnit.SECONDS.sleep(2);
        //断言当前锁未被锁定，其他线程照样可以抢到该锁
        assert !lock.isLocked();
        //断言当前没有因为获取锁而被阻塞的线程
        assert !lock.hasQueuedThreads();
        //主线程正常获得该锁
        lock.lock();
        try {
            //断言有condition的waiter
            assert lock.hasWaiters(condition);
            //断言调用condition await 方法的waiter的数量为1
            assert lock.getWaitQueueLength(condition) == 1;
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void signal() throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        //启动10个线程，在每一个线程启动之后简单休眠1秒的时间，以确保线程是按照顺序启动的
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    condition.await();
                    System.out.println(Thread.currentThread().getName() + " is waked up.");
                    //断言第一个被唤醒的线程是首次执行了await方法而进入wait队列中的线程
                    assert "0".equals(Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
                //为线程命名
            }, String.valueOf(i)).start();
            //休眠1秒的时间以确保数字最小的线程最先启动
            TimeUnit.SECONDS.sleep(1);
        }

        TimeUnit.SECONDS.sleep(15);
        lock.lock();
        try {
            //唤醒wait队列中的一个线程
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void hasWaitersAndGetWaitQueueLength() throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        new Thread(() -> {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);
        //上面的代码片段中，启动的线程将立即执行condition的await方法进而进入wait队列中
        try {
            //为获取lock锁就直接执行hasWaiters方法将会抛出异常
            lock.hasWaiters(condition);
            //不允许程序执行到这里
            assert false : "should not process to here.";
        } catch (Exception e) {
            //断言抛出异常的类型
            assert e instanceof IllegalMonitorStateException;
        }
        try {
            //未获取lock锁就直接执行getWaitQueueLength方法将会抛出异常
            lock.getWaitQueueLength(condition);
            //不允许程序执行到这里
            assert false : "should not process to here.";
        } catch (Exception e) {
            //断言抛出异常的类型
            assert e instanceof IllegalMonitorStateException;
        }
        //获取lock锁
        lock.lock();
        try {
            //调用相关的方法将不会出现错误
            assert lock.hasWaiters(condition);
            assert lock.getWaitQueueLength(condition) == 1;
        } finally {
            lock.unlock();
        }
    }
}