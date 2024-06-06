package org.coder.concurrency.programming.thread.api.priority;

/**
 * 线程关于组继承的问题
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadPriority3 {

    public static void main(String[] args) {

        Thread t1 = new Thread();
        System.out.println("t1 priority " + t1.getPriority());

        Thread t2 = new Thread(
                () -> {
                    Thread t3 = new Thread();
                    System.out.println("t3 priority " + t3.getPriority());
                }
        );
        t2.setPriority(6);
        t2.start();
        System.out.println("t2 priority " + t2.getPriority());
    }
}
