package org.coder.concurrency.programming.thread;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadConstructors {

    private final static String PREFIX = "CODER-";

    public static void main(String[] args) throws InterruptedException {

        // 1. 默认 Thread 构造器
        IntStream.range(0, 5).boxed().map(i -> new Thread(
                () -> System.out.println(Thread.currentThread().getName()))
        ).forEach(Thread::start);

        // 2. 命名 Thread 构造器
        IntStream.range(5, 10).mapToObj(ThreadConstructors::createThread).forEach(Thread::start);

        // 3. Thread 的 setName 方法在线程启动后也能修改
        Thread t1 = new Thread(
                () -> {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        t1.setName("--线程启动前修改名称");
        System.out.println(t1.getState());
        System.out.println(t1.getName());

        t1.start();
        // 让线程充分运行，使其状态不是：NEW,线程不是 NEW 状态，对其的修改将不会生效
        TimeUnit.SECONDS.sleep(1);

        System.out.println(t1.getState());
        t1.setName("线程启动后修改名称--");
        System.out.println(t1.getName());

        // 4. 关于 ThreadGroup 问题
        t1 = new Thread("t1");
        ThreadGroup group = new ThreadGroup("TestGroup");
        Thread t2 = new Thread(group, "t2");
        ThreadGroup mainThreadGroup = Thread.currentThread().getThreadGroup();
        System.out.println("Main thread belong group: " + mainThreadGroup.getName());
        System.out.println("t1 and main belong the same group: " + (mainThreadGroup == t1.getThreadGroup()));
        System.out.println("t2 thread group not belong main group: " + (mainThreadGroup != t2.getThreadGroup()));
        System.out.println("t2 thread group belong TestGroup: " + (group == t2.getThreadGroup()));
    }

    private static Thread createThread(int intName) {
        return new Thread(
                () -> System.out.println(Thread.currentThread().getName())
                , PREFIX + intName);
    }
}
