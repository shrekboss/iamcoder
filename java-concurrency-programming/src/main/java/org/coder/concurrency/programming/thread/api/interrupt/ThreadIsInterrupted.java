package org.coder.concurrency.programming.thread.api.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * 判断当前线程是否被中断，该方法仅仅是对 interrupt 标识的一个判断
 *
 *     public boolean isInterrupted() {
 *         return isInterrupted(false); // ClearInterrupted:false
 *     }
 * private native boolean isInterrupted(boolean ClearInterrupted);
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadIsInterrupted {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    // do nothing, just empty loop!
                }
            }
        };
        thread.start();
        // short block and make sure thread is started!!!
        TimeUnit.MILLISECONDS.sleep(2);
        System.out.printf("Thread is interrupted ? %s%n", thread.isInterrupted());
        thread.interrupt();
        System.out.printf("Thread is interrupted ? %s%n", thread.isInterrupted());
    }
}
