package org.coder.concurrency.programming.thread.classloading;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 同一个时间，只能有一个线程执行到静态代码块中的内容，并且静态代码块仅仅只会被执行一次，
 * JVM 保证了 <clinit>() 方法在多线程的执行环境下的同步原语
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ClassInit1 {

    static {
        try {
            System.out.println("The ClassInit static code block will be invoke.");
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        IntStream.range(0, 5)
                .forEach(i -> new Thread(ClassInit1::new));
    }
}
