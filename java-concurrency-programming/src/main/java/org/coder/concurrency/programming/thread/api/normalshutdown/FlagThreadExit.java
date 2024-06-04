package org.coder.concurrency.programming.thread.api.normalshutdown;

import java.util.concurrent.TimeUnit;

/**
 * 使用 volatile 开关控制
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class FlagThreadExit {

    static class MyTask extends Thread {

        private volatile boolean closed = false;

        @Override
        public void run() {
            System.out.println("I will start work!");

            while (!closed && !isInterrupted()) {
                // working
            }
            System.out.println("I will be exiting!!");
        }

        public void closed() {
            this.closed = true;
            this.interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask task = new MyTask();
        task.start();
        TimeUnit.SECONDS.sleep(5);
        System.out.println("System will be shutdown!!!");
        task.closed();
    }
}
