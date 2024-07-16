package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 3.invokeAll
 * invokeAll方法同样可用于异步处理批量的任务，但是该方法关心所有异步任务的运行，invokeAll方法同样也是阻塞方法，一直等待所有的异步任务执行结束并返回结果。
 * 示例代码如下：
 * <p>
 * ExecutorService还提供了invokeAll方法的重载形式，增加了超时特性。
 * <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException;
 * <p>
 * 5.2.3 Future的不足之处
 * Future的不足之处包括如下几项内容。
 * 1.无法被动接收异步任务的计算结果：虽然我们可以主动将异步任务提交给线程池中的线程来执行，但是待异步任务结束后，
 * 主（当前）线程无法得到任务完成与否的通知（关于这一点，5.2.4节中将会给出解决方案），它需要通过get方法主动获取计算结果。
 * 2.Future间彼此孤立：有时某一个耗时很长的异步任务执行结束以后，你还想利用它返回的结果再做进一步的运算，该运算也会是一个异步任务，两者之间的关系需要程序开发人员手动进行绑定赋予，
 * Future并不能将其形成一个任务流（pipeline），每一个Future彼此之间都孤立，但5.5节将要介绍的CompletableFuture就可以将多个Future串联起来形成任务流。
 * 3.Future没有很好的错误处理机制：截至目前，如果某个异步任务在执行的过程中发生了异常错误，
 * 调用者无法被动获知，必须通过捕获get方法的异常才能知道异步任务是否出现了错误，从而再做进一步的处理。
 */
public class FutureExample7 {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //定义批量任务
        List<Callable<Integer>> callables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            callables.add(() -> {
                int random = ThreadLocalRandom.current().nextInt(30);
                TimeUnit.SECONDS.sleep(random);
                System.out.println("Task:" + random + " completed in Thread " + Thread.currentThread());
                return random;
            });
        }
        try {
            //执行批量任务，返回所有异步任务的future集合
            List<Future<Integer>> futures = executor.invokeAll(callables);
            //输出计算结果
            futures.forEach(future -> {
                try {
                    System.out.println("Result:" + future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        executor.shutdown();
    }

}