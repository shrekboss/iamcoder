package org.coder.concurrency.programming.thread.api.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * 如果一个线程在没有执行可中断方法之前就被中断，那么其接下来将执行可中断方法，比如 sleep 会发生什么样的情况?
 * ===> 如果一个线程设置了 interrupt 标识，那么接下来的可中断方法会立即中断，因此 注释5的信号捕获部分代码会被执行
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadInterruptedAndIsInterruptedDiff {

    public static void main(String[] args) {

        // 注释 1. 判断当前线程是否被中断
        System.out.println("Main thread is interrupted? " + Thread.interrupted());

        // 注释 2. 中断当前线程
        Thread.currentThread().interrupt();

        // 注释 3. 判断当前线程是否已经被中断.
        // 如果 Thread.currentThread().isInterrupted() 换成 Thread.interrupted()，那么方法不会捕获异常，也就是注释5部分代码不输出
        System.out.println("Main thread is Interrupted? " + Thread.currentThread().isInterrupted());

        try {
            // 注释 4. 当前线程执行可中断方法
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            // 注释 5. 捕获中断信号
            // 如果一个线程设置了 interrupt 标识，那么接下来的可中断方法会立即中断，因此 注释5的信号捕获部分代码会被执行
            System.out.println("I will be interrupted still");
        }
    }
}
