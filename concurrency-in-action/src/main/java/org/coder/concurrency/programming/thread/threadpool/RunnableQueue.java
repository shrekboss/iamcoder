package org.coder.concurrency.programming.thread.threadpool;

/**
 * 2. 用任务队列，主要用户缓存提交到线程池中的任务
 * 于存放提交的 Runnable，队列是一个 BlockedQueue，并且有 limit 的限制
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface RunnableQueue {

    /**
     * 当前新的任务进来时首先会 offer 到队列中
     */
    void offer(Runnable runnable);

    /**
     * 工作线程通过 take 方法获取 Runnable
     */
    Runnable take() throws InterruptedException;

    /**
     * 获取任务队列中任务的数量
     */
    int size();
}
