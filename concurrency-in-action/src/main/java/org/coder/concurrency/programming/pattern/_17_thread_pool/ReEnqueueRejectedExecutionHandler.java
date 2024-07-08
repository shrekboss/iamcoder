package org.coder.concurrency.programming.pattern._17_thread_pool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 该线程池饱和处理策略支持将提交失败的任务重新放入线程池工作队列。
 */
public class ReEnqueueRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            ;
        }
    }

}