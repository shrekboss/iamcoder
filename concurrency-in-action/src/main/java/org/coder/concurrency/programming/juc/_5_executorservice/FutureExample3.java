package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.*;

/**
 * 获取异步执行任务的结果：当异步任务被正常执行完毕，可以通过get方法或者其重载方法（指定超时单位时间）获取最终的结果。
 */
public class FutureExample3 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Double> future = executor.submit(() -> {
            TimeUnit.SECONDS.sleep(20);
            return 53.3d;
        });
        System.out.println("The task result:" + future.get());
        System.out.println("The task is done?" + future.isDone());
    }

}