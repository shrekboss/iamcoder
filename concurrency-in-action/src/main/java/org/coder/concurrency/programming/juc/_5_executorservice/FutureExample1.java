package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.*;

/**
 * 5.2 Future和Callback
 * 5.2.1 Future详解
 * 简单来说，Future代表着一个异步任务在未来的执行结果，这个结果可以在最终的某个时间节点通过Future的get方法来获得，关于Future更多的细节和原理，
 * 在笔者的书《Java高并发编程详解：多线程与架构设计》中第19章“Future设计模式”里进行了很详细的阐述，这里不再赘述，有需要的读者可以自行查阅。
 * <p>
 * 对于长时间运行的任务来说，使其异步执行并立即返回一个Future接口是一种比较不错的选择，因为这样可以允许程序在等待结果的同时继续去执行其他的任务，比如如下这些任务。
 * 1.密集型计算(数学和科学计算)。
 * 2.针对大数据的处理计算。
 * 3.通过远程方法调用数据。
 * 下面来看一个简单的例子，快速了解一下Future接口的特点，以及其在异步任务中所带来的便利。
 * <p>
 * Future接口也是在JDK1.5版本中随着并发包一起被引入JDK的，Future接口定义如下所示(共包含5个接口方法)。
 * package java.util.concurrent;
 * public interface Future<V> {
 * //取消任务的执行，如果mayInterruptIfRunning为true，则工作线程将会被中断，否则即使执行了cancel方法，也会等待其完成，
 * //无论mayInterruptIfRunning为true还是false，isCancelled()都会为true，并且执行get方法会抛异常。
 * boolean cancel(boolean mayInterruptIfRunning);
 * <p>
 * //判断异步任务是否被取消
 * boolean isCancelled();
 * <p>
 * //判断异步任务的执行是否结束
 * boolean isDone();
 * <p>
 * //获取异步任务的执行结果，如果任务未运行结束，则该方法会使当前线程阻塞
 * //异步任务运行错误，调用get方法会抛出ExecutionException异常
 * V get() throws InterruptedException, ExecutionException;
 * <p>
 * //同get方法，但是允许设置最大超时时间
 * V get(long timeout, TimeUnit unit) throw InterruptedException, ExecutionException, TimeoutException;
 * }
 * <p>
 * Future接口中定义的方法，我们在源码注释中已经进行了详细的说明，现在重点说明一下其中的一些接口方法和关键接口callable。
 * <p>
 * Callable接口：该接口与Runnable接口非常相似，但是Runnable作为任务接口最大的问题就是无法返回最终的计算结果，
 * 因此在JDK1.5版本中引入了Callable泛型接口，它允许任务执行结束后返回结果。
 * package java.util.concurrent;
 *
 * @FunctionalInterface public interface Callable<V> {
 * V call() throws Exception;
 * }
 */
public class FutureExample1 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //提交任务，传入Callable接口，并且立即返回Future
        Future<Double> future = executor.submit(() -> {
            //模拟任务执行耗时
            TimeUnit.SECONDS.sleep(20);
            return 53.3d;
        });
        //当前线程在等待结果结束的同时还可以做一些其他的事情
        System.out.println("main thread do other thing.");
        //获取执行结果
        System.out.println("The task result:" + future.get());
        executor.shutdown();

    }

}