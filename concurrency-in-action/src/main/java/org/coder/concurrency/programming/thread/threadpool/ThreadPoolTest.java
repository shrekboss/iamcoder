package org.coder.concurrency.programming.thread.threadpool;

import java.util.concurrent.TimeUnit;

/**
 * 9. 自定义线程池测试测试类
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {
        final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 1000);
        for (int i = 0; i < 20; i++)
            threadPool.execute(() ->
            {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName() + " is running and done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        for (; ; ) {
            // 不断输出线程池的信息
            System.out.println("getActiveCount:" + threadPool.getActiveCount());
            System.out.println("getQueueSize:" + threadPool.getQueueSize());
            System.out.println("getCoreSize:" + threadPool.getCoreSize());
            System.out.println("getMaxSize:" + threadPool.getMaxSize());
            System.out.println("======================================");
            TimeUnit.SECONDS.sleep(5);
        }

        // 以下代码是为了验证：线程池的销毁功能
//        TimeUnit.SECONDS.sleep(12);
//        threadPool.shutdown();
//
//        // 使用 main 线程 join，方便通过工具观察线程堆栈信息
//        Thread.currentThread().join();
    }
}
