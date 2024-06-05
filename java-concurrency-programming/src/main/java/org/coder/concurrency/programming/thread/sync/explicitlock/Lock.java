package org.coder.concurrency.programming.thread.sync.explicitlock;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Lock {

    void lock() throws InterruptedException;

    void lock(long mills) throws InterruptedException, TimeoutException;

    void unlock();

    List<Thread> getBlockedThreads();
}
