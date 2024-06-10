package org.coder.design.patterns._3_programming_specification._2_testability.mock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 模拟 RedisDistributedLock 是分布式 Redis 锁是单例模式
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RedisDistributedLock implements Lock {

    private final static RedisDistributedLock INSTANCE = new RedisDistributedLock();

    private RedisDistributedLock() {
    }

    public static RedisDistributedLock getSingletonInstance() {
        return INSTANCE;
    }

    @Override
    public void lock() {
        // ...
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        // ...
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        // ...
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
