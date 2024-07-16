package org.coder.concurrency.programming.juc._4_colleciton;

import com.google.common.base.Stopwatch;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 4.3.2 并发队列在使用中需要注意的问题
 * 虽然并发队列在高并发多线程的环境中有着优异的性能表现，但是如果对其使用不当不仅对性能没有任何提升反倒会降低整个系统的运行效率。
 * <p>
 * 1.在并发队列中使用size方法不是个好主意
 * 我们知道每一个Collection(队列Queue也是Collection的子接口)都提供了size()方法用于获取Collection中元素个数，
 * 但是在并发队列中执行该方法却不是一个明智的操作，为什么呢？
 * 1).首先，并发队列是基于链表的结构实现的，并且在其内部并未提供类似于计数器的变量(当元素插入队列计数器时增一，当元素从队列头部被移除时计数器减一)，
 * 因此想要获得当前队列的元素个数，需要遍历整个队列才能计算得出(效率低下)。
 * 2).其次并发队列采用无锁(Lock-Free)的算法实现，因此在某个线程中执行size()方法获取元素数量的同时，其他线程也可以对该队列进行读写操作，
 * 所以size()返回的数值不会是一个精确值，而是一个近似值、一个估计值。
 * <p>
 * 2.ConcurrentLinkedQueue的内存泄露问题
 * 另外，ConcurrentLinkedQueue在执行remove方法删除元素时还会出现性能越来越低，甚至内存泄漏的问题。
 * 这个问题最早是由于jetty的开发者发现的，因为jetty内部的线程池采用的就是ConcurrentLinkedQueue作为任务的队列，
 * 随后在很多开源项目中都发现了内存泄漏的问题，比如Apache Cassandra。
 * <p>
 * 值得庆幸的是，在jetty中该问题被发现后得到了解决，开发者们采用ConcurrentHashSet替代了ConcurrentLinkedQueue的解决方案，
 * 不过很遗憾的是，在JDK的7、8、9版本中该问题依然存在（笔者亲测），下面通过程序重现该bug以及借助于工具进行内存溢出的探测。
 * <p>
 * 暂时不要关心内存泄漏的问题，先来执行上面的程序，观察每10000次的add和remove运行的耗时对比情况。
 * <p>
 * 在上面的程序中，程序每add一个object至队列后会立即将其remove掉，由于我们在代码注释①处提前插入了一个元素进入队列，
 * 因此每一个批次(10000)执行结束之后，队列的元素个数应该始终为一个，并且每一个批次(10000)的执行时间都应该相等或者相差不大，
 * 但是通过程序的输出我们不难发现队列的效率越来越低(慢)。
 * <p>
 * 下面再来分析一下ConcurrentLinkedQueue内存泄漏的问题，在程序启动时我们打开JVM监控工具观察内存的变化。
 * <p>
 * 在程序运行至while循环之前，JVM的堆内存使用情况大致为18MB左右，但是当程序运行至while循环之后，程序运行所占用的内存会不断升高。
 * <p>
 * 如图4-16所示的锯齿状内存使用情况不免让人觉得堆内存会被正常回收，并未出现不可回收的泄漏情况。但是当我们将每一次GC之后堆内存的使用数据提取出来进行对比，
 * 不难发现被使用的堆内存最小值在不断升高，这就意味着出现了内存无法被回收即内存泄漏的情况。
 * <p>
 * 18513208bytes(初始)——>7505960bytes(第一次GC)——>10356944bytes(第二次GC)——>12287880bytes(第三次GC)——>12527880bytes(第四次GC)——>13627984bytes(第五次GC)，
 * 每次的GC操作都是笔者手动执行的(为例尽快显示问题)，通过这组数据不难看出堆内存的最小使用值在不断增加。
 * <p>
 * 既然remove方法存在越来越慢甚至内存泄漏的风险，那么我们应该怎么做呢？
 * 第一，删除注释①处的代码；第二，将注释②处1的remove方法换成poll方法；第三，poll方法只能从队列头部移除元素而无法移除指定的元素，如果想要移除指定的元素，那么又该怎么做呢？
 * 在jdk官方未解决该问题之前建议你更换另外的解决方案，比如学习jetty的思路使用ConcurrentHashSet替代ConcurrentLinkedQueue。
 * <p>
 * 为了能够进行比较，我们将注释①处的代码删除，再次运行该程序，不难发现这次程序的执行速度非常快，并且每个批次(10000)的耗时几乎相等(接近)，并不会出现越来越慢的情况，运行耗时情况如下所示。
 * <p>
 * 程序运行速度非常快，并且会出现频繁的GC操作(链表是有不同的Node节点构成的，Node对象具备GC条件，因此对Node节点的删除会触发JVM的minGC操作)，
 * 下面我们来看一下这个时候堆内存的使用情况，如图4-17所示。
 * <p>
 * 4.3.3 并发队列总结
 * 本节介绍了ConcurrentLinkedQueue(先进先出队列)和ConcurrentLinkedDeque(双向队列)，对于队列的使用前文中做了很多介绍，因此本节并未对每种方法都展开详细的介绍。
 * 并发队列在实现上采用了无锁(Lock Free)算法，因此在多线程高并发的环境中其拥有更出色的性能表现，但是ConcurrentLinkedQueue并不是在任何情境下都会保持高效，
 * 比如执行size()方法时，甚至本身在对元素进行删除操作时都存在着性能隐患和内存溢出的问题，关于这些，本节中都做了非常详细的介绍。当然了，这并不妨碍你在开发中使用它，
 * 但是使用得当的前提是你必须搞清楚它在什么情况下会出现问题，只有这样才能对其驾驭得当、运用自如。
 */
public class ConcurrentLinkedQueueMemLeak {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<>();
        queue.add(new Object());//① 这一行代码会导致内存泄漏
        Object object = new Object();

        int loops = 0;
        //休眠10秒，方便打开jdk诊断工具，监控执行前后的内存变化
        TimeUnit.SECONDS.sleep(30);

        Stopwatch watch = Stopwatch.createStarted();

        while (true) {
            //每执行10000次进行一次耗时统计，并且输出
            if (loops % 1000000 == 0 && loops != 0) {
                long elapsedMs = watch.stop().elapsed(TimeUnit.MILLISECONDS);
                System.out.printf("loops=%d duration = %d MS%n", loops, elapsedMs);
                watch.reset().start();
            }
            queue.add(object);
            //② remove方法删除object
            queue.remove(object);
            ++loops;
        }
    }

}