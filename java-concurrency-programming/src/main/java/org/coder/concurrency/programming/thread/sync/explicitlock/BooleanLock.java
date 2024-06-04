package org.coder.concurrency.programming.thread.sync.explicitlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;

/**
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class BooleanLock implements Lock {

    private Thread currentThread;
    // false 代表当前该锁没有被任何线程获得或者已经释放，true 代表该锁已经被某个县城获得，该线程就是 currentThread
    private boolean locked = false;
    // 用来存储哪些线程在获取当前线程时进入了阻塞状态
    private final List<Thread> blockedThreads = new ArrayList<>();

    @Override
    public void lock() throws InterruptedException {
        synchronized (this) {
            while (locked) {
                // 暂存当前线程
                Thread tempThread = currentThread();
                try {
                    if (!blockedThreads.contains(tempThread)) {
                        blockedThreads.add(tempThread);
                    }
                    this.wait();
                } catch (InterruptedException e) {
                    // 如果当前线程在 wait 时被中断，则从 blockedList 中将其删除，避免内存泄露
                    this.blockedThreads.remove(tempThread);
                    // 继续抛出异常
                    throw e;
                }
            }

            blockedThreads.remove(currentThread);
            this.locked = true;
            this.currentThread = currentThread();
        }
    }

    @Override
    public void lock(long mills) throws InterruptedException, TimeoutException {

        synchronized (this) {
            if (mills <= 0) {
                this.lock();
            } else {
                long remainingMills = mills;
                long endMills = currentTimeMillis() + remainingMills;
                while (locked) {
                    if (remainingMills <= 0) {
                        throw new TimeoutException("can not get the lock during " + mills + " ms.");
                    }
                    // 暂存当前线程
                     Thread tempThread = currentThread();
                    try {
                        if (!blockedThreads.contains(tempThread)) {
                            blockedThreads.add(tempThread);
                        }
                        this.wait(remainingMills);
                    } catch (InterruptedException e) {
                        // 如果当前线程在 wait 时被中断，则从 blockedList 中将其删除，避免内存泄露
                        this.blockedThreads.remove(tempThread);
                        // 继续抛出异常
                        throw e;
                    }

                    remainingMills = endMills - currentTimeMillis();
                }

                blockedThreads.remove(currentThread());
                this.locked = true;
                this.currentThread = currentThread();
            }
        }
    }

    @Override
    public void unlock() {
        synchronized (this) {
            if (currentThread == currentThread()) {
                this.locked = false;
                Optional.of(currentThread().getName() + " release the lock monitor.")
                        .ifPresent(System.out::println);
                this.notifyAll();
            }
        }
    }

    @Override
    public List<Thread> getBlockedThreads() {
        return Collections.unmodifiableList(blockedThreads);
    }
}
