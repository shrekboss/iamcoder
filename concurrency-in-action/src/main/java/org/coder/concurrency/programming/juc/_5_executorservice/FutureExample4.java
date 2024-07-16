package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 任务执行错误：Runnable类型的任务中，run()方法抛出的异常（运行时异常）只能被运行它的线程捕获，但是启动运行线程的主线程却很难获得Runnable任务运行时出现的异常信息。
 * 在《Java高并发编程详解：多线程与架构设计》一书的第7章“Hook线程以及捕获线程执行异常”中有讲到，我们可以通过设置UncaughtExceptionHandler的方式来捕获异常，
 * 但是这种方式的确不够优雅，并且也无法精确地执行哪个任务时出现的错误，Future则是通过捕获get方法异常的方式获取异步任务执行的错误信息的，如下面的示例代码所示。
 */
public class FutureExample4 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Double> future = executor.submit(() -> {
            //抛出异常
            throw new RuntimeException();
        });
        try {
            System.out.println("The task result:" + future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            //cause是RuntimeException
            System.out.println(e.getCause());
        }
    }

}