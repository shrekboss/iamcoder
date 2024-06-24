package org.coder.concurrency.programming.thread.hook;

import java.util.concurrent.TimeUnit;

/**
 * 没有设置 Thread 的 {@link java.lang.Thread.UncaughtExceptionHandler}
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see Thread#getUncaughtExceptionHandler()
 * @see ThreadGroup#uncaughtException(Thread, Throwable)
 * @since 1.0.0
 */
public class EmptyExceptionHandler {

    public static void main(String[] args) {
        /**
         * 没有设置默认的 Handler，也没有对 thread 指定 handler，因此当 thread 出现异常时，会向上寻找 Group
         * 的 uncaughtException 方法
         *
         * 线程出现异常 -> Main Group -> System Group -> System.err
         */

        // get current thread's thread group
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        System.out.println(mainGroup.getName());
        System.out.println(mainGroup.getParent());
        System.out.println(mainGroup.getParent().getParent());

        final Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // here will throw unchecked exception.
            System.out.println(1 / 0);
        }, "Test-Thread");
        thread.start();
    }
}
