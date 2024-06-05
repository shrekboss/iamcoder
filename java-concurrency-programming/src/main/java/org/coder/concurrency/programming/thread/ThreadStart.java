package org.coder.concurrency.programming.thread;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see Thread#start0()
 * @since 1.0.0
 */
public class ThreadStart {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        // 验证点1：抛出java.lang.IllegalThreadStateException，重复启动，但是此时线程是处于运行状态的
//         thread.start();

        TimeUnit.SECONDS.sleep(2);

        // 验证点2：抛出java.lang.IllegalThreadStateException ，该启动时不允许的，此时没有线程，因为线程的生命周期已经被终结
//        thread.start();
    }
}
