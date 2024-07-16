package org.coder.concurrency.programming.juc._5_executorservice;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 5.1.4 Executors详解
 * 通过前面内容的学习，我们知道要创建一个ExecutorService，尤其是ThreadPoolExecutor是比较复杂的，
 * Java并发包中提供了类似工厂方法的类，用于创建不同的ExecutorService，当然还包括拒绝策略、ThreadFactory等。
 * 1.FixedThreadPool
 * //创建ExecutorService，指定核心线程数
 * public static ExecutorService new FixedThreadPool(int nThreads) {
 * return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.milliseconds, new LinkedBlockingQueue<Runnable>());
 * }
 * //创建ExecutorService，指定核心线程数和ThreadFactory
 * 通过源码我们不难发现，线程池的核心线程数和最大线程数是相等的，因此该线程池中的工作线程数将始终是固定的。
 * 任务队列为LinkedBlockingQueue，所以理论上提交至线程池的任务始终都会被执行，只有显式地执行线程池的关闭方法才能关闭线程池。
 * <p>
 * 2.SingleThreadPool
 * //创建只有一个工作线程的线程池
 * public static ExecutorService newSingleThreadExecutor(){
 * return new FinalizableDelegatedExecutorService(
 * new ThreadPoolExecutor(1, 1, 0L, TimeUnit.milliseconds, new LinkedBlockingQueue<Runnable>());
 * );
 * }
 * //创建只有一个工作线程的线程池，并指定ThreadFactory
 * public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory){
 * return new FinalizableDelegatedExecutorService(
 * new ThreadPoolExecutor(1, 1, 0L, TimeUnit.milliseconds, new LinkedBlockingQueue<Runnable>(), threadFactory));
 * );
 * }
 * static class FinalizableDelegatedExecutorService extends DelegatedExecutorService {
 * FinalizableDelegatedExecutorService(ExecutorService executor) {
 * super(executor);
 * }
 * }
 * //重写finalize方法
 * protected void finalize() {
 * //当gc发生时，线程池会被执行shutdown
 * super.shutdown();
 * }
 * SingleThreadPool是只有一个核心线程的线程池，但是Finalizable代理了该线程池，因此当线程池引用可被垃圾回收器回收时，线程池的shutdown方法会被执行，
 * 当然我们还是建议显式地调用线程池的关闭方法。
 * 3.CachedThreadPool
 * //创建Cached线程
 * public static ExecutorService newCachedThreadPool() {
 * return new ThreadPoolExecutor(0, Integer.max_value, 60L, TimeUnit.seconds, new SynchronousQueue<Runnable>());
 * }
 * //创建Cached线程池并指定ThreadFactory
 * public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
 * return new ThreadPoolExecutor(0, Integer.Max_value, 60L, TimeUnit.seconds, new SynchronousQueue<Runnable>(), threadFactory);
 * }
 * CachedThreadPool根据需要创建新线程，但是会重用以前构造的可用线程。该线程池通常会用于提高执行量大的、好事较短的，异步任务程序的运行性能，
 * 在该线程池中，如果有可用的线程将被直接重用。如果没有可用的线程，则会创建一个新线程并且将其添加到池中。未被使用且空闲时间超过60秒的线程将被终止并从线程池中移除，
 * 因此长时间空闲的线程不会消耗任何资源。
 * <p>
 * 4.ScheduledThreadPool
 * //构造指定核心线程数的ScheduledThreadPoolExecutor
 * public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize){
 * return new ScheduledThreadPoolExecutor(corePoolSize);
 * }
 * //指定核心线程数和ThreadFactory
 * public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory) {
 * return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
 * }
 * 关于创建指定核心线程数量的ScheduledExecutorService，由于其很容易理解，所以此处不再赘述。
 * <p>
 * 5.WorkStealingPool
 * //并发度等于CPU核数
 * public static ExecutorService newWorkStealingPool() {
 * return new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
 * }
 * //允许指定并发度
 * public static ExecutorService newWorkStealingPool(int parallelism) {
 * return new ForkJoinPool(parallelism, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
 * }
 * WorkStealingPool是在JDK1.8版本中引入的线程池，它的返回结果是ForkJoinPool，而不是ScheduledThreadPoolService或者ThreadPoolExecutor。
 * <p>
 * 与其他线程池不同的是，WorkStealingPool中的工作线程会处理任务队列中与之对应的任务分片(Divide and conquer:分而治之)，
 * 如果某个线程处理的任务执行比较耗时，那么它所负责的任务将会被其他线程“窃取”执行，进而提高并发处理的效率。
 * <p>
 * 5.1.5 ExecutorService总结
 * 本节介绍了ExecutorService及其两个重要实现ScheduledExecutorService和ThreadPoolExecutor，
 * 详细分析了任务执行方法的流程、ThreadFactory、几种不同的拒绝策略，以及如何有效地关闭ExecutorService等。
 * <p>
 * 在应用程序中构造一个合理的ExecutorService是一件非常重要的事情，因此Executors提供了若干创建不同ExecutorService的工厂，本章对它们进行了重点讲述。
 * Google的Guava工具集中还提供了MoreExecutors用于创建其他类型的ThreadPool，感兴趣的读者也可以了解一下。
 * <p>
 * 当然，在线程池中执行的异步不仅仅是Runnable类型，还可以在线程池中提交执行Callable类型的任务，以及得到Future(凭据)返回结果以备在未来的某个时间点使用，甚至取消执行中的任务，5.2节就将讲解这些内容。
 */
public class ExecutorsTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    private void shutdownAndAwaitTermination(ExecutorService executor, long timeout, TimeUnit unit) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeout, unit)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(timeout, unit)) {
                    //print executor not terminated by normal.
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void FixedThreadPool() {
        //创建FixedThreadPool
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorService.execute(() -> {
                try {
                    System.out.println(Thread.currentThread() + " is running.");
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        //关闭ExecutorService
        shutdownAndAwaitTermination(executorService, 1, TimeUnit.MINUTES);
    }

    private void singleThreadPool2() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //提交执行异步任务
        executor.execute(() -> System.out.println("normal task."));
    }

    //输出JVM线程堆栈信息
    private void printThreadStack() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] ids = threadMXBean.getAllThreadIds();
        for (long id : ids) {
            System.out.println(threadMXBean.getThreadInfo(id));
        }
    }

