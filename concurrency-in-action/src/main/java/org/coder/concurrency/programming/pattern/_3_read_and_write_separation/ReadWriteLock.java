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
public interface ReadWriteLock {

    Lock readLock();

    Lock writeLock();

    /**
     * 获取当前有多少线程正在执行写操作
     */
    int getWritingWriters();

    /**
     * 获取当前有多少线程正在等待获取写入锁
     */
    int getWaitingWriters();

    /**
     * 获取当前有多少线程正在执行读操作
     */
    int getReadingReaders();

    /**
     * 工厂方法，创建 ReadWriteLock
     */
    static ReadWriteLock readWriteLock() {
        return new ReadWriteLockImpl();
    }

    /**
     * 工厂方法，创建 ReadWriteLock，并且传入 preferWriter
     */
    static ReadWriteLock readWriteLock(boolean preferWriter) {
        return new ReadWriteLockImpl(preferWriter);
    }
}
