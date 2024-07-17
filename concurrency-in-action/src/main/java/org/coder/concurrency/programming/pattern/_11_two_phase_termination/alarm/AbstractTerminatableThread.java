package org.coder.concurrency.programming.pattern._11_two_phase_termination.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可停止的抽象线程。
 * <p>
 * 模式角色：Two-phaseTermination.AbstractTerminatableThread
 */
public abstract class AbstractTerminatableThread extends Thread implements Terminatable {

    final static Logger logger = LoggerFactory.getLogger(AbstractTerminatableThread.class);
    private final boolean DEBUG = true;

    // 模式角色：Two-phaseTermination.TerminationToken
    public final TerminationToken terminationToken;

    /**
     * 一个可停止线程实例
     */
    public AbstractTerminatableThread() {
        this(new TerminationToken());
    }

    /**
     * 多个可停止线程实例
     * @param terminationToken 线程间共享的线程终止标志实例
     */
    public AbstractTerminatableThread(TerminationToken terminationToken) {
        this.terminationToken = terminationToken;
        terminationToken.register(this);
    }

    /**
     * 留给子类实现其线程处理逻辑。
     *
     * @throws Exception
     */
    protected abstract void doRun() throws Exception;

    /**
     * 留给子类实现。用于实现线程停止后的一些清理动作。
     *
     * @param cause
     */
    protected void doCleanup(Exception cause) {
        // 什么也不做
    }

    /**
     * 留给子类实现。用于执行线程停止所需的操作。
     */
    protected void doTerminiate() {
        // 什么也不做
    }

    /**
     * 执行阶段
     */
    @Override
    public void run() {
        Exception ex = null;
        try {
            for (; ; ) {

                // 在执行线程的处理逻辑前先判断线程停止的标志。
                if (terminationToken.isToShutdown()
                        && terminationToken.reservations.get() <= 0) {
                    break;
                }
                doRun();
            }

        } catch (Exception e) {
            // 使得线程能够响应interrupt调用而退出
            ex = e;
            if (e instanceof InterruptedException) {
                if (DEBUG) {
                    logger.debug(String.valueOf(e));
                }
            } else {
                logger.error("", e);
            }
        } finally {
            try {
                doCleanup(ex);
            } finally {
                terminationToken.notifyThreadTermination(this);
            }
        }
    }

    @Override
    public void interrupt() {
        terminate();
    }

    /**
     * 准备阶段
     * 请求停止线程。
     */
    @Override
    public void terminate() {
        terminationToken.setToShutdown(true);
        try {
            doTerminiate();
        } finally {

            // 若无待处理的任务，则试图强制终止线程
            if (terminationToken.reservations.get() <= 0) {
                super.interrupt();
            }
        }
    }

    /**
     * 准备阶段
     * 请求停止线程。
     */
    public void terminate(boolean waitUtilThreadTerminated) {
        terminate();
        if (waitUtilThreadTerminated) {
            try {
                this.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}