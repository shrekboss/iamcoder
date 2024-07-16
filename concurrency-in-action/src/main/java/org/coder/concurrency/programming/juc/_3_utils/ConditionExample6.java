package org.coder.concurrency.programming.juc._3_utils;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * 那么我们应该如何进行优化呢？使用两个Condition对象，一个用于队列已满临界值条件的处理，另外一个用于队列为空的临界值条件的处理，
 * 这样一来，在生产者中唤醒的阻塞线程只能是消费者线程，在消费者中唤醒的也只能是生产者线程，下面是优化后的代码片段。
 * <p>
 * 采用两个Condition对象的方式就很好地解决了生产者线程除了唤醒消费者线程以外，还唤醒生产者线程而引起的无效线程上下文切换的情况，
 * 大家思考一下，使用传统的对象监视器(Object Monitor)的方式是不是很难这样优雅地解决这样的问题呢？
 * <p>
 * 3.8.4 Condition总结
 * Condition一经推出，就大规模地替代了传统对象监视器方式进行多个线程间的通信和数据交换，同时Condition又提供了更多的操作方法，比如用于线程监控等。
 * 相比对象监视器的方式，Condition更加高效，避免了很多无谓的线程上下文切换，从而提供了CPU的利用率。建议大家使用Condition的方式完全替代对象监视器的使用。
 * 由于Condition的卓越表现，除了广泛应用于开发中之外，JDK本身很多类的底层都是采用Condition来实现的。比如3.2节中已学到的CyclicBarrier，
 * 以及在第3章中将要学习到的阻塞队列，几乎都仰仗于Condition的突出表现才得以完成。
 */
public class ConditionExample6 {
    //定义显式锁
    private static final ReentrantLock lock = new ReentrantLock();
    //创建与显式锁Lock关联的Condition对象
    private static final Condition FULL_CONDITION = lock.newCondition();
    private static final Condition EMPTY_CONDITION = lock.newCondition();
    //定义long型数据的链表
    private static final LinkedList<Long> list = new LinkedList<>();
    //定义链表的最大容量为100
    private static final int CAPACITY = 100;
    //定义数据的初始值为0
    private static long i = 0;

    //生产者方法
    private static void produce() {
        //获取锁
        lock.lock();
        try {
            //当队列满了，生产者线程进入FULL_CONDITION wait队列中
            while (list.size() >= CAPACITY) {
                FULL_CONDITION.await();
            }
            //当链表中的数据量不足100时，生产新的数据
            i++;
            //将数据放到链表尾部
            list.addLast(i);
            System.out.println(Thread.currentThread().getName() + "->" + i);
            //生产者线程唤醒消费者线程
            EMPTY_CONDITION.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    //消费者方法
    private static void consume() {
        //获取锁
        lock.lock();
        try {
            //当队列为空，消费者线程进入EMPTY_CONDITION wait队列中
            while (list.isEmpty()) {
                EMPTY_CONDITION.await();
            }
            //消费数据
            Long value = list.removeFirst();
            System.out.println(Thread.currentThread().getName() + "->" + value);
            //消费者线程唤醒生产者线程
            FULL_CONDITION.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    private static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 启动10个生产者线程
        IntStream.range(0, 10).forEach(i -> new Thread(() -> {
            for (; ; ) {
                produce();
                sleep();
            }
        }, "Producer-" + i).start());
        // 启动5个生产者线程
        IntStream.range(0, 5).forEach(i -> new Thread(() -> {
            for (; ; ) {
                consume();
                sleep();
            }
        }, "Consumer-" + i).start());
    }

}