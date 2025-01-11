package org.coder.concurrency.programming.pattern._3_read_and_write_separation;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
class WriteLock implements Lock {

    private final ReadWriteLockImpl readWriteLock;

    public WriteLock(ReadWriteLockImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {
        // 使用 Mutex 作为锁
        synchronized (readWriteLock.getMutex()) {
            try {
                // 首先使等待获取写入锁的数字加一
                readWriteLock.incrementWaitingWriters();
                // 如果此时有其他线程正在进行读操作，或者写操作，那么当前线程将被挂起
                while (readWriteLock.getReadingReaders() > 0
                        || readWriteLock.getWritingWriters() > 0) {
                    readWriteLock.getMutex().wait();
                }
            } finally {
                // 成功获得写锁，使得等待获取写入锁的计数减一
                readWriteLock.decrementWaitingWriters();
            }
            // 将正在写入的线程数量加一
            readWriteLock.incrementWritingWriters();
        }
    }

    @Override
    public void unlock() {
        // 使用 Mutex 作为锁
        synchronized (readWriteLock.getMutex()) {
            // 释放锁的过程就是使得当前 writing 的数量减一
            readWriteLock.decrementWritingWriters();
            // ***** 将 preferWriter 设置为 false，可以使得 reader 线程获得更多的机会 *****
            readWriteLock.changePrefer(false);
            // 通知唤醒与 Mutex 关联的 Monitor wait set 中的线程
            readWriteLock.getMutex().notifyAll();
        }
    }
}
