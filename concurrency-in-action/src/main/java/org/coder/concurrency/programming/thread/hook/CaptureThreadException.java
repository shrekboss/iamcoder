package org.coder.concurrency.programming.thread.hook;

import java.util.concurrent.TimeUnit;

/**
 * UncaughtExceptionHandler
 * 当线程在运行过程中出现异常时，会回调 UncaughtExceptionHandler 接口，从而得知是哪个线程在运行时出错，以及出现什么样的错误。
 * <p>
 * 解决的问题：线程在执行单元中是不允许抛出 checked 异常的，而且线程运行在自己的上下文中，派生它的线程无法直接获得它运行中出现的异常信息。
 * </p>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see Thread.UncaughtExceptionHandler
 * @see Thread#setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)：为某个特定线程指定 UncaughtExceptionHandler
 * @see Thread#setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)：设置全局的 UncaughtExceptionHandler
 * @see Thread#getUncaughtExceptionHandler()：获取特定线程的 UncaughtExceptionHandler
 * @see Thread#getDefaultUncaughtExceptionHandler()：获取全局的 UncaughtExceptionHandler
 * @since 1.0.0
 */
public class CaptureThreadException {

    public static void main(String[] args) throws InterruptedException {

//        Thread thread1 = new Thread(() -> {
//            System.out.println(1/0);
//        });
//        thread1.start();
//
//        TimeUnit.SECONDS.sleep(1);
//        System.out.println("================================================");

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println(t.getName() + " occur exception ");
            e.printStackTrace();
        });

        final Thread thread2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // here will throw unchecked exception.
            System.out.println(1 / 0);
        }, "Test-Thread");

        thread2.start();
    }
}
