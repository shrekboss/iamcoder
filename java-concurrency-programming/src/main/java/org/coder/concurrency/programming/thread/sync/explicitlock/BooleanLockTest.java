package org.coder.concurrency.programming.thread.sync.explicitlock;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class BooleanLockTest {

    private final Lock lock = new BooleanLock();

    public void syncMethod() {
        try {
            lock.lock();
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println(Thread.currentThread() + " get the lock.");
            System.out.println("syncMethod 执行时间：" + randomInt + "s");
            TimeUnit.SECONDS.sleep(randomInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void syncMethodTimeout() {
        try {
            lock.lock(1000);
            System.out.println(Thread.currentThread() + " get the lock.");
            int randomInt = ThreadLocalRandom.current().nextInt(10);
            System.out.println("syncMethodTimeout 执行时间：" + randomInt + "s");
            TimeUnit.SECONDS.sleep(randomInt);
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    // 验证控制阻塞时长
//    public static void main(String[] args) {
//        BooleanLockTest blt = new BooleanLockTest();
//        IntStream.range(0, 10)
//                .mapToObj(i -> new Thread(blt::syncMethod))
//                .forEach(Thread::start);
//    }

    // 验证可被中断
//    public static void main(String[] args) throws InterruptedException {
//        BooleanLockTest blt = new BooleanLockTest();
//        new Thread(blt::syncMethod, "T1").start();
//        TimeUnit.MILLISECONDS.sleep(2);
//        Thread t2 = new Thread(blt::syncMethod, "T2");
//        t2.start();
//        TimeUnit.MILLISECONDS.sleep(10);
//        t2.interrupt();
//    }

    // 验证阻塞超时，不是每次都出现
    // java.util.concurrent.TimeoutException: can not get the lock during 1000 ms.
    public static void main(String[] args) throws InterruptedException {
        BooleanLockTest blt = new BooleanLockTest();
        new Thread(blt::syncMethod, "T1").start();
        TimeUnit.MILLISECONDS.sleep(2);
        Thread t2 = new Thread(blt::syncMethodTimeout, "T2");
        t2.start();
        TimeUnit.MILLISECONDS.sleep(10);
    }
}
