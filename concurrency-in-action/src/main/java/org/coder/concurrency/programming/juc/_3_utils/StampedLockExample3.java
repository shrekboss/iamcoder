package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.locks.StampedLock;

/**
 * 3.乐观读模式
 * StampedLock还提供了一个模式，即乐观读模式，使用tryOptimisticRead()方法获取一个非排他锁并且不会进入阻塞状态，
 * 与此同时该模式依然会返回一个long型的数据戳用于接下来的验证(该验证主要用来判断共享资源是否有写操作发生)，
 * 我们还是通过一个示例为大家解释乐观读的使用方式和设计技巧。
 * 1).乐观读模式的使用方法也是非常简单的，首先调用tryOptimisticRead()(注释①处，该方法为立即返回方法，并不会导致当前线程进入阻塞等待)方法进行乐观读操作，
 * 同样该方法也会返回一个long型的数据戳(stamp)，如果获取成功，则数据戳为非0，如果失败，则数据戳为0。
 * 2).get方法首先进行了一次乐观读锁的获取并且立即返回一个数据戳(stamp)，但是仅就这样的操作是不足以立即将共享数据返回的，这会导致数据出现不一致的情况，具体说明如下。
 * (1).假设调用乐观读返回的数据戳(stamp)为零，则代表其他线程正在对共享资源进行写操作，也就是说其他线程获取了对该共享资源的写权限。
 * (2).假设调用乐观读返回的数据戳(stamp)为非零，紧接着又有其他线程立即获取了对共享资源的写操作。
 * <p>
 * 基于以上两点，我们还需要对数据戳stamp进行校验之后才能决定对共享资源进行阻塞式的读还是将其立即返回，具体的代码在注释②处，使用StampedLock的validate方法可以判断上述两种情况是否发生。
 * 3).如果上述两种情况已经发生，则进行读锁的获取操作，此时若有其他线程对共享资源进行写操作，则当前线程会进入阻塞等待直到获取到读锁。
 * 4).如果在注释①处获取的读锁通过验证，则直接返回共享数据注释④处，不进行任何同步操作，这样的话就可以对共享数据进行无锁读操作了，即提高了共享资源并发读取的能力。
 */
public class StampedLockExample3 {

    private static int shareData = 0;
    private static final StampedLock lock = new StampedLock();

    public static void inc() {
        long stamp = lock.writeLock();
        try {
            shareData++;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public static int get() {
        //注释①
        long stamp = lock.tryOptimisticRead();
        //注释②
        if (!lock.validate(stamp)) {
            //注释③
            stamp = lock.readLock();
            try {
                return shareData;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        //注释④
        return shareData;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}