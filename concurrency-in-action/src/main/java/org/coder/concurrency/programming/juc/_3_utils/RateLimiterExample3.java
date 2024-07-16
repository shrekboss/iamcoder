package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RateLimiterBucket已经完成，现在简单模拟一下对该限速漏桶的使用。
 * <p>
 * 由于上面的程序足够简单，所以这里就不再做过多解释，下面运行一下该程序，大家会看到消息进入漏桶或者被降权处理，以及被匀速处理的全过程。
 */
public class RateLimiterExample3 {

    private static final AtomicInteger data = new AtomicInteger(0);
    private static final RateLimiterBucket bucket = new RateLimiterBucket();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    bucket.submitRequest(data.getAndIncrement());
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true) {
                    bucket.handleRequest(System.out::println);
                }
            }).start();
        }
    }

}