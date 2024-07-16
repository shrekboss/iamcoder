package org.coder.concurrency.programming.juc._3_utils;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Date;

/**
 * 3.11 Guava之RateLimiter详解
 * RateLimiter，顾名思义就是速率(Rate)限流器(Limiter)，事实上它的作用正如名字描述的那样，经常用于进行流量、访问等的限制，
 * 这一点与3.4节中介绍过的Semaphore非常类似，但是它们的关注点却完全不同，RateLimiter关注却完全不同，
 * RateLimiter关注的是在单位时间里对资源的操作速率(在RateLimiter内部也存在许可证permits的概念，因此可以理解为在单位时间内允许颁发的许可证数量)，
 * 而Semaphore则关注的是在同一时间内最多允许多少个许可证可被使用，它不关心速率而只关心个数。
 * <p>
 * 3.11.1 RateLimiter的基本使用
 * 我们先来快速查看一个实例，假设我们只允许某个方法在单位时间内(1秒)被调用0.5次，也就是说该方法的访问速率为0.5/秒，即2秒内只有一次对该方法的访问操作，
 * 示例代码如下所示。
 * <p>
 * 运行上面的程序，我们会发现该testRateLimiter()只能每2秒执行一次(当然时间精度没有做到绝对的严格)。
 * <p>
 * 看到这里我们不难发现，RateLimiter的功能非常强大，比如，要想开发一个程序向数据库写入数据的条目，向中间件服务器中发送的消息个数、对某个远程TCP端口发送的字节数等，
 * 若这些操作的速率无法被控制，则可能会引起数据库拒绝服务、中间件宕机、TCP服务器端口无法响应等问题，而借助于RateLimiter就可以很好地帮助我们进行匀速的控制。
 * <p>
 * 下面的程序中采用的是单线程的方式对RateLimiter进行操作，那么在多线程的情况之下，RateLimiter是否还能做到将方法的访问速率控制在0.5次/秒呢?
 * 答案是可以的，我们将程序稍作修改，如下所示。
 */
public class RateLimiterExample1 {
    //定义一个Rate Limiter，单位时间(默认为秒)的设置为0.5
    private static RateLimiter rateLimiter = RateLimiter.create(0.5);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(RateLimiterExample1::testRateLimiter).start();
        }
        for (; ; ) {
            testRateLimiter();
        }
    }

    //测试rate limiter
    private static void testRateLimiter() {
        // 在访问该方法之前首先要进行rateLimiter的获取，返回值为实际的获取等待开销时间
        double elapsedSecond = rateLimiter.acquire();
        System.out.println(Thread.currentThread() + ": elapsed seconds: " + elapsedSecond + "\t" + new Date());
    }

}