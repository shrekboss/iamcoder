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
        // 如果当前任务为 running 并且没有被中断，则其将不断地从 queue 中获取 runnable，然后执行 run 方法
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

    /**
     * 停止当前任务，主要会在线程池的 shutdown 方法中使用
     */
    public void stop() {
        this.running = false;
    }
}
