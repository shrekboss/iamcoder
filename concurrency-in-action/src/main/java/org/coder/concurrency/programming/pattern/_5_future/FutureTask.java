package org.coder.concurrency.programming.pattern._5_future;

/**
 * (what) 主要用于接受任务被完成的通知
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class FutureTask<T> implements Future<T> {

    private T result;
    private boolean isDone = false;
    private final Object LOCK = new Object();

    @Override
    public T get() throws InterruptedException {

        synchronized (LOCK) {
            while (!isDone) {
                LOCK.wait();
            }

            return result;
        }
    }

    protected void finish(T result) {
        synchronized (LOCK) {
            // balking 设计模式
            if (isDone) {
                return;
            }
            this.result = result;
            this.isDone = true;
            LOCK.notifyAll();
        }
    }

    @Override
    public boolean done() {
        return isDone;
    }
}
