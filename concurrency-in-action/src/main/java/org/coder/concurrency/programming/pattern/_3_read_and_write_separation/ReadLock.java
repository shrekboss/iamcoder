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
class ReadLock implements Lock {

    private final ReadWriteLockImpl readWriteLock;

    public ReadLock(ReadWriteLockImpl readWriteLock) {
        this.readWriteLock = readWriteLock;
    }

    @Override
    public void lock() throws InterruptedException {
        // 使用 Mutex 作为锁
        synchronized (readWriteLock.getMutex()) {
            // 若此时有线程在进行写操作，或者写线程在等待并且偏向写锁的标识为 true 时，就会无法获取读锁，只能被挂起
            while (readWriteLock.getWritingWriters() > 0
                    || (readWriteLock.getPreferWriter()
                    && readWriteLock.getWaitingWriters() > 0)) {
                readWriteLock.getMutex().wait();
            }
            // 成功获得读锁，并且是 readingReaders 的数量增加
            readWriteLock.incrementReadingReaders();
        }
    }

    @Override
    public void unlock() {
        // 使用 Mutex 作为锁
        synchronized (readWriteLock.getMutex()) {
            // 释放锁的过程就是使得当前 reading 的数量减一
            readWriteLock.decrementReadingReaders();
            // ***** 将 preferWriter 设置为 true，可以使得 writer 线程获得更多的机会 *****
            readWriteLock.changePrefer(true);
            // 通知唤醒与 Mutex 关联的 Monitor wait set 中的线程
            readWriteLock.getMutex().notifyAll();
        }
    }
}
