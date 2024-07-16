package org.coder.concurrency.programming.juc._3_utils;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Date;

/**
 * 虽然说RateLimiter主要是用于控制速率的，但是在其内部也有许可证permits的概念，你甚至可以将其理解为单位时间内颁发的许可证数量，
 * RateLimiter不仅允许每一次获取一个许可证的操作，还允许获取超出剩余许可证数量的行为，只不过后者的操作将使得下一次请求为提前的透支付出代价，
 * 下面我们来看一段代码片段。
 * <p>
 * 运行上面的程序，不难发现第二次调用rateLimiter.acquire(2)方法时耗时为2秒，原因就是因为第一次的透支。
 * 0.0		Wed Oct 07 11:57:18 CST 2020
 * 1.978192	Wed Oct 07 11:57:20 CST 2020
 * 0.997708	Wed Oct 07 11:57:21 CST 2020
 * 0.998316	Wed Oct 07 11:57:22 CST 2020
 */
public class RateLimiterExample2 {
    //定义单位时间的速率或者可用的许可证数量
    private static RateLimiter rateLimiter = RateLimiter.create(2.0D);

    public static void main(String[] args) {
        // 第一次就申请4个，这样会透支下一次请求的时间
        System.out.println(rateLimiter.acquire(4) + "\t" + new Date());
        System.out.println(rateLimiter.acquire(2) + "\t" + new Date());
        System.out.println(rateLimiter.acquire(2) + "\t" + new Date());
        System.out.println(rateLimiter.acquire(2) + "\t" + new Date());
    }

}