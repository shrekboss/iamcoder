package org.coder.concurrency.programming.pattern._10_thread_per_message._reusable;

/**
 * 对任务处理的抽象。
 *
 * @param <T> 表示任务的类型
 * @param <V> 表示任务处理结果的类型
 */
public interface TaskProcessor<T, V> {
    /**
     * 对指定任务进行处理。
     *
     * @param task 任务
     * @return 任务处理结果
     * @throws Exception
     */
    V doProcess(T task) throws Exception;
}