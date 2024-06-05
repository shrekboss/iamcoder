package org.coder.concurrency.programming.thread.threadpool;

import java.util.LinkedList;

/**
 * 7. RunnableQueue 队列的实现
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class LinkedRunnableQueue implements RunnableQueue {

    private final int limit;

    private final DenyPolicy denyPolicy;

    private final LinkedList<Runnable> runnableList = new LinkedList<>();

    private final ThreadPool threadPool;

    public LinkedRunnableQueue(int limit, DenyPolicy denyPolicy, ThreadPool threadPool) {
        this.limit = limit;
        this.denyPolicy = denyPolicy;
        this.threadPool = threadPool;
    }


    @Override
    public void offer(Runnable runnable) {
        synchronized (runnableList) {
            if (runnableList.size() >= limit) {
                denyPolicy.reject(runnable, threadPool);
            } else {
                runnableList.addLast(runnable);
                runnableList.notifyAll();
            }
        }
    }

    @Override
    public Runnable take() throws InterruptedException {
        synchronized (runnableList) {
            while (runnableList.isEmpty()) {
                try {
                    runnableList.wait();
                } catch (InterruptedException e) {
                    throw e;
                }
            }

            return runnableList.removeFirst();
        }
    }

    @Override
    public int size() {
        synchronized (runnableList) {
            return runnableList.size();
        }
    }
}
