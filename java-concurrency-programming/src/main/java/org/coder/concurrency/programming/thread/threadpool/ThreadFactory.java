package org.coder.concurrency.programming.thread.threadpool;

/**
 * 3. 提供创建线程的接口
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface ThreadFactory {

    Thread createThread(Runnable runnable);
}
