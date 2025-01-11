package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 4.ThreadFactory详解
 * 在ThreadExecutorPool的构造参数中提供了一个接口ThreadFactory，用于定义线程池中的线程，我们可以通过该接口指定线程的命名规则、优先级、是否为daemon守护线程等信息
 * (在第7章“Metrics”中，构造Reporter内部线程池时就通过自定义ThreadFactory的方式将所有工作线程设置为守护线程)。
 * package java.util.concurrent;
 * public interface ThreadFactory {
 * Thread newThread(Runnable r);
 * }
 * <p>
 * 在ThreadFactory中只有一个接口方法newThread，参数为Runnable，任务接口返回值为Thread实例，下面是ThreadFactory的一个简单实现，并将其应用与创建ThreadExecutorPool的示例。
 * <p>
 * 然后，我们在构造ThreadExecutorPool时指定自定义的ThreadFactory即可。
 * <p>
 * 运行下面的程序，会看到自定义线程的输出信息。
 */
public class ThreadFactoryTest {

    //静态内部类，用于实现ThreadFactory接口
    private static class MyThreadFactory implements ThreadFactory {
        private final static String PREFIX = "ALEX";
        private final static AtomicInteger INC = new AtomicInteger();

        //重写newThread方法
        @Override
        public Thread newThread(Runnable command) {
            //定义线程MyPool
            ThreadGroup group = new ThreadGroup("MyPool");
            //构造线程时指定线程所属的线程组以及线程的命名
            Thread thread = new Thread(group, command, PREFIX + "-" + INC.getAndIncrement());
            //设置线程优先级
            thread.setPriority(10);
            //设置线程为守护线程
            thread.setDaemon(true);
            return thread;
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new MyThreadFactory(), //使用自定义的ThreadFactory
                new ThreadPoolExecutor.DiscardPolicy());
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                //...省略
                System.out.println("Task finish done by " + Thread.currentThread());
            });
        }
    }
}