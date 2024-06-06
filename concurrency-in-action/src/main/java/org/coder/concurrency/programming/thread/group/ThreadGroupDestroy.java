package org.coder.concurrency.programming.thread.group;

/**
 * ThreadGroup 的 destroy
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @exception IllegalThreadStateException  if the thread group is not
 * empty or if the thread group has already been destroyed.
 * <p>
 * 该方法只针对一个没有任何 active 线程的 group 进行一次destroy 标记，调用该方法的直接结果是在父 group 中将自己移除
 * 如果有 active 线程存在，调用 destroy 方法会抛出异常：
 * @see ThreadGroup#destroy()
 * @since 1.0.0
 */
public class ThreadGroupDestroy {

    public static void main(String[] args) {
        ThreadGroup group = new ThreadGroup("TestGroup");

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
        System.out.println("group.isDestroyed=" + group.isDestroyed());
        mainGroup.list();

        group.destroy();

        System.out.println("group.isDestroyed=" + group.isDestroyed());
        mainGroup.list();
    }
}
