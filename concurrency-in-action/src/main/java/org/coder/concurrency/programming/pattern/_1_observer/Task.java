package org.coder.concurrency.programming.pattern._1_observer;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface Task<T> {

    /**
     * 任务执行接口，该接口允许有返回值
     */
    T call();
}
