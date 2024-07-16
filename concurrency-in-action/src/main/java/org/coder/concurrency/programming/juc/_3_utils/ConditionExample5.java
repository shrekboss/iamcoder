package org.coder.concurrency.programming.juc._3_utils;

import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * 3.8.3 使用Condition之生产者消费者
 * <p>
 * 无论是Condition还是对象监视器，在进行await()、wait()或者notify()、signal()等方法调用的时候，主要是针对临界值的判断而发出的，
 * 比如数据有没有被消费，队列是否为空、是否已满等。虽然我们在不同的书籍和资料中阅读了太多的生产者消费者内容，但是不得不说线程间的通信生产者消费者模式是最好、最常见的场景之一，
 * 因此在本书中我们也不能免俗地使用Condition实现多线程的生产者消费者场景，以加深读者对Condition的理解。
 * <p>
 * 运行上面的程序，会发现生产者和消费者线程在交替地运行，进行数据的生产与消费。
 * <p>
 * 下面的程序虽然能够正常运行，但是仍然存在一些不足之处，比如在注释①②处，此刻的唤醒动作唤醒的是与Condition关联的阻塞队列中的所有阻塞线程。
 * 由于我们使用的是唯一的一个Condition实例，因此生产者唤醒的有可能是与Condition关联的wait队列中生产者线程，
 * 假设当生产者线程被唤醒后抢到了CPU的调度获得执行权，但是又发现队列已满再次进行阻塞。这样的线程上下文开销实际上是没有意义的，
 * 甚至会影响性能(多线程下的线程上下文开销其实是一个非常的大的性能损耗，一般针对高并发程序的调优就是在减少上下文切换发生的概率)。
 */
public class ConditionExample5 {
    //定义显式锁
    private static final ReentrantLock lock = new ReentrantLock();
    //创建与显式锁Lock关联的Condition对象
    private static final Condition condition = lock.newCondition();
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
            //链表数据大于等于100为一个临界值，当list中的数据量达到100时，生产者线程将被阻塞加入与condition关联的wait队列中
            while (list.size() >= CAPACITY) {
                condition.await();
            }
            //当链表中的数据量不足100时，生产新的数据
            i++;
            //将数据放到链表尾部
            list.addLast(i);
            System.out.println(Thread.currentThread().getName() + "->" + i);
            //①通知其他线程
            condition.signalAll();
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
            //链表为空是另外一个临界值，当list中的数据为空时，消费者线程将被阻塞加入与condition关联的wait队列中
            while (list.isEmpty()) {
                condition.await();
            }
            //消费数据
            Long value = list.removeFirst();
            System.out.println(Thread.currentThread().getName() + "->" + value);
            //②通过其他线程
            condition.signalAll();
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