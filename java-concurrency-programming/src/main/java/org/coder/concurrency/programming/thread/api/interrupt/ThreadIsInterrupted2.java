package org.coder.concurrency.programming.thread.api.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * 判断当前线程是否被中断，该方法仅仅是对 interrupt 标识的一个判断
 * 可中断方法捕获到了中断信号(signal)之后，也就是捕获了 InterruptedException 异常之后会擦除掉 interrupt 的标识
 *
 *      public boolean isInterrupted() {
 *          return isInterrupted(false); // ClearInterrupted:false
 *      }
 * private native boolean isInterrupted(boolean ClearInterrupted);
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadIsInterrupted2 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        // ignore the exception
                        // here the interrupt flag will be clear
                        System.out.printf("I am be interrupted ? %s%n", isInterrupted());
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        // short block and make sure thread is started!!!
        TimeUnit.MILLISECONDS.sleep(2);
        System.out.printf("Thread is interrupted ? %s%n", thread.isInterrupted());
        thread.interrupt();
        TimeUnit.MILLISECONDS.sleep(2);
        System.out.printf("Thread is interrupted ? %s%n", thread.isInterrupted());
    }
}
