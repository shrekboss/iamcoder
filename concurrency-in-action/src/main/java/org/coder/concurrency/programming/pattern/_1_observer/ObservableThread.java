package org.coder.concurrency.programming.pattern._1_observer;

/**
 * ObserverThread 是任务监控的关键
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ObservableThread<T> extends Thread implements Observable {

    private final TaskLifecycle<T> lifecycle;

    private final Task<T> task;

    private Cycle cycle;

    /**
     * 指定 Task 的实现，默认情况下使用 EmptyLifecycle
     */
    public ObservableThread(Task<T> task) {
        this(new TaskLifecycle.EmptyLifecycle<>(), task);
    }

    /**
     * 指定 TaskLifecycle 的同时指定 Task
     */
    public ObservableThread(TaskLifecycle<T> lifecycle, Task<T> task) {
        super();
        if (task == null) {
            throw new IllegalArgumentException("The task is required.");
        }
        this.lifecycle = lifecycle;
        this.task = task;
    }

    /**
     * final 防止被重写
     */
    @Override
    public final void run() {
        // 在执行线程逻辑单元的时候，分别触发相应的时间
        this.update(Cycle.STARTED, null, null);
        try {
            this.update(Cycle.RUNNING, null, null);
            T result = this.task.call();
            this.update(Cycle.DONE, result, null);
        } catch (Exception e) {
            this.update(Cycle.ERROR, null, e);
        }
    }

    private void update(Cycle cycle, T result, Exception e) {
        this.cycle = cycle;
        if (lifecycle == null) {
            return;
        }
        try {
            switch (cycle) {
                case STARTED:
                    this.lifecycle.onStart(Thread.currentThread());
                    break;
                case RUNNING:
                    this.lifecycle.onRunning(Thread.currentThread());
                    break;
                case DONE:
                    this.lifecycle.onFinish(Thread.currentThread(), result);
                    break;
                case ERROR:
                    this.lifecycle.onError(Thread.currentThread(), e);
                    break;
            }
        } catch (Exception ex) {
            if (cycle == Cycle.ERROR) {
                throw ex;
            }
        }
    }

    @Override
    public Cycle getCycle() {
        return this.cycle;
    }
}
