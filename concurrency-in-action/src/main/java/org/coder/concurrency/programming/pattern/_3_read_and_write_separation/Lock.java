package org.coder.concurrency.programming.pattern._3_read_and_write_separation;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Lock {

    /**
     * 获取显示锁，没有获得锁的线程将被阻塞
     */
    void lock() throws InterruptedException;

    void unlock();
}
