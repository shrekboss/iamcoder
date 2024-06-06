package org.coder.concurrency.programming.volatile_;

import java.util.concurrent.CountDownLatch;

/**
 * 验证：volatile 不保证原子性
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class VolatileTest {

    private static volatile int i = 0;
    private static final CountDownLatch latch = new CountDownLatch(10);

    private static void inc() {
        // i++ 的操作是由三步组成的，非原子性操作
        i++;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    inc();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println(i);
    }
}
