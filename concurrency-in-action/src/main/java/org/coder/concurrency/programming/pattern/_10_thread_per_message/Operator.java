package org.coder.concurrency.programming.pattern._10_thread_per_message;

import org.coder.concurrency.programming.thread.threadpool.BasicThreadPool;
import org.coder.concurrency.programming.thread.threadpool.ThreadPool;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Operator {

    private final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 1000);

    public void call(String business) {
        TaskHandler taskHandler = new TaskHandler(new Request(business));
        threadPool.execute(taskHandler);
    }
}
