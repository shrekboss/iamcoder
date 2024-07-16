package org.coder.concurrency.programming.juc._3_utils;

import org.junit.Test;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * 3.3.2 Exchanger的方法详解
 * 通过在3.3.1节中对Exchanger的讲解以及两个示例的加持，相信读者应该能够理解和掌握Exchanger的基本用法了，Exchanger对外提供的方法非常简单，
 * 仅有两个方法，但是如果使用不得当将会出现问题，比如整个线程阻塞进而导致整个JVM进程的阻塞，本节将说明使用Exchanger时需要注意的问题有哪些。
 * 1.public V exchange(V x) throws InterruptedException：数据交换方法，该方法的作用是将数据x交换至搭档线程，执行该方法后，
 * 当前线程会进入阻塞状态，只有搭档线程也执行了exchange方法之后，该当前线程才会退出阻塞状态进行下一步的工作，与此同时，该方法的返回值代表着搭档线程所传递过来的交换数据。
 * 2.public V exchange(V x, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException：
 * 该方法的作用与前者类似，只不过增加了超时的功能，也就是说在指定的时间内搭档线程没有执行exchange方法，当前线程会退出阻塞，并且返回值为null。
 * <p>
 * Exchanger用于数据交换的前提基本上是exchange方法被成对调用。另外，我们虽然可以在exchange方法中传入null值，但是Exchanger会为我们提供一个默认的Object(NULL_ITEM)值，
 * 在最后返回值时还会根据交换数据与NULL_ITEM进行匹配，并将交换数据重新返回为null，具体请看如下源码。
 * public V exchange(V x) throws InterruptedException {
 * Object v;
 * //如果x为null，则使用NULL_ITEM替代，NULL_ITEM其实就是一个new object
 * Object item = (x == null) ? NULL_ITEM : x;
 * if((arena != null || (v = slotExchange(item, false, 0L)) == null) && ((Thread.interrupted() || (v = arenaExchange(item, falae, 0L)) == null))) {
 * throw new InterruptedException();
 * }
 * //匹配v值，如果v值与null_item相等，则说明对方线程交换了null值，因此重新还原为null
 * return (v == NULL_ITEM) ? null : (V)v;
 * }
 * <p>
 * 3.3.3 Exchanger总结
 * Exchanger在类似于生产者-消费者的情况下可能会非常有用。在生产者-消费者问题中，拥有一个公共的数据缓冲区(队列)、一个或多个数据消费者。
 * 由于交换机类只涉及两个线程，因此如果你想要在两个线程之间同步数据或者交换数据，那么这种情况就可以使用Exchanger这个工具，
 * 当然在使用它的时候请务必做好线程的管理工作，否则将会出现线程阻塞，程序无法继续执行的假死情况。
 */
public class ExchangerTest {
    /**
     * 如果使用Exchanger的两个线程，其中一个由于某种原因意外退出，那么此时另外一个线程将会永远处于阻塞状态，进而导致JVM进程出现假死的情况。
     * 当然使用了超时功能的exchange在设定时间到达时会退出阻塞，因此在使用Exchanger时中断数据交换线程的操作是非常重要的，下面来看一下如何中断数据交换线程。
     *
     * @throws InterruptedException
     */
    @Test
    public void exchange1() throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();
        Thread t = new Thread(() -> {
            try {
                //线程进入阻塞
                exchanger.exchange(null);
            } catch (InterruptedException e) {
                //当有外部线程执行了该线程的中断操作时，此处会捕获到中断信号
                System.out.println("An interrupt signal was caught");
            }
        });
        t.start();
        //一秒之后t线程将会被中断
        TimeUnit.SECONDS.sleep(1);
        //中断线程
        t.interrupt();
    }

    /**
     * 上面的程序片段会将执行了exchange方法的线程从阻塞中中断，但是这还远远不够，我们来看下面的示例代码，即使执行了中断方法，线程仍然会被阻塞。
     * 运行下面的代码片段，线程t将永远不会被中断，原因是在线程t的休眠代码块中捕获到了中断信号并未做任何处理，因此中断信号被擦除。
     * 当线程再次进入exchange方法时就会进入阻塞，因此该线程将会导致JVM出现假死的情况。
     *
     * @throws InterruptedException
     */
    @Test
    public void exchange2() throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();
        Thread t = new Thread(() -> {
            try {
                //模拟线程执行某些可捕获中断信号的方法，比如sleep方法
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                //System.out.println("A");
            }

            try {
                exchanger.exchange(null);
            } catch (InterruptedException e) {
                //当有外部线程执行了该线程的中断操作时，此处会捕获到中断信号
                System.out.println("An interrupt signal was caught");
            }
        });
        t.start();
        TimeUnit.SECONDS.sleep(1);
        t.interrupt();
    }

    /**
     * 如果当前线程被执行过中断方法，并且从未捕获过中断信号，那么在执行exchange方法的时候会立即被中断，请看下面的代码片段。
     *
     * @throws InterruptedException
     */
    @Test
    public void exchange3() throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();
        Thread t = new Thread(() -> {
            //模拟复杂的计算，等待主线程执行对该线程的中断操作
            String s = "";
            for (int i = 0; i < 10000; i++) {
                s += "exchanger";
            }
            try {
                //执行该方法会被立即中断，因为中断信号并未被擦除
                exchanger.exchange(s);
            } catch (InterruptedException e) {
                System.out.println("An interrupt signal was caught");
            }
        });
        t.start();
        TimeUnit.SECONDS.sleep(1);
        //中断t线程
        t.interrupt();
    }
}