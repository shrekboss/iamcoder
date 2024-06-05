package org.coder.concurrency.programming.thread.api;

/**
 * Thread.sleep 只会导致当前线程进入指定时间的休眠
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadSleep {

    public static void main(String[] args) {
        Thread t1 = new Thread(
                () -> {
                    long startTime = System.currentTimeMillis();
                    sleep(2000L);
                    long endTime = System.currentTimeMillis();
                    System.out.println(String.format("Total spend %d ms", (endTime - startTime)));
                }
        );

        long startTime = System.currentTimeMillis();
        sleep(3000L);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("Main thread total spend %d ms", (endTime - startTime)));

    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
