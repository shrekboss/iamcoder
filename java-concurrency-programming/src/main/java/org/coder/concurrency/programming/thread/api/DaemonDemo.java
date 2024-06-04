package org.coder.concurrency.programming.thread.api;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class DaemonDemo {

    public static void main(String[] args) throws InterruptedException {

        // 1. Main 线程开始
        Thread thread = new Thread(
            () -> {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        );
        // 2. 将 thread 设置为守护线程, 不设置，该程序无法结束
//        thread.setDaemon(true);
        // 3. 启动 thread 线程
        thread.start();
        Thread.sleep(2000L);
        System.out.println("Main thread finished lifecycle");
        // 4. Main 线程结束

        //
        System.out.println(thread.isDaemon() ? "守护线程" : "正常线程");
    }
}
