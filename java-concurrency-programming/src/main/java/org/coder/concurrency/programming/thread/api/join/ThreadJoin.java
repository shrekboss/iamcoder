package org.coder.concurrency.programming.thread.api.join;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * join 方法会适当前线程永远地等待下去，直到期间被另外的线程中断，或者 join 的线程执行结束！！！
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadJoin {

    public static void main(String[] args) throws InterruptedException {

        List<Thread> threads = IntStream.range(1, 3).mapToObj(ThreadJoin::create).collect(toList());

        threads.forEach(Thread::start);

        // main -> t1
        // main -> t2
        // join 方法是被主线程调用的
        // t1 和 t2 交替地输出知道它们结束生命周期，main线程的循环才会开始运行
        for (Thread thread : threads) {
            // 注释掉 thread.join()，三个线程会交替地输出
            thread.join();
        }

        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName() + "#" + i);
            shortSleep();
        }
    }

    private static Thread create(int seq) {

        return new Thread(
            () -> {
                for (int i = 0; i < 10; i++) {
                    System.out.println(Thread.currentThread().getName() + "#" + i);
                    shortSleep();
                }
            }, String.valueOf(seq)
        );
    }

    private static void shortSleep() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
