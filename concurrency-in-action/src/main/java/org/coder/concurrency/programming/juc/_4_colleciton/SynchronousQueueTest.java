package org.coder.concurrency.programming.juc._4_colleciton;

import java.util.concurrent.SynchronousQueue;
import java.util.stream.IntStream;

/**
 * 4.2.5 SynchronousQueue
 * SynchronousQueue也是实现自BlockingQueue的一个阻塞队列，每一次对其的写入操作必须等待(阻塞)其他线程进行对应的移除操作，
 * SynchronousQueue的内部并不会涉及容量、获取size，就连peek方法的返回值永远都将会是null，除此之外还有更多的方法在
 * SynchronousQueue中也都未提供对应的支持(列举如下)，因此在使用的过程中需要引起注意，否则会使得程序的运行出现不符合预期的错误。
 * 1.clear()：清空队列的方法在SynchronousQueue中不起作用。
 * 2.contains(Object o)：永远返回false。
 * 3.containsAll(Collection<?> c)：等价于c是否为空的判断。
 * 4.isEmpty()：永远返回true。
 * 5.iterator()：返回一个空的迭代器。
 * 6.peek()：永远返回null。
 * 7.remainingCapacity()：始终返回0。
 * 8.remove(Object o)：不做任何删除，并且始终返回false。
 * 9.removeAll(Collection<?> c)：不做任何删除，始终返回false。
 * 10.retainAll(Collection<?> c)：始终返回false。
 * 11.size()：返回值始终为0。
 * 12.spliterator()：返回一个空的Spliterator。
 * 13.toArray()及toArray(T[] a)方法同样也不支持。
 * <p>
 * 看起来好多方法在SynchronousQueue中都不提供对应的支持，那么SynchronousQueue是一个怎样的队列呢?
 * 简单来说，我们可以借助于SynchronousQueue在两个线程间进行线程安全的数据交换，这一点比较类似于3.3节“Exchanger工具详解”中介绍的Exchanger工具类。
 * <p>
 * 尽管SynchronousQueue是一个队列，但是它的主要作用在于在两个线程之间进行数据交换，区别于Exchanger的主要地方在于(站在使用的角度)SynchronousQueue所涉及的一对线程
 * 一个更加专注于数据的生产，另一个更加专注于数据的消费(各司其职)，而Exchanger则更加强调一对线程数据的交换。打开Exchanger的官方文档，可以看到如下的一句话：
 * An Exchanger may be viewed as a bidirectional form of a {@link SynchronousQueue}.
 * Exchanger可以看作一个双向的SynchronousQueue。
 * <p>
 * SynchronousQueue在日常的开发使用中并不是很常见，即使在JDK内部，该队列也仅用于ExecutorService中的Cache Thread Pool创建(第五章会接触到相关知识)，
 * 本节只是简单了解一下SynchronousQueue的基本使用方法即可。
 */
public class SynchronousQueueTest {

    public static void main(String[] args) {
        // 定义String类型的SynchronousQueue
        SynchronousQueue<String> queue = new SynchronousQueue<>();
        //启动两个线程，向queue中写入数据
        IntStream.rangeClosed(0, 1).forEach(i -> new Thread(() -> {
            try {
                //若没有对应的数据消费线程，则put方法将会导致当前线程进入阻塞
                queue.put(Thread.currentThread().getName());
                System.out.println(Thread.currentThread() + " put element " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start());
        //启动两个线程从queue中消费数据
        IntStream.rangeClosed(0, 1).forEach(i -> new Thread(() -> {
            try {
                //若没有对应的数据生产线程，则take方法将会导致当前线程进入阻塞
                String value = queue.take();
                System.out.println(Thread.currentThread() + " take " + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start());
    }

}