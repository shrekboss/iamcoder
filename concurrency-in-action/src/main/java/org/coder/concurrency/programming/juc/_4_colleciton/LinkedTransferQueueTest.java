package org.coder.concurrency.programming.juc._4_colleciton;

import org.junit.Test;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * 4.2.7 LinkedTransferQueue
 * TransferQueue是一个继承了BlockingQueue的接口，并且增加了若干新的方法。
 * LinkedTransferQueue是TransferQueue接口的实现类，其定义为一个无界的队列，具有FIFO的特性。
 * <p>
 * 继承自BlockingQueue的方法在使用方法上与本节中学过的其他BlockingQueue并没有太大的区别(SynchronousQueue除外)，
 * 因此我们只介绍继承自TransferQueue的方法，看看TransferQueue为其赋予了怎样的新特性。
 * 1.transfer方法
 * 当某个线程执行了transfer方法后将会进入阻塞，直到有其他线程对transfer的数据元素进行了poll或者take，否则当前线程会将该数据元素插入队列尾部，并且等待其他线程对其进行消费。
 * 这段文字描述包含了一些非常苛刻的要求，首先，LinkedTransferQueue是一个队列，是可以存放无限(Integer.MAX_VALUE)数据元素的队列，因此允许同时有多个线程将数据元素插入队列尾部；
 * 其次当线程A通过transfer将元素E插入队列尾部时，即使此时此刻有其他线程也对该队列进行着消费操作，如果元素E未被消费，那么线程A同样也会进入阻塞直到元素E被其他线程消费。
 * 下面看一个简单的代码片段了解一下transfer方法的特性。
 * <p>
 * 下面程序的运行与我们在代码注释中的分析完全一致，这就是transfer方法的主要特性，非常类似于SynchronousQueue的put方法，
 * 但是不同于SynchronousQueue的地方在于LinkedTransferQueue存在容量，允许无限多个数据元素的插入，而前者则不支持。
 * <p>
 * 2.tryTransfer方法
 * 与transfer方法不同的是，tryTransfer方法并不会使得执行线程进入阻塞，如果当前并没有线程等待对元素E的消费(poll或者take)，
 * 那么执行tryTransfer方法会立即放回失败，并且元素E也不会插入队列的尾部(transfer不成功)，否则返回成功。
 * <p>
 * tryTransfer还有一个重载方法，支持最大超时时间的设定，在设定的最大超时时间内，如果没有其他线程对transfer的数据元素进行消费，
 * 那么元素E将不会被插入队列尾部，并且退出阻塞，如果在单位时间内有其他线程消费transfer的元素数据，则返回成功并退出阻塞。
 * <p>
 * 3.其他monitor方法
 * 在TransferQueue中还提供了两个与monitor相关的方法，主要用于获取当前是否有消费者线程在等待消费TransferQueue中的数据。
 * <p>
 * 有关LinkedTransferQueue的使用就介绍这么多了，对比前文中所介绍过的其他阻塞队列，
 * LinkedTransferQueue更像是一个集成了LinkedBlockingQueue和SynchronousQueue特性的阻塞队列，
 * 它们所具备的特点在LinkedTransferQueue中都可以得到体现，通过学习我们实际上可以看出，
 * LinkedTransferQueue相比较于SynchronousQueue可以存储更多的元素数据，
 * 在支持LinkedBlockingQueue所有方法的同时又有比它更好的性能表现，因为在LinkedTransferQueue中没有使用到锁，
 * 同步操作均是有CAS算法和LockSupport提供的。
 * <p>
 * 4.2.8 BlockingQueue总结
 * 本节学习了7钟BlockingQueue，每一种BlockingQueue都是线程安全的队列，非常适合应用于高并发多线程的应用程序中，
 * 虽然每一种阻塞队列在使用和实现上都有各自不同的特点，但是它们也存在着诸多的共性(7种BlockingQueue之间的关系如图4-13所示)。
 * 比如它们都有阻塞式的操作方法；它们都存在边界的概念(不管是有边界、无边界还是可选边界)；它们都只允许非空的元素数据存入等。
 * 在使用中，程序开发者需要根据它们所具备的特性做出正确的选择，在合理的地方解决与之对应的问题。
 */
public class LinkedTransferQueueTest {

    @Test
    public void transfer() throws InterruptedException {
        //定义LinkedTransferQueue
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
        //通过不同的方法在队列尾部插入三个数据元素
        queue.add("hello");
        queue.offer("world");
        queue.put("java");
        //此时该队列的数据元素为（队尾）Java->world->hello
        new Thread(() -> {
            try {
                //创建匿名线程，并且执行transfer方法
                queue.transfer("Alex");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("current thread exit.");
        }).start();
        //此刻队列的数据元素为（队尾）Alex->Java->world->hello
        TimeUnit.SECONDS.sleep(2);
        //执行take方法从队列头部移除消费元素hello，但是匿名线程仍旧被阻塞
        System.out.println(queue.take());
        //在队尾插入新的数据元素（队尾）Scala->Alex->Java->world
        queue.put("Scala");
        //执行poll方法从队列头部移除消费元素world，匿名线程继续被阻塞
        System.out.println(queue.poll());
        //执行take方法从队列头部移除消费元素Java，匿名线程继续阻塞中
        System.out.println(queue.take());
        //执行take方法从队列头部移除消费元素Java，匿名线程退出阻塞
        System.out.println(queue.take());
    }

    @Test
    public void tryTransfer() throws InterruptedException {
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
        queue.add("hello");
        queue.offer("world");
        new Thread(() -> {
            //立即返回false
            assert !queue.tryTransfer("Alex");
            System.out.println("current thread exit.");
        }).start();

        TimeUnit.SECONDS.sleep(2);
        //Alex并未插入至队尾
        assert queue.size() == 2;
    }

    @Test
    public void tryTransfer2() throws InterruptedException {
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
        queue.add("hello");
        queue.offer("world");
        new Thread(() -> {
            try {
                //在单位时间(3秒)如果有其他线程对Alex进行消费，则退出阻塞，成功插入队尾
                assert queue.tryTransfer("Alex", 3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("current thread exit.");
        }).start();

        TimeUnit.SECONDS.sleep(2);
        assert queue.take().equals("hello");
        assert queue.take().equals("world");
        //主线程成功消费数据元素Alex
        assert queue.take().equals("Alex");
    }

    @Test
    public void monitor() throws InterruptedException {
        //启动三个线程消费queue中的元素（从头开始）
        LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    System.out.println(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + " consume data over.");
            }).start();
        }
        //休眠1秒，确保3个线程均已启动且阻塞
        TimeUnit.SECONDS.sleep(1);
        //断言正在等待消费的线程以及数量
        assert queue.hasWaitingConsumer();
        assert queue.getWaitingConsumerCount() == 3;
        //插入一条数据至队列
        queue.offer("test");
        assert queue.hasWaitingConsumer();
        assert queue.getWaitingConsumerCount() == 2;
    }
}