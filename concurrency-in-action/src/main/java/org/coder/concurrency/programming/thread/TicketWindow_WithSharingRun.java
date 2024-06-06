package org.coder.concurrency.programming.thread;

/**
 * Thread 的 run 方法不能共享，使用 Runnable 接口则很容易就实现这一点
 * <p>
 * 可能出现的问题
 * 1. 某个号码被略过没有出现
 * 2. 某个号码被多次显示
 * 3. 号码超过了设定的最大值
 * <p>
 * 多个线程对 index 变量同时操作引起的
 * 可以通过 synchronized 关键字解决
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TicketWindow_WithSharingRun implements Runnable {

    private final static int MAX = 50;
    private final static Object MUTEX = new Object();
    private int index = 1;

    /**
     * Thread 的 run 方法不能共享，使用 Runnable 接口则很容易就实现这一点
     */
    public static void main(String[] args) {
        final TicketWindow_WithSharingRun task = new TicketWindow_WithSharingRun();

        Thread windowThread1 = new Thread(task, "一号窗口");
        Thread windowThread2 = new Thread(task, "二号窗口");
        Thread windowThread3 = new Thread(task, "三号窗口");
        Thread windowThread4 = new Thread(task, "四号窗口");

        windowThread1.start();
        windowThread2.start();
        windowThread3.start();
        windowThread4.start();
    }

    @Override
    public void run() {
//        synchronized (MUTEX) {
        while (index <= MAX) {
            System.out.println("柜台：" + Thread.currentThread().getName() + " 当前的号码是：" + (index++));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        }
    }
}
