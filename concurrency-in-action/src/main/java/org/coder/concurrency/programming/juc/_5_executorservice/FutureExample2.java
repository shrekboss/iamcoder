package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.*;

/**
 * 取消异步正在执行的任务：如果一个异步任务的运行特别耗时，那么Future是允许对其进行取消操作的。
 * <p>
 * 运行上面的程序会看到如下的结果，如果在执行future的cancel方法时指定参数为true，那么在callable接口中正在运行的可中断方法会被立即中断，比如sleep方法。
 */
public class FutureExample2 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Double> future = executor.submit(() -> {
            TimeUnit.SECONDS.sleep(20);
            System.out.println("Task completed.");
            return 53.3d;
        });
        TimeUnit.SECONDS.sleep(10);
        //取消正在执行的异步任务（参数为false，任务虽然已被取消但是不会将其中断）
        System.out.println("cancel success ? " + future.cancel(false));
        //isCancelled返回任务是否被取消
        System.out.println("future is cancelled ? " + future.isCancelled());
        //对一个已经取消的任务执行get方法会抛出异常
        System.out.println("The task result:" + future.get());

    }

}