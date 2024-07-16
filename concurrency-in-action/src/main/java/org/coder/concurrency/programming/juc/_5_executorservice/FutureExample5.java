package org.coder.concurrency.programming.juc._5_executorservice;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.concurrent.*;

/**
 * 5.2.2 ExecutorService与Future
 * 在5.2.1节中，我们了解了在线程池中通过submit方法callable异步执行任务并且返回future的操作。
 * 线程池中还提供了其他更多任务执行的方法，本节将逐一进行介绍。
 * <p>
 * 1.提交Runnable类型任务
 * Submit方法除了可以提交执行Callable类型的任务之外，还可以提交Runnable类型的任务并且有两种重载形式，具体如下。
 * 1).public Future<?> submit(Runnable task):提交Runnable类型的任务并且返回Future，待任务执行结束后，通过该future的get方法返回的结果始终为null。
 * 2).public <T> Future<T> submit(Runnable task, T result):前一个提交Runnable类型的任务虽然会返回Future，但是任务结束之后通过future却拿不到任务的执行结果，而通过该submit方法则可以。
 */
public class FutureExample5 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //定义result
        AtomicDouble result = new AtomicDouble();
        Future<AtomicDouble> future = executor.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(20);
                //计算结果
                result.set(35.34D);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }, result);
        //获取返回结果为35.34
        System.out.println("The task result:" + future.get());
        //异步任务执行成功
        System.out.println("The task is done?" + future.isDone());
    }

}