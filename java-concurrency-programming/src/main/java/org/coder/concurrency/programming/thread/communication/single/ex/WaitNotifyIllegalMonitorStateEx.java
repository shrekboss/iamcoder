package org.coder.concurrency.programming.thread.communication.single.ex;

/**
 * 1. wait 和 notify 必须在同步方法中使用
 * 2. 同步代码的 monitor 必须与执行 wait notify 方法的对象一致
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class WaitNotifyIllegalMonitorStateEx {

    private void testWait() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void testNotify() {
        this.notify();
    }

    private final Object MUTEX = new Object();

    private synchronized void testWait1() {
        try {
            MUTEX.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void testNotify1() {
        MUTEX.notify();
    }

    public static void main(String[] args) {
        WaitNotifyIllegalMonitorStateEx err = new WaitNotifyIllegalMonitorStateEx();
        // java.lang.IllegalMonitorStateException
//        err.testWait();

        // java.lang.IllegalMonitorStateException
//        err.testNotify();

        // java.lang.IllegalMonitorStateException
//        err.testWait1();

        // java.lang.IllegalMonitorStateException
//        err.testNotify1();
    }
}
