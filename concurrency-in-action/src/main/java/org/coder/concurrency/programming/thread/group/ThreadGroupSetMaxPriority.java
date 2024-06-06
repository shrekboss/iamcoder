package org.coder.concurrency.programming.thread.group;

import java.util.concurrent.TimeUnit;

/**
 * 特别说明
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see ThreadGroup#setMaxPriority(int)
 * @since 1.0.0
 */
public class ThreadGroupSetMaxPriority {

    public static void main(String[] args) {
        /*
         * Create a thread group and thread.
         */
        ThreadGroup group1 = new ThreadGroup("group1");
        Thread thread = new Thread(group1, () ->
        {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread");
        thread.setDaemon(true);
        thread.start();
        // group.getMaxPriority()=10
        System.out.println("group.getMaxPriority()=" + group1.getMaxPriority());
        // thread.getPriority()=5
        System.out.println("thread.getPriority()=" + thread.getPriority());

        // *****
        group1.setMaxPriority(3);
        // *****

        // group.getMaxPriority()=3
        System.out.println("group.getMaxPriority()=" + group1.getMaxPriority());
        // thread.getPriority()=5
        // 出现了已经加入该 group 的线程的优先级大于 group 最大优先级的情况，但是后面加入该 group 的线程再不会大于新设置的值：3
        System.out.println("thread.getPriority()=" + thread.getPriority());
    }
}
