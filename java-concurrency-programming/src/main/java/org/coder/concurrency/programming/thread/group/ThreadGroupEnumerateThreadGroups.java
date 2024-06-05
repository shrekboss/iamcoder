package org.coder.concurrency.programming.thread.group;

import java.util.concurrent.TimeUnit;

/**
 * 复制 ThreadGroup 数组，主要复制当前 ThreadGroup 的子 Group
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see ThreadGroup#enumerate(ThreadGroup[])
 * @see ThreadGroup#enumerate(ThreadGroup[], boolean)
 * @since 1.0.0
 */
public class ThreadGroupEnumerateThreadGroups {

    public static void main(String[] args) throws InterruptedException {

        ThreadGroup group1 = new ThreadGroup("group1");
        ThreadGroup group2 = new ThreadGroup(group1, "group2");

        TimeUnit.MILLISECONDS.sleep(2);
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        ThreadGroup[] list = new ThreadGroup[mainGroup.activeCount()];
        // 2 group1 group2
        int recurseSize = mainGroup.enumerate(list);
        System.out.println(recurseSize);

        // 1 group1
        recurseSize = mainGroup.enumerate(list, false);
        System.out.println(recurseSize);
    }
}
