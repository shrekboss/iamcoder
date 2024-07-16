package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 3.3 Exchanger 工具详解
 * Exchange(交换器)，从字面意思来看它的主要作用就是用于交换，那么它是用来交换什么的？是由谁和谁进行交换？交换怎样的数据？本节将介绍Java并发工具包中的Exchanger工具类。
 * Exchanger简化了两个线程之间的数据交互，并且提供了两个线程之间的数据交换点，Exchanger等待两个线程调用其exchange()方法。调用此方法，交换机会交换两个线程提供给对方的数据。
 * <p>
 * 3.3.1 一对线程间的数据交换
 * Exchanger在某种程度上可以看成是生产者和消费者模式的实现，但是它重点关注的是数据交换，所谓交换就是我给了你A，你给了我B，而生产者消费者模式中间使用队列将其解耦。
 * 生产者只需要在队列中放入元素，它并不在乎是否会有消费者的存在，同样消费者也只是从队列中获取元素，并不关心生产者是否存在，如图3-4所示。
 * <p>
 * 下面我们来快速地看一个例子，通过示例来感受如何使用Exchanger，下面的代码中定义了两个线程T1和T2，分别调用Exchanger的exchange方法将各自的数据传递给对方，
 * 在这里需要注意的是，每个线程在构造数据时的开销是不一样的，因此调用方法exchange的并不是同一时刻，当T1线程在执行exchange方法的时候，如果T2方法没有执行exchange方法，
 * 那么T1线程会进入阻塞状态等待T2线程执行exchange方法，只有当两个线程都执行了exchange方法之后，它们才会退出阻塞。
 * <p>
 * 上面这段代码的关键在于Exchanger的exchange方法，该方法是一个阻塞方法，只有成对的线程执行了exchange调用之后才会退出阻塞，
 * 我们通过随机休眠的方法模拟T1和T2线程不同程度的时间开销。调用exchange方法需要传递交换的数据，该数据的类型在定义Exchanger时就已经确立了，
 * 同时exchange方法的返回值代表着对方线程所交换过来的内容，运行上面代码将会得到如下的结果输出。
 * Thread[T1,5,main] start.
 * Thread[T2,5,main] start.
 * #T1线程退出阻塞的同时得到了来自T2线程交换过来的数据
 * Thread[T1,5,main]received: I am from T2
 * Thread[T1,5,main]end.
 * #T2线程退出阻塞的同时得到了来自T1线程交换过来的数据
 * Thread[T2,5,main]received: I am from T1
 * Thread[T2,5,main]end.
 */
public class ExchangerExample1 {

    public static void main(String[] args) {
        // 定义Exchanger类，该类是一个泛型类，String类型标明一对线程交换的数据只能是String类型
        final Exchanger<String> exchanger = new Exchanger<>();
        //定义线程T1
        new Thread(() -> {
            System.out.println(Thread.currentThread() + " start.");
            try {
                //随机休眠1~10秒钟
                randomSleep();
                //①执行exchange方法，将对应的数据传递给T2线程，同时从T2线程获取交换的数据data就是从T2线程中返回的数据
                String data = exchanger.exchange("I am from T1");
                System.out.println(Thread.currentThread() + "received: " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + "end.");
        }, "T1").start();
        //原理同T1线程，省略注释内容...
        new Thread(() -> {
            System.out.println(Thread.currentThread() + " start.");
            try {
                randomSleep();
                String data = exchanger.exchange("I am from T2");
                System.out.println(Thread.currentThread() + "received: " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + "end.");
        }, "T2").start();
    }

    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}