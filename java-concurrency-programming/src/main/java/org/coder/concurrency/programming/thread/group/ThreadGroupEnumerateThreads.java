package org.coder.concurrency.programming.thread.group;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 复制 Thread 数组
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @see ThreadGroup#enumerate(Thread[])
 * @see ThreadGroup#enumerate(Thread[], boolean)
 * @since 1.0.0
 */
public class ThreadGroupEnumerateThreads {

    public static void main(String[] args) throws InterruptedException {
        ThreadGroup myGroup = new ThreadGroup("myGroup");
        Thread thread = new Thread(myGroup, () -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "myThread");
        thread.start();

        TimeUnit.MILLISECONDS.sleep(2);
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        Thread[] list = new Thread[mainGroup.activeCount()];
        // enumerate 方法获取的线程仅仅是个预估值，不能百分之一百地保证当前 group 的活跃线程
        // enumerate 方法的返回值 int 相较于 Thread[] 的长度更为真实，代表真实的数量，并非 Thread[] 数组的长度
        int recurseSize = mainGroup.enumerate(list);
        // 3 main Monitor myThread
        System.out.println(recurseSize);

        // Thread[main,5,main]
        //Thread[Monitor Ctrl-Break,5,main]
        //Thread[myThread,5,myGroup]
        Arrays.stream(list).forEach(System.out::println);

        // 少一个，recurse：false，myGroup 中的线程不会包含在内
        recurseSize = mainGroup.enumerate(list, false);
        // 2 main Monitor
        System.out.println(recurseSize);
    }
}
