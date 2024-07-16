package org.coder.concurrency.programming.juc._5_executorservice;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 5.2.4 Google Guava 的 Future
 * Future虽然为我们提供了一个凭据，但是在未来某个时间节点进行get()操作时仍然会使当前线程进入阻塞，显然这种操作方式并不是十分完美，
 * 因此在Google Guava并发包中提供了对异步任务执行的回调支持，它允许你注册回调函数而不用再通过get()方法苦苦等待异步任务的最终计算结果
 * （Don't Call Us, We'll Call You）。
 * <p>
 * 1.ListenableFuture
 * Guava提供了ListeningExecutorService，使用该ExecutorService提交执行异步任务时将返回ListenableFuture，通过该Future，我们可以注册回调接口。
 * 示例代码如下：
 */
public class ListenableFutureTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        //通过MoreExecutors定义ListeningExecutorService
        ListeningExecutorService decoratorService = MoreExecutors.listeningDecorator(executorService);
        //提供异步任务并且返回ListenableFuture
        ListenableFuture<String> listenableFuture = decoratorService.submit(() -> {
            TimeUnit.SECONDS.sleep(10);
            return "I am the result";
        });
        //注册回调函数，待任务执行完成后，该回调函数将被调用执行
        listenableFuture.addListener(() -> {
            System.out.println("The task completed.");
            try {
                System.out.println("The task result:" + listenableFuture.get());
                decoratorService.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }, decoratorService);
    }

}