package org.coder.design.patterns._3_programming_specification._2_testability;

import org.coder.design.patterns._3_programming_specification._2_testability.simulate.RedisDistributedLock;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TransactionLock {

    public boolean lock(String id) {
        RedisDistributedLock.getSingletonInstance().lock();
        return true;
    }

    public boolean unlock() {
        RedisDistributedLock.getSingletonInstance().unlock();
        return false;
    }
}
