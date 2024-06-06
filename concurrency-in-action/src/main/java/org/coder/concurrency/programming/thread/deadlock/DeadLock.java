package org.coder.concurrency.programming.thread.deadlock;

/**
 * 死锁：交叉锁导致程序出现死锁
 * <p>
 * 没事别运行，后果自负！！！
 * 没事别运行，后果自负！！！
 * 没事别运行，后果自负！！！
 * 没事别运行，后果自负！！！
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class DeadLock {

    private final Object MUTEX_READ = new Object();
    private final Object MUTEX_WRITE = new Object();

    public static void main(String[] args) {
        final DeadLock deadLock = new DeadLock();

        Thread t1 = new Thread(() -> {
            while (true) {
                deadLock.read();
            }
        }, "READ-THREAD");
//        t1.start();

        Thread t2 = new Thread(() -> {
            while (true) {
                deadLock.write();
            }
        }, "WRITE-THREAD");
//        t2.start();
    }

    public void read() {
        synchronized (MUTEX_READ) {
            System.out.println(Thread.currentThread().getName() + " get READ lock");
            synchronized (MUTEX_WRITE) {
                System.out.println(Thread.currentThread().getName() + " get WRITE lock");
            }
            System.out.println(Thread.currentThread().getName() + " release WRITE lock");
        }
        System.out.println(Thread.currentThread().getName() + " release READ lock");
    }

    public void write() {
        synchronized (MUTEX_WRITE) {
            System.out.println(Thread.currentThread().getName() + " get WRITE lock");
            synchronized (MUTEX_READ) {
                System.out.println(Thread.currentThread().getName() + " get READ lock");
            }
            System.out.println(Thread.currentThread().getName() + " release READ lock");
        }
        System.out.println(Thread.currentThread().getName() + " release WRITE lock");
    }
}
