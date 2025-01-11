package org.coder.concurrency.programming.juc._3_utils;

import com.google.common.util.concurrent.Monitor;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 3.11.3 令牌环桶算法
 * 令牌环桶与漏桶比较类似，漏桶对水流进入的速度不做任何限制，它只对水流出去的速率是有严格控制的，
 * 令牌环桶则与之相反，在对某个资源或方法进行调用之前首先要获取到令牌也就是获取到许可证才能进行相关的操作，否则将不被允许。
 * 比如，常见的互联网秒杀抢购等活动，商品的数量是有限的，为了防止大量的并发流量进入系统后台导致普通商品消费出现影响，
 * 我们需要对类似这样的操作增加令牌授权、许可放行等操作，这就是所谓的令牌环桶。
 * 1.根据固定的速率向桶里提交数据。
 * 2.新加数据时如果超过了通的容量，则请求将会被直接拒绝。
 * 3.如果令牌不足，则请求也会被拒绝（请求可以再次尝试）。
 * <p>
 * 下面写一个简单的程序，模拟互联网商品抢购的方式，为大家介绍令牌环桶的使用方法。
 * <p>
 * 下面的代码也是非常简单的，但是我们在进行商品订购操作的时候调用了RateLimiter的另一个方法rateLimiter.tryAcquire(100, TimeUnit.milliseconds)，
 * 该方法用于尝试获取令牌，最多等待100毫秒的时间，如果仍然失败则会抛出订购失败的错误，客户端可以再次尝试。
 */
public class RateLimiterTokenBucket {
    //当前活动商品数量
    private final static int MAX = 1000;
    //订单编号，订单成功之后会产生一个新的订单
    private int orderID;
    //单位时间内只允许10个用户能够抢购到商品，也就是说订单服务将会被匀速地调用
    private final RateLimiter rateLimiter = RateLimiter.create(10.0D);
    private Monitor bookOrderMonitor = new Monitor();

    //当前商品售完的时候就会抛出该异常
    static class NoProductionException extends Exception {

        private static final long serialVersionUID = 903278426707008684L;

        public NoProductionException(String message) {
            super(message);
        }
    }

    //当抢购商品失败时就会抛出该异常
    static class OrderFailedException extends Exception {

        private static final long serialVersionUID = -7129680647295689925L;

        public OrderFailedException(String message) {
            super(message);
        }
    }

    //前台用户下单，但是只允许匀速地进行订单服务调用
    public void bookOrder(Consumer<Integer> consumer) throws NoProductionException, OrderFailedException {
        //如果当前商品有库存则执行抢购操作
        if (bookOrderMonitor.enterIf(bookOrderMonitor.newGuard(() -> orderID < MAX))) {
            try {
                //抢购商品，最多等待100毫秒
                if (!rateLimiter.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                    //如果在100毫秒之内抢购仍然失败，则抛出订购失败的异常，客户端可以尝试重新操作
                    throw new OrderFailedException("book order failed, please try again later.");
                }
                //执行订单订购操作
                orderID++;
                consumer.accept(orderID);
            } finally {
                bookOrderMonitor.leave();
            }
        } else {
            //如果当前商品已经没有库存，则抛出没有商品的异常，该异常将不会再次进行尝试动作
            throw new NoProductionException("No available production now.");
        }
    }
}