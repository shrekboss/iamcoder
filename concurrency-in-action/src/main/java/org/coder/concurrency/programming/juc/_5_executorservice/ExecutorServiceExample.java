package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * 5.4 CompletionService详解
 * 在5.2节中，我们接触到了Future接口，Future接口提供了一种在未来某个时间节点判断异步任务是否完成执行、获取运算结果等操作的方式。
 * 如果在异步任务仍在继续运行之时执行get方法，会使得当前线程进入阻塞直到异步任务运行结束(正常结束/异常结束)。
 * 因此无论是通过ExecutorService提交Runnable类型的任务还是Callable类型的任务，只要你关注异步任务的运行结果，就必须持续跟踪返回Future引用。
 * <p>
 * 本节将要讲解的CompletionService则采用了异步任务提交和计算结果Future解耦的一种设计方式，
 * 在CompletionService中，我们进行任务的提交，然后通过操作队列的方式（比如take或者poll）来获取消费Future。
 * <p>
 * CompletionService并不是ExecutorService的子类，因此它并不具备执行异步任务的能力(异步任务的执行是由CompletionService内部的ExecutorService来完成的)，
 * 它只是对ExecutorService的一个封装，在其内部提供了阻塞队列用于Future的消费。
 * <p>
 * 5.4.1 ExecutorService执行批量任务的缺陷
 * Future除了“调用者线程需要持续对其进行关注才能获得结果”这个缺陷之外，还有一个更为棘手的问题在于，
 * 当通过ExecutorService的批量任务执行方法invokeAll来执行一批任务时，无法第一时间获取最先完成异步任务的返回结果。
 * 下面来看一个简单的示例，代码如下。
 * <p>
 * 在下面的代码中我们定义了三个批量任务，很明显，耗时10秒的任务将会第一个执行完成，但是很遗憾，我们无法立即使用该异步任务运算所得的结果。
 * 原因是在批量任务中存在一个拖后腿的（30秒才能运行结束），因此想要在接下来的程序运行中使用上述批量任务的结果至少还要等待30秒的时间，
 * 这对于耗时较快的任务来说是一种非常不必要的等待。
 * <p>
 * 下面通过图5-7简单分析一下在ExecutorService中通过invokeAll方法提交批量任务并返回最终结果的整个过程，
 * 整个任务的执行时长将以执行最慢的任务为准。
 */
public class ExecutorServiceExample {

    public static void main(String[] args) {
        batchTaskDefect();
    }

    private static void batchTaskDefect() {
        //定义ExecutorService
        ExecutorService executor = Executors.newCachedThreadPool();
        //定义批量异步任务，每一个异步任务耗时不相等
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
        try {
            //批量提交执行异步任务，该方法会阻塞等待所有的Future返回
            List<Future<Integer>> futures = executor.invokeAll(tasks);
            futures.forEach(future -> {
                try {
                    System.out.println(future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //休眠方法
    private static void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}