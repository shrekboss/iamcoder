package org.coder.concurrency.programming.thread.group;

import java.util.concurrent.TimeUnit;

/**
 * ThreadGroup 基本操作
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadGroupBasic {

    public static void main(String[] args) throws InterruptedException {
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

        //make sure the thread is started
        TimeUnit.MILLISECONDS.sleep(1);

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        // activeCount=3
        System.out.println("activeCount=" + mainGroup.activeCount());
        // activeGroupCount=1
        System.out.println("activeGroupCount=" + mainGroup.activeGroupCount());
        // getMaxPriority=10
        System.out.println("getMaxPriority=" + mainGroup.getMaxPriority());
        // getName=main
        System.out.println("getName=" + mainGroup.getName());
        // getParent=java.lang.ThreadGroup[name=system,maxpri=10]
        System.out.println("getParent=" + mainGroup.getParent());
        /**
         * java.lang.ThreadGroup[name=main,maxpri=10]
         *     Thread[main,5,main]
         *     Thread[Monitor Ctrl-Break,5,main]
         *     java.lang.ThreadGroup[name=group1,maxpri=10]
         *         Thread[thread,5,group1]
         */
        mainGroup.list();
        System.out.println("--------------------------");
        // parentOf=true 判断当前 group 是不是给定 group 的父 group
        System.out.println("parentOf=" + mainGroup.parentOf(group1));
        // parentOf=true 如果给定的 group 就是自己本身，那么该方法也会返回 true
        System.out.println("parentOf=" + mainGroup.parentOf(mainGroup));
    }
}
