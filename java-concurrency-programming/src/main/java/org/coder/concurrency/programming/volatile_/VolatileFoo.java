package org.coder.concurrency.programming.volatile_;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class VolatileFoo {

    final static int MAX = 5;
    /**
     * Reader 线程压根就没有感知到 int_value的变化，而进入了死循环
     * <p>
     * The init_value will be changed to [1]]
     * The init_value will be changed to [2]]
     * The init_value will be changed to [3]]
     * The init_value will be changed to [4]]
     * The init_value will be changed to [5]]
     * </p>
     * 验证点1：volatile 变量和普通变量的区别
     */
//    static int int_value = 0;

    // volatile 只能修饰类变量和实例变量
    static volatile int int_value = 0;

    public static void main(String[] args) {
        new Thread(() -> {
            int localValue = int_value;
            while (localValue < MAX) {
                if (localValue != int_value) {
                    System.out.printf("The init_value is updated to [%d]\n", int_value);
                    localValue = int_value;
                }
            }
        }, "Reader").start();

        new Thread(() -> {
            int localValue = int_value;
            while (localValue < MAX) {
                System.out.printf("The init_value will be changed to [%d]\n", ++localValue);
                int_value = localValue;

                // 短暂休眠，目的是为了使 Reader 线程能够来得及输出变化内容
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Updater").start();
    }
}
