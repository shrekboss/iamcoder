package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * 5.5.2 任务的异步运行
 * 当然，CompletableFuture除了具备Future的基本特征之外，还可以直接使用它执行异步任务，通常情况下，任务的类型为Supplier和Runnable，
 * 前者非常类似于Callable接口，可返回指定类型的运算结果，后者则仍旧只是关注异步任务运行本身。
 * 1.异步任务执行Supplier类型的任务：可以直接调用CompletableFuture的静态方法supplyAsync异步执行Supplier类型的任务。
 * 2.异步执行Runnable类型的任务：也可以直接调用CompletableFuture的静态方法runAsync异步执行Runnable类型的任务。
 */
public class CompletableFutureExample3 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 353);
        assert future.get() == 353;
        /** supplyAsync方法的另外一个重载方法，允许传入ExecutorService**/
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 353, Executors.newCachedThreadPool());
        assert future2.get() == 353;
        CompletableFuture.runAsync(() -> {
            System.out.println("async task.");
        });
        /** runAsync方法的另外一个重载方法，允许传入ExecutorService **/
        CompletableFuture.runAsync(() -> {
            System.out.println("async task 2.");
        }, Executors.newCachedThreadPool());
    }

}