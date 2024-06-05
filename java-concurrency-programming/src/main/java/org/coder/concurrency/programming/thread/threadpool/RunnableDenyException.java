package org.coder.concurrency.programming.thread.threadpool;

/**
 * 5. RunnableDenyException 是 RuntimeException 的子类，主要用于通知任务提交者，任务队列已无法再接受新的任务
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RunnableDenyException extends RuntimeException {

    public RunnableDenyException(String message) {
        super(message);
    }
}
