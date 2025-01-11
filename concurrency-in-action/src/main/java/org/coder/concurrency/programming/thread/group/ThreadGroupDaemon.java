package org.coder.concurrency.programming.thread.group;

import java.util.concurrent.TimeUnit;

/**
 * 守护 ThreadGroup
 * 将一个 ThreadGroup 设置为 daemon，并不影响线程的 daemon 属性，如果一个 ThreadGroup 的 daemon 被设置为 true，那么在 group 中
 * 没有任何 active 线程的时候该 group 将自定 destroy
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see ThreadGroup#setDaemon(boolean)
 * @since 1.0.0
 */
public class ThreadGroupDaemon {

    public static void main(String[] args) throws InterruptedException {
        ThreadGroup group1 = new ThreadGroup("Group1");
        new Thread(group1, () ->
        {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group1-thread1").start();

        ThreadGroup group2 = new ThreadGroup("Group2");
        new Thread(group2, () ->
        {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "group2-thread1").start();

        //设置daemon为true
        group2.setDaemon(true);

        TimeUnit.SECONDS.sleep(3);
        System.out.println(group1.isDestroyed());
        System.out.println(group2.isDestroyed());
    }
}
