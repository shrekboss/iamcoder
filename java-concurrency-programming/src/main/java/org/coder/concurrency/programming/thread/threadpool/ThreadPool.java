package org.coder.concurrency.programming.thread.threadpool;

/**
 * 1. 定义线程池基本操作和方法
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface ThreadPool {

    /**
     * 提交任务到线程池
     */
    void execute(Runnable runnable);

    /**
     * 关闭线程池
     */
    void shutdown();

    /**
     * 获取线程池的初始化大小
     */
    int getInitSize();

    /**
     * 获取线程池最大的线程数
     */
    int getMaxSize();

    /**
     * 获取线程池的核心线程数量
     */
    int getCoreSize();

    /**
     * 获取线程池中用于缓存任务队列大小
     */
    int getQueueSize();

    /**
     * 获取线程池中活跃线程的数量
     */
    int getActiveCount();

    /**
     * 判断线程池是否已经被 shutdown
     */
    boolean isShutdown();
}
