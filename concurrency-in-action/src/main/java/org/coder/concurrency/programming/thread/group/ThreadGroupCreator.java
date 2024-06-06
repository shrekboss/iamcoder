package org.coder.concurrency.programming.thread.group;

/**
 * 创建 ThreadGroup
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see ThreadGroup#ThreadGroup(String)
 * @see ThreadGroup#ThreadGroup(ThreadGroup, String)
 * @since 1.0.0
 */
public class ThreadGroupCreator {

    public static void main(String[] args) {
        // 获取当前线程的 group
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        // 定义一个新的 group1
        ThreadGroup group1 = new ThreadGroup("group1");

        // true
        System.out.println(group1.getParent() == currentGroup);

        // 定义一个新的 group2
        ThreadGroup group2 = new ThreadGroup(group1, "group2");
        // true
        System.out.println(group2.getParent() == group1);
    }
}
