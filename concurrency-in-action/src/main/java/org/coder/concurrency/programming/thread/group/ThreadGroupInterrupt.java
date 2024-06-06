package org.coder.concurrency.programming.thread.group;

import java.util.concurrent.TimeUnit;

/**
 * ThreadGroup 的 Interrupt
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see ThreadGroup#interrupt()
 * 1. interrupt 一个 thread group 会导致该 group 中所有的 active 线程都被 interrupt，也就是说该 group 中的
 * 每一个线程的 Interrupt 标识都被设置了
 * 2. 会递归获取子 group
 * @since 1.0.0
 */
public class ThreadGroupInterrupt {

    public static void main(String[] args) throws InterruptedException {
        ThreadGroup group = new ThreadGroup("TestGroup");

        new Thread(group, () ->
        {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                } catch (InterruptedException e) {
                    //received interrupt SIGNAL and clear quickly
                    break;
                }
            }
            System.out.println("t1 will exit.");
        }, "t1").start();

        new Thread(group, () ->
        {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    //received interrupt SIGNAL and clear quickly
                    break;
                }
            }
            System.out.println("t2 will exit.");
        }, "t2").start();

        //make sure all of above threads started.
        TimeUnit.MILLISECONDS.sleep(2);

        group.interrupt();
    }
}
