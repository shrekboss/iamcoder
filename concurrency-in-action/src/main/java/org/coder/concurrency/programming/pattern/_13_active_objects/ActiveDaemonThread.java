package org.coder.concurrency.programming.pattern._13_active_objects;

/**
 * (what) ActiveDaemonThread 是一个守护线程，主要是从 queue 中获取 Message 然后执行 execute 方法。
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
class ActiveDaemonThread extends Thread {

    private final ActiveMessageQueue queue;

    public ActiveDaemonThread(ActiveMessageQueue queue) {
        super("ActiveDaemonThread");
        this.queue = queue;
        setDaemon(true);
    }

    @Override
    public void run() {
        for (; ; ) {
            ActiveMessage activeMessage = this.queue.take();
            activeMessage.execute();
        }
    }
}
