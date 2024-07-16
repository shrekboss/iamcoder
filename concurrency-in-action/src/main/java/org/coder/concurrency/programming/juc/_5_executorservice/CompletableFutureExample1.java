package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 5.5 CompletableFuture 详解
 * CompletableFuture是自JDK1.8版本中引入的新的Future，常用于异步编程之中，所谓异步编程，
 * 简单来说就是：“程序运算与应用程序的主线程在不同的线程上完成，并且程序运算的线程能够向主线程通知其进度，以及成功失败与否的非阻塞式的编码方式”，
 * 这句话听起来与前文中学习的ExecutorService提交异步执行任务并没有多大的区别，但是别忘了，
 * 无论是ExecutorService还是CompletionService，都需要主线程主动地获取异步任务执行的最终计算结果，
 * 如此看来，Google Guava所提供的ListenableFuture更符合这段话的描述，但是ListenableFuture无法将计算的结果进行异步任务的级联并行运算，
 * 甚至构成一个异步任务并行运算的pipeline，但是这一切在CompletableFuture中都得到了很好的支持。
 * <p>
 * CompletableFuture实现自CompletionStage接口，可以简单地认为，该接口是同步或者异步任务完成得某个阶段，
 * 它可以是整个任务管道中的最后一个阶段，甚至可以是管道中的某一个阶段，这意味着可以将多个CompletionStage链接在一起形成一个异步任务链，
 * 前置任务执行结束之后会自动触发下一个阶段任务的执行。另外，CompletableFuture还实现了Future接口，所以你可以像使用Future一样使用它。
 * <p>
 * CompletableFuture中包含了50多个方法，这一数字在JDK1.9版本中还得到了进一步的增加，
 * 这些方法可用于Future之间的组合、合并、任务的异步执行，多个Future的并行计算以及任务执行发生异常的错误处理等。
 * <p>
 * CompletableFuture的方法中，大多数入参都是函数式接口，比如Supplier、Function、BiFunction、Consumer等，
 * 因此熟练理解这些函数式接口是灵活使用CompletableFuture的前提和基础，同时CompletableFuture之所以能够异步执行任务，
 * 主要归功于其内部的ExecutorService，默认情况下为ForkJoinPool.commonPool()，当然也允许开发者显式地指定。
 * <p>
 * 5.5.1 CompletableFuture的基本用法
 * 不管怎么说，CompletableFuture首先是一个Future，因此你可以将它当作普通的Future来使用，这也没有什么不妥，
 * 比如我们在前文中学到，ExecutorService如果提交了Runnable类型的任务却又期望得到运算结果的返回，则需要在submit方法中将返回值的引用也作为参数传进去。
 * 笔者不是很喜欢这种API的设计方式，下面的代码将借助CompletableFuture来优雅地解决该问题。
 */
public class CompletableFutureExample1 {

    public static void main(String[] args) {
        //定义Double类型的CompletableFuture
        CompletableFuture<Double> completableFuture = new CompletableFuture<>();
        //提交异步任务
        Executors.newCachedThreadPool().submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                //执行结束
                completableFuture.complete(1245.23D);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //非阻塞获取异步任务的计算结果，很明显，此刻异步任务并未执行结束，那么可以采用默认值的方式（该方法也可以被认为是放弃异步任务的执行结果，但不会取消异步任务的执行）
        assert completableFuture.getNow(0.0D) == 0.0;
        try {
            //阻塞获取异步任务的执行结果，与前文中的Future非常类似
            assert completableFuture.get() == 1245.23D;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}