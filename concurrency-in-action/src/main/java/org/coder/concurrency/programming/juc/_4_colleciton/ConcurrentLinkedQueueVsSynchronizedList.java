package org.coder.concurrency.programming.juc._4_colleciton;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 4.3 ConcurrentQueue(并发队列)
 * 4.2节中详细地介绍了7种类型的阻塞队列(当然阻塞队列的实现远远不止这些，比如在Google Guava、Akka、Actor等第三方类库中也提供了阻塞队列的不同实现，如果有需要甚至还要自行实现)。
 * 虽然每一种阻塞队列都有各自的特性和实现方式，但是它们解决的问题主要是，当队列达到某临界值时，与之对应的线程被挂起以等待其他线程的唤醒通知，对于这样的场景我们在日常的程序开发中经常使用到，
 * 其有一个非常专业和学术的叫法，即生产者消费者模型(模式)。
 * <p>
 * 在绝大多数的BlockingQueue中，为了保护共享数据的一致性，需要对共享数据的操作进行加锁处理(显式锁或者synchronized关键字)，
 * 为了使得操作线程挂起和被唤醒，我们需要借助于对象监视器的wait/notify/notifyAll或者与显式锁关联的Condition。
 * <p>
 * 那么，在Java中有没有一种队列的实现方式可以不用关心临界值的判断，操作该队列的线程也不会被挂起并且等待被其他线程唤醒，
 * 我们只是单纯地向该队列插入或者获取数据，并且该队列是线程安全的，是可以应用于高并发多线程的场景中呢？
 * 在JDK1.5版本以前要实现这些要求我们大致有两种方式，具体如下。
 * 1).通过synchronized关键字对非线程安全的队列或者链表的操作方法进行同步。
 * 2).使用Collections类的同步方法。
 * 其实第二种方式非常类似于第一种方式，随便打开一个同步方法，源码如下。
 * ...省略
 * //Collections类的部分源码
 * public static <T> List<T> synchronizedList(List<T> list) {
 * return (list instanceof RandomAccess ? new SynchronizedRandomAccessList<>(list) : new SynchronizedList<>(list));
 * }
 * ...省略
 * SynchronizedList(List<E> list) {
 * super(list);
 * this.list = list;
 * }
 * SynchronizedList(List<E> list, Object mutex) {
 * super(list, mutex);
 * this.list = list;
 * }
 * public boolean equals(Object o) {
 * if(this == o)
 * return true;
 * synchronized (mutex) {
 * return list.equals(o);
 * }
 * }
 * public int hashCode() {
 * synchronized (mutex) {
 * return list.hashCode();
 * }
 * }
 * public E get(int index) {
 * synchronized (mutex) {
 * return list.get(index);
 * }
 * }
 * public E set(int index, E element) {
 * synchronized (mutex) {
 * return list.set(index, element);
 * }
 * }
 * public void add(int index, E element) {
 * synchronized (mutex) {
 * list.add(index, element);
 * }
 * }
 * public E remove(int index) {
 * synchronized (mutex) {
 * return list.remove(index);
 * }
 * }
 * ...省略
 * 这种方式虽然可以确保Collection在多线程环境下的线程安全性，但是synchronized关键字相对于显式锁Lock甚至无锁的实现方式来说效率低下，
 * 因此自JDK1.5版本后，Java的开发者们实现了无锁的且线程安全的并发队列实现方案，开发者可以直接借助于它们开发出高性能的应用程序
 * (在本章的最后，笔者也给出了一个简单的无锁数据结构实现，同样适用于高并发多线程的环境之中)。
 * 1.ConcurrentLinkedQueue：无锁的、线程安全的、性能高效的、基于链表结构实现的FIFO单向队列(在JDK1.5版本中被引入)。
 * 2.ConcurrentLinkedDeque：无锁的、线程安全的、性能高效的、基于链表结构实现的双向队列(在JDK1.7版本中被引入)。
 * <p>
 * ConcurrentLinkedQueue和ConcurrentLinkedDeque的使用都是比较简单的，
 * 为了节约篇幅，本节不会对每一种方法都展开详细的介绍，读者可以通过阅读JDK帮助文档获得帮助。
 * <p>
 * 4.3.1 并发队列的性能
 * 并发队列由于采用了无锁算法的实现方式，因此在多线程高并发的场景中其性能表现将会足够优异，下面通过一个简单的基准测试对其性能进行一下对比。
 * <p>
 * 运行上面的基准测试，在10个线程同时读写的情况下(5个线程向队列尾部插入数据，5个线程从队列头部读取数据)，ConcurrentLinkedQueue的性能明显要出色很多。
 * Benchmark                                                                    Mode  Cnt  Score   Error  Units
 * ConcurrentLinkedQueueVsSynchronizedList.concurrent                           avgt   10  0.870 ± 0.027  us/op
 * ConcurrentLinkedQueueVsSynchronizedList.concurrent:concurrentLinkedQueueAdd  avgt   10  0.916 ± 0.055  us/op
 * ConcurrentLinkedQueueVsSynchronizedList.concurrent:concurrentLinkedQueueGet  avgt   10  0.825 ± 0.042  us/op
 * ConcurrentLinkedQueueVsSynchronizedList.sync                                 avgt   10  0.588 ± 0.365  us/op
 * ConcurrentLinkedQueueVsSynchronizedList.sync:synchronizedListAdd             avgt   10  0.473 ± 0.289  us/op
 * ConcurrentLinkedQueueVsSynchronizedList.sync:synchronizedListGet             avgt   10  0.703 ± 0.442  us/op
 */
@Warmup(iterations = 10)
@Measurement(iterations = 10)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Group)
public class ConcurrentLinkedQueueVsSynchronizedList {

    private SynchronizedLinkedList synchronizedList;
    private ConcurrentLinkedQueue<String> concurrentLinkedQueue;
    private final static String DATA = "TEST";
    private final static Object LOCK = new Object();

    /**
     * 在SynchronizedLinkedList内部对LinkedList的操作方法进行同步代码块操作
     **/
    private static class SynchronizedLinkedList {
        private LinkedList<String> list = new LinkedList<>();

        void addLast(String element) {
            synchronized (LOCK) {
                list.addLast(element);
            }
        }

        String removeFirst() {
            synchronized (LOCK) {
                //LinkedList为空时，调用removeFirst会报错，因此需要进行简单判断
                if (list.isEmpty()) return null;
                return list.removeFirst();
            }

        }
    }

    @Setup(Level.Iteration)
    public void setUp() {
        synchronizedList = new SynchronizedLinkedList();
        concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    }

    @Group("sync")
    @Benchmark
    @GroupThreads(5)
    public void synchronizedListAdd() {
        synchronizedList.addLast(DATA);
    }

    @Group("sync")
    @Benchmark
    @GroupThreads(5)
    public String synchronizedListGet() {
        return synchronizedList.removeFirst();
    }

    @Group("concurrent")
    @Benchmark
    @GroupThreads(5)
    public void concurrentLinkedQueueAdd() {
        concurrentLinkedQueue.offer(DATA);
    }

    @Group("concurrent")
    @Benchmark
    @GroupThreads(5)
    public String concurrentLinkedQueueGet() {
        return concurrentLinkedQueue.poll();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder().include(ConcurrentLinkedQueueVsSynchronizedList.class.getSimpleName()).build();
        new Runner(opt).run();
    }
}