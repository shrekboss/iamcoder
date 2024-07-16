package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * 5.4.2 CompletionService详解
 * CompletionService很好地解决了异步任务问题，在CompletionService中提供了提交异步任务的方法（真正的异步任务执行还是由其内部的ExecutorService完成的），
 * 任务提交之后调用者不再关注Future，而是从BlockingQueue中获取已经执行完成的Future，在异步任务完成之后Future才会被插入阻塞队列，
 * 也就是说调用者从阻塞队列中获取的Future是已经完成了的异步执行任务，所以再次通过Future的get方法获取结果时，调用者所在的当前线程将不会被阻塞。
 * 示例代码如下：
 * <p>
 * 运行上面的程序你会发现，最先完成的任务将会存入阻塞队列之中，因此调用者线程可以立即处理从阻塞队列中得到的异步任务的运算结果，并进行下一步的操作。
 */
public class CompletionServiceExample1 {

    public static void main(String[] args) {
        //定义ExecutorService
        ExecutorService executor = Executors.newCachedThreadPool();
        //定义CompletionService使用ExecutorService
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        //定义同样的任务
        final List<Callable<Integer>> tasks = Arrays.asList(
                () -> {
                    //模拟耗时30秒
                    sleep(30);
                    System.out.println("Task 30 completed done.");
                    return 30;
                },
                () -> {
                    //模拟耗时10秒
                    sleep(10);
                    System.out.println("Task 10 completed done.");
                    return 10;
                },
                () -> {
                    //模拟耗时20秒
                    sleep(20);
                    System.out.println("Task 20 completed done.");
                    return 20;
                }
        );
        //提交所有异步任务
        tasks.forEach(completionService::submit);

        for (int i = 0; i < tasks.size(); i++) {
            try {
                //从completionService中获取已完成的Future，take方法会阻塞
                System.out.println(completionService.take().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        System.out.println("end.");
    }

    private static void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}