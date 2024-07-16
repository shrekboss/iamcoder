package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 2.invokeAny
 * ExecutionService允许一次性提交一批任务，但是其只关心第一个完成的任务和结果，比如，我们要获取某城市当天天气情况的服务信息，
 * 在该服务中，我们需要调用不同的服务提供商接口，最快返回的那条数据将会是显示在APP或者Web前端的天气情况信息，这样做的好处是可以提供系统响应速度，
 * 提升用户体验，下面通过一个简单的例子来了了解一下invokeAny的使用。
 * <p>
 * invokeAny是一个阻塞方法，它会一直等待直到有一个任务完成，运行上面的程序会看到如下的结果。
 * Task:3 completed in Thread Thread[pool-1-thread-2,5,main]
 * Result:3
 * <p>
 * 在ExecutorService中还提供了invokeAny的重载方法，该方法允许执行任务的超时设置。
 * <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
 */
public class FutureExample6 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //定义一批任务
        List<Callable<Integer>> callables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            callables.add(() -> {
                int random = ThreadLocalRandom.current().nextInt(30);
                //随机休眠，模拟不同接口访问的不同时间开销
                TimeUnit.SECONDS.sleep(random);
                System.out.println("Task:" + random + " completed in Thread " + Thread.currentThread());
                return random;
            });
        }
        //批量执行任务，但是只关心第一个完成的任务返回的结果
        Integer result = executor.invokeAny(callables);
        System.out.println("Result:" + result);
    }

}