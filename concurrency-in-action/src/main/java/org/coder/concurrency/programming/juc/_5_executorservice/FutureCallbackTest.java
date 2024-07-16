package org.coder.concurrency.programming.juc._5_executorservice;

import com.google.common.util.concurrent.*;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 2.FutureCallback
 * 除了ListenableFuture之外，还可以注册FutureCallback，相比前者用Runnable接口作为回调接口，FutureCallback提供的回调方式则更为直观。
 * 示例代码如下：
 * <p>
 * 5.2.5 Future 总结
 * 本节详细介绍了Future和Callable的用法，以及通过Future如何取消正在执行的异步任务，通过get方法如何在未来的某个时间节点获取异步任务最终的运算结果等。
 * <p>
 * Future一般是被用于ExecutorService提交任务之后返回的“凭据”，本节对ExecutorService中所有涉及Future相关的执行方法都做了比较详细的讲解，
 * Java中的Future不支持回调的方式，这显然不是一种完美的做法，调用者需要通过get方法进行阻塞方式的结果获取，
 * 因此在Google Guava工具集中提供了可注册回调函数的方式，用于被动地接受异步任务的执行结果，这样一来，提交异步任务的线程便不用关心如何得到最终的运算结果。
 * <p>
 * 虽然Google Guava的ListenableFuture是一种优雅的解决方案，但是5.5节将要学习的CompletableFuture则更为强大和灵活，目前也已成为使用最广的Future实现之一。
 */
public class FutureCallbackTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ListeningExecutorService decoratorService = MoreExecutors.listeningDecorator(executorService);
        //提交任务返回listenableFuture
        ListenableFuture<String> listenableFuture = decoratorService.submit(() -> {
            TimeUnit.SECONDS.sleep(10);
            return "I am the result";
        });
        //使用Futures增加callback
        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            //任务执行成功会被回调
            @Override
            public void onSuccess(@Nullable String result) {
                System.out.println("The Task completed and result:" + result);
                decoratorService.shutdown();
            }

            //任务执行失败会被回调
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, decoratorService);
    }

}