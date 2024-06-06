package org.coder.concurrency.programming.thread.api.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * 1. interrupted 是一个静态方法，用于判断当前线程是否被中断；
 * 2. 和成员方法 isInterrupted 还是有很大的区别，调用该方法会直接擦除掉现成的 interrupt 标识
 * <p>
 * 需要注意的是，如果当前线程被打断了，那么第一次调用 interrupted 方法会返回 true，并且立即擦除了 interrupt 标识；
 * 第二次包括以后得调用永远都会返回 false。
 * <p>
 * public boolean isInterrupted() {
 * return isInterrupted(true); // ClearInterrupted:true
 * }
 * private native boolean isInterrupted(boolean ClearInterrupted);
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadInterrupted {

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(() -> {
            while (true) {
                // 在很多的 false 包围中发现了一个 true
                System.out.println(Thread.interrupted());
            }
        });
        thread.setDaemon(true);
        thread.start();

        // shortly block make sure the thread is started!!!
        TimeUnit.MILLISECONDS.sleep(2);
        thread.interrupt();
    }
}
