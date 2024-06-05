package org.coder.concurrency.programming.thread.api.priority;

/**
 * 优先级不能小于 1 也不能大于 10
 * main 线程默认优先级是 5
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadPriority1 {

    public static void main(String[] args) {
        Thread t1 = new Thread(
                () -> {
                    while (true) {
                        System.out.println("t1");
                    }
                }
        );
        t1.setPriority(3);

        Thread t2 = new Thread(
                () -> {
                    while (true) {
                        System.out.println("t2");
                    }
                }
        );
        t2.setPriority(10);

        // 交替出现
        t1.start();
        t2.start();
    }
}
