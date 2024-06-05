package org.coder.concurrency.programming.thread.threadpool;

/**
 * 6. 是 Runnable 的一个实现，主要用户线程池内部，该类会使用到 RunnableQueue，然后不断地从 queue 中取出某个 runnable，并运行 runnable
 * 的 run 方法
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class InternalTask implements Runnable {

    private final RunnableQueue runnableQueue;

    private volatile boolean running = true;

    public InternalTask(RunnableQueue runnableQueue) {
        this.runnableQueue = runnableQueue;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = runnableQueue.take();
                task.run();

            } catch (InterruptedException e) {
                running = false;
                break;
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
