package org.coder.concurrency.programming.thread;

/**
 * Thread 的 run 方法不能共享，使用 Runnable 接口则很容易就实现这一点
 *
 * 可能出现的问题
 * 1. 某个号码被略过没有出现
 * 2. 某个号码被多次显示
 * 3. 号码超过了设定的最大值
 *
 * 多个线程对 index 变量同时操作引起的
 * 可以通过 synchronized 关键字解决
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class TicketWindow extends Thread{

    private final String name;
    private static int index = 1;
    private final static int MAX = 50;

    public TicketWindow(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (index <= MAX) {
            System.out.println("柜台：" + name + " 当前的号码是：" + (index++));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Thread 的 run 方法不能共享，使用 Runnable 接口则很容易就实现这一点
     */
    public static void main(String[] args) {

        TicketWindow windowThread1 = new TicketWindow("一号窗口");
        windowThread1.start();

        TicketWindow windowThread2 = new TicketWindow("二号窗口");
        windowThread2.start();

        TicketWindow windowThread3 = new TicketWindow("三号窗口");
        windowThread3.start();

        TicketWindow windowThread4 = new TicketWindow("四号窗口");
        windowThread4.start();
    }
}
