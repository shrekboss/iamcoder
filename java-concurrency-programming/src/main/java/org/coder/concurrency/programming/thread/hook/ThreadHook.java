package org.coder.concurrency.programming.thread.hook;

import java.util.concurrent.TimeUnit;

/**
 * Java 程序注入 Hook
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadHook {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                System.out.println("The hook thread 1 is running.");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("The hook thread 1 will exit.");
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("The hook thread 2 is running.");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("The hook thread 2 will exit.");
        }));

        /**
         * 给 Java 程序注入了两个Hook 线程，在 main 线程中结束，也就是说，JVM中没有了活动的非守护线程，JVM 进程即将退出时，两个 Hook 线程
         * 会被启动并且运行，输出结果：
         *
         * The program will is stopping.
         * The hook thread 2 is running.
         * The hook thread 1 is running.
         * The hook thread 2 will exit.
         * The hook thread 1 will exit.
         */
        System.out.println("The program will is stopping.");
    }
}
