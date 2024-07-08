package org.coder.concurrency.programming.pattern._11_two_phase_termination.alarm;

public class DelegatingTerminatableThread extends AbstractTerminatableThread {

    private final Runnable task;

    public DelegatingTerminatableThread(Runnable task) {
        this.task = task;
    }

    @Override
    protected void doRun() throws Exception {
        this.task.run();
    }

    public static AbstractTerminatableThread of(Runnable task) {
        DelegatingTerminatableThread ret = new DelegatingTerminatableThread(
                task);
        return ret;
    }
}