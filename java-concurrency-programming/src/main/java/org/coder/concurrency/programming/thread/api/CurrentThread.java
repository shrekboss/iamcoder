package org.coder.concurrency.programming.thread.api;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class CurrentThread {

    public static void main(String[] args) {

        Thread thread = new Thread() {

            @Override
            public void run() {
                System.out.println(Thread.currentThread() == this);
            }
        };
        thread.start();

        String name = Thread.currentThread().getName();
        System.out.println("main".equals(name));
    }
}
