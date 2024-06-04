package org.coder.concurrency.programming.thread.sync;

import java.util.concurrent.TimeUnit;

/**
 * synchronized 关键字的缺陷
 * 1. 无法控制阻塞时长
 * 2. 阻塞不可以中断(synchronized 不可以被中断)
 *
 * @see org.coder.concurrency.programming.thread.sync.explicitlock.BooleanLockTest 解决办法：通过 synchronizedI(隐式锁) 实现显式锁功能
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class SynchronizedDefect {

    public synchronized void syncMethod() {
        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedDefect defect = new SynchronizedDefect();
        Thread t1 = new Thread(defect::syncMethod, "T1");
        // make sure the t1 started!
        t1.start();
        TimeUnit.MILLISECONDS.sleep(2);

        // T2 线程启动执行 syncMethod 方法时会进入阻塞，T2 什么时候能够获取 syncMethod 的执行完全取决于 T1 何时对其释放
        // 无法做到定时获得执行权
        // 1. 无法控制阻塞时长
        Thread t2 = new Thread(defect::syncMethod, "T2");
        t2.start();

        // 2. 阻塞不可以中断(synchronized 不可以被中断), 放开下面注释代码可以验证 synchronized 第二点缺陷
        // make sure the T2 started.
//        TimeUnit.MILLISECONDS.sleep(2);
//        t2.interrupt();
//        System.out.println(t2.isInterrupted());
//        System.out.println(t2.getState());
    }
}
