package org.coder.concurrency.programming.thread.api.normalshutdown;

import java.util.concurrent.TimeUnit;

/**
 * 捕获中断信号关闭线程
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class InterruptThreadExit2 implements Runnable {

    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(new InterruptThreadExit2());
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        System.out.println("System will be shutdown!!!");
        thread.interrupt();
    }

    @Override
    public void run() {
        System.out.println("I will start work!");
        for (; ; ) {
            //working
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("I will be exiting!!");
    }
}
