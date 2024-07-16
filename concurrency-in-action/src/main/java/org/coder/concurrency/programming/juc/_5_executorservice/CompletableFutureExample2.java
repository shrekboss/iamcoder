package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 在普通的Future中，我们可以通过cancel操作来决定是否取消异步任务的继续执行，同样，在CompletableFuture中也有类似地操作。
 */
public class CompletableFutureExample2 {

    public static void main(String[] args) {
        CompletableFuture<Double> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                //取消任务
                completableFuture.cancel(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        try {
            //get时会抛出异常
            completableFuture.get();
        } catch (Exception e) {
            //异常类型为CancellationException
            assert e instanceof CancellationException;
        }
    }

}