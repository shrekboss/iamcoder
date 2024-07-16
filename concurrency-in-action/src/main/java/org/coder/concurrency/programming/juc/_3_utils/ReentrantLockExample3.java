package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * (3)多个原子性方法的组合不能确保原子性
 * 无论是synchronized关键字还是lock锁，其主要作用之一都是保证若干代码指令的原子操作，要么都成功要么都失败，
 * 也就是说在代码指令的运行过程中不允许被中断，但是多个原子性方法的组合就无法担保原子性了，无论是使用同一个lock对象还是不同的lock对象。
 * <p>
 * 在下面的代码中我们定义了Accumulator类，每一个方法都是线程安全的方法，因此也可以说每一个方法的执行都是原子性的，
 * 但是在AccumulatorThread中使用了多个原子性方法的组合，其结果未必就是原子性的了，执行程序会出现很多x和y不相等的情况，
 * 甚至出现x和y相等还被输出的情况，读者可以参考《Java高并发编程详解：多线程与架构设计》第16章的分析。
 */
public class ReentrantLockExample3 {

    public static void main(String[] args) {
        // 启动10个线程
        final Accumulator accumulator = new Accumulator();
        for (int i = 0; i < 10; i++) {
            new AccumulatorThread(accumulator).start();
        }
    }

    private static class AccumulatorThread extends Thread {
        private final Accumulator accumulator;

        private AccumulatorThread(Accumulator accumulator) {
            this.accumulator = accumulator;
        }

        @Override
        public void run() {
            //不断地调用addX和addY,根据我们的期望，x和y应该一样，但是事实并非如此
            while (true) {
                accumulator.addX();
                accumulator.addY();
                //检查不相等的情况
                if (accumulator.getX() != accumulator.getY()) {
                    System.out.printf("The x: %d not equals y:%d\n", accumulator.getX(), accumulator.getY());
                }
            }
        }
    }

    /**
     * 在Accumulator中，所有的方法都是线程安全的，每个方法的执行都是原子性的，不可被中断
     */
    private static class Accumulator {
        private static final Lock lock = new ReentrantLock();
        private int x = 0;
        private int y = 0;

        void addX() {
            lock.lock();
            try {
                x++;
            } finally {
                lock.unlock();
            }
        }

        void addY() {
            lock.lock();
            try {
                y++;
            } finally {
                lock.unlock();
            }
        }

        int getX() {
            lock.lock();
            try {
                return x;
            } finally {
                lock.unlock();
            }
        }

        int getY() {
            lock.lock();
            try {
                return y;
            } finally {
                lock.unlock();
            }
        }
    }
}