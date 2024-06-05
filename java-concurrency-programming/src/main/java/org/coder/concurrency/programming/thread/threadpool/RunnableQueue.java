package org.coder.concurrency.programming.thread.threadpool;

/**
 * 2. 用于存放提交的 Runnable，队列是一个 BlockedQueue，并且有 limit 的限制
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface RunnableQueue {

    void offer(Runnable runnable);

    Runnable take() throws InterruptedException;

    int size();
}
