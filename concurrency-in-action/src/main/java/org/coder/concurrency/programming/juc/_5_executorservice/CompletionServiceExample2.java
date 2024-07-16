package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 在了解了CompletionService的基本使用场景之后，我们再来一下它的其他方法和构造方式。
 * 1.CompletionService的构造：CompletionService并不具备异步执行任务的能力，因此要构造CompletionService则需要ExecutorService，当然还允许指定不同的BlockingQueue实现。
 * 1).ExecutorCompletionService(Executor executor)：BlockingQueue默认为LinkedBlockingQueue（可选边界）。
 * 2).ExecutorCompletionService(Executor executor, BlockingQueue<Future<V>> completionQueue):允许在构造时指定不同的BlockingQueue。
 * 2.提交Callable类型的任务：已经通过示例进行了说明。
 * Future<V> submit(Callable<V> task)
 * 3.提交Runnable类型的任务：除了提交Callable类型的任务之外，在CompletionService中还可以提交Runnable类型的任务，但是返回结果仍然需要在提交任务方法时指定。
 * 4.立即返回方法Future<V> poll()：从CompletionService的阻塞队列中获取已执行完成的Future，如果此刻没有一个任务完成则会立即返回null值。
 * 5.Future<V> poll(long timeout, TimeUnit unit):同上，指定了超时设置。
 * 6.阻塞方法Future<V> take() throws InterruptedException：会使当前线程阻塞，直到在CompletionService中的阻塞队列有完成的异步任务Future。
 * <p>
 * 下面通过一段代码来了解下如何在CompletionService中提交Runnable类型的任务。
 * <p>
 * 5.4.3 CompletionService总结
 * 本节学习了CompletionService及其实现ExecutorCompletionService，它并不是ExecutorService的一个实现或者子类，而是对ExecutorService提供了进一步的封装，
 * 使得任务的提交者不再关注追踪所返回的Future，并且通过CompletionService直接获取已经运算结束的异步任务，这种方式实现了调用者和Future之间的解耦合，
 * 在一定程度上解决了Future会使调用者线程进入阻塞的问题，尤其是通过ExecutorService提交批处理任务为如何快速使用最早结束的异步任务运算结果提供了一种新的思路和实现方式。
 */
public class CompletionServiceExample2 {

    public static void main(String[] args) {
        //自定义ExecutorService并将其用于构造CompletionService
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<AtomicLong> completionService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < 5; i++) {
            AtomicLong al = new AtomicLong();
            //提交Runnable类型的任务，但是需要指定返回值
            completionService.submit(() -> {
                long random = ThreadLocalRandom.current().nextLong(30);
                sleep(random);
                System.out.println("Task " + random + " completed.");
                //设置计算结果
                al.set(random);
            }, al);
        }

        for (int i = 0; i < 5; i++) {
            try {
                //阻塞式地获取已完成任务的Future，并使用运算结果
                System.out.println(completionService.take().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }

    private static void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}