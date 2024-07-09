package org.coder.concurrency.programming.pattern._18_master_slave._reusable;

import java.util.concurrent.Future;

/**
 * 对 Master-Slave模式Slave参与者的抽象。
 *
 * @param <T> 子任务类型
 * @param <V> 子任务处理结果类型
 */
public interface SlaveSpec<T, V> {
    /**
     * 用于Master向其提交一个子任务。
     *
     * @param task 子任务
     * @return 可借以获取子任务处理结果的 Promise 实例。
     * @throws InterruptedException
     */
    Future<V> submit(final T task) throws InterruptedException;

    /**
     * 初始化Slave实例提供的服务
     */
    void init();

    /**
     * 停止Slave实例对外提供的服务
     */
    void shutdown();
}