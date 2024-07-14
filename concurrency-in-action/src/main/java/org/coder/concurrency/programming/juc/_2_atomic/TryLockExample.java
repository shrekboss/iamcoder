package org.coder.concurrency.programming.juc._2_atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 在下面的代码中，我们启动的10个线程在一个while死循环中不断地进行锁的获取以及释放过程，运行上面的代码不难看出，
 * 在同一时刻只会有一个线程能够成功获得对该锁的持有。
 * <p>
 * 2.2.4 AtomicBoolean总结
 * 本节学习了AtomicBoolean的使用方法，通常情况下，我们可以使用AtomicBoolean来进行某个flag的开关控制。
 * 为了加深大家对AtomicBoolean的理解，我们借助于AtomicBoolean实现了一个TryLock，该显式锁只在提供线程获取锁失败立即返回的解决方案，
 * 本章中的显式锁Lock、ReentrantLock、StampedLock等都提供了TryLock的方法。
 */
public class TryLockExample {

    private final static Object VAL_OBJ = new Object();

    public static void main(String[] args) {
        //定义TryLock锁
        final TryLock lock = new TryLock();
        final List<Object> validation = new ArrayList<>();
        //启动10个线程，并且不断地进行锁的获取和释放动作
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        //尝试获取该锁，该方法不会导致当前线程进行阻塞
                        if (lock.tryLock()) {
                            System.out.println(Thread.currentThread() + ": get the lock.");
                            //进行校验，以确保validation中只存在一个元素
                            if (validation.size() > 1) {
                                throw new IllegalStateException("validation failed.");
                            }
                            validation.add(VAL_OBJ);
                            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                        } else {
                            //未获得锁，简单做一个休眠，以防止出现CPU过高电脑死机的情况发生
                            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //在finally语句块中进行锁的释放操作
                        if (lock.release()) {
                            System.out.println(Thread.currentThread() + ": release the lock.");
                            validation.remove(VAL_OBJ);
                        }
                    }
                }
            }).start();
        }
    }

}
