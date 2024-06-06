package org.coder.concurrency.programming.thread.api.priority;

/**
 * 指定的线程优先级大于所在 group 的最大优先级，那么设置的优先级将会失效，取而代之的是 group 的最大优先级
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadPriority2 {

    public static void main(String[] args) {

        ThreadGroup group = new ThreadGroup("test");
        // 线程组的最大优先级是 7
        group.setMaxPriority(7);

        Thread t1 = new Thread(group, "test-group");
        // 企图将线程的优先级设置为 10
        t1.setPriority(10);
        // 企图未遂
        System.out.println(t1.getPriority());
    }
}
