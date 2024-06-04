package org.coder.concurrency.programming.thread.api.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * 1. 如果一个线程被 interrupt，那么它的 flag 将被设置；
 * 2. 如果当前线程正在执行可中断方法被阻塞时，调用 interrupt 方法将其中断，会导致 flag 被清除；
 * 3. 如果一个线程已经是死亡状态，那么尝试对其的 interrupt 会被直接忽略。
 *
 * 可中断方法：
 * 1. Object 的 wait 方法(3个)
 * 2. Thread 的 sleep 方法(2个)
 * 3. Thread 的 join 方法(3个)
 * 4. InterruptibleChannel 的 IO 操作
 * 5. Selected 的 wakeup 方法
 * 5. 其他方法
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadInterrupt {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(
                () -> {
                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Oh. i am be interrupted!");
                    }
                }
        );
        thread.start();

        // short block and make sure thread is started!!!
        TimeUnit.MILLISECONDS.sleep(2);
        thread.interrupt();
    }
}