    @Test
    public void singleThreadPool() throws InterruptedException {
        //创建SingleThreadPool并执行任务
        singleThreadPool2();
        //输出当前JVM的线程堆栈信息
        printThreadStack();
        //简单分割
        System.out.println("*********************************************");
        //显式调用GC，但是并不会立即作用(详见笔者第一本书中的ActiveObject)
        System.gc();
        TimeUnit.MINUTES.sleep(1);
        //再次输出当前JVM的线程堆栈信息
        printThreadStack();
        //异步任务的执行输出
//		normal task.
        //SingleThreadPool中的线程
//		"pool-1-thread-1" Id=12 WAITING on java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject@123a439b
//
//
//		"ReaderThread" Id=11 RUNNABLE (in native)
//
//
//		"Attach Listener" Id=5 RUNNABLE
//
//
//		"Signal Dispatcher" Id=4 RUNNABLE
//
//
//		"Finalizer" Id=3 WAITING on java.lang.ref.ReferenceQueue$Lock@7de26db8
//
//
//		"Reference Handler" Id=2 WAITING on java.lang.ref.Reference$Lock@1175e2db
//
//
//		"main" Id=1 RUNNABLE
//
//
//		*********************************************分割线以下找不到线程池中的线程了
//		"ReaderThread" Id=11 RUNNABLE (in native)
//
//
//		"Attach Listener" Id=5 RUNNABLE
//
//
//		"Signal Dispatcher" Id=4 RUNNABLE
//
//
//		"Finalizer" Id=3 WAITING on java.lang.ref.ReferenceQueue$Lock@7de26db8
//
//
//		"Reference Handler" Id=2 WAITING on java.lang.ref.Reference$Lock@1175e2db
//
//
//		"main" Id=1 RUNNABLE		
    }

    @Test
    public void CachedThreadPool() {

    }

    @Test
    public void test3() {

    }

    @Test
    public void test4() {

    }
}