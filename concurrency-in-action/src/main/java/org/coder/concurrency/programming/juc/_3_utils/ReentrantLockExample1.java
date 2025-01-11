package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3.6 Lock&ReentrantLock详解
 * 在Java1.5版本以前，我们开发多线程程序只能通过关键字synchronized进行共享资源的同步、临界值的控制，虽然随着版本的不断升级，
 * JDK对synchronized关键字的性能优化工作一直都没有停止过，但是synchronized在使用的过程中还是存在着比较多的缺陷和不足，
 * 因此在1.5版本以后JDK增加了对显式锁的支持，显式锁Lock除了能够完成关键字synchronized的语义和功能之外，它还提供了很多灵活方便的方法，
 * 比如，我们可以通过显式锁对象提供的方法查看有哪些线程被阻塞，可以创建Condition对象进行线程间的通信，可以中断由于获取锁而被阻塞的线程，
 * 设置获取锁的超时时间等一系列synchronized关键不具备的能力。
 * <p>
 * 本节将学习Lock接口及其接口方法，掌握使用ReentrantLock的使用方法，以及如何通过ReentrantLock提供的API观察线程的阻塞情况，
 * 最后还会通过JMH基准测试工具为大家分析对比synchronized关键字和ReentrantLock的性能。
 * <p>
 * 3.6.1 Lock及ReentrantLock方法详解
 * 1.Lock接口方法
 * Lock接口是对锁操作方法的一个基本定义，它提供了synchronized关键字所具备的全部功能方法，另外我们可以借助于Lock创建不同的Condition对象进行多线程间的通信操作，
 * 与关键字synchronized进行方法同步代码块同步的方式不同，Lock提供了编程式的锁获取(lock())以及释放操作(unlock())等其他操作。
 * 1).lock方法：尝试获取锁，如果此刻该锁未被其他线程持有，则会立即返回，并且设置锁的hold计数为1；
 * 如果当前线程已经持有该锁则会再次尝试申请，hold计数将会增加一个，并且立即返回；
 * 如果该锁当前被另外一个线程持有，那么当前线程会进入阻塞，直到获取该锁，由于调用lock方法而进入阻塞状态的线程同样不会被中断，
 * 这一点与进入synchronized同步方法或者代码块被阻塞类似。
 * 2).lockInterruptibly()方法：该方法的作用与前者类似，但是使用该方法试图获取锁而进入阻塞操作的线程则是可被中断的，也就是说线程可以获得中断信号。
 * 3).tryLock()方法：调用该方法获取锁，无论成功与否都会立即返回，线程不会进入阻塞状态，若成功获取锁则返回true，若获取锁失败则返回false。
 * 使用该方法时请务必注意进行结果的判断，否则会出现获取锁失败却仍旧操作共享资源而导致数据不一致等问题的出现。
 * 4).tryLock(long time, TimeUnit unit)方法：该方法与tryLock()方法类似，只不过多了单位时间设置，如果在单位时间内为获取到锁，则返回结果为false，
 * 如果在单位时间内获取到了锁，则返回结果为true，同样hold计数也会被设置为1。
 * 5).unlock()方法：当前某个线程对锁的使用结束之后，应该确保对锁资源的释放，以便其他线程能够继续争抢，unlock()方法的作用正在于此。
 * 6).newCondition()方法：创建一个与该lock相关联的Condition对象，在本章的3.8节中，我们会重点讲解Condition的使用。
 * <p>
 * 2.ReentrantLock扩展方法
 * 在显式锁Lock接口的诸多实现中，我们用的最多的就是ReentrantLock，该类不仅完全实现了显式锁Lock接口所定义的接口，也扩展了对使用显式锁Lock的一些监控方法。
 * 1).getHoldCount()方法：查询当前线程在某个Lock上的数量，如果当前线程成功获取了Lock，那么该值大于等于1；如果没有获取到Lock的线程调用该方法，则返回值为0。
 * 2).isHeldByCurrentThread()方法：判断当前线程是否持有某个Lock，由于Lock的排他性，因此在某个时刻只有一个线程调用该方法返回true。
 * 3).isLocked()方法：判断Lock是否已经被线程持有。
 * 4).isFair()方法：创建的ReentrantLock是否为公平锁。
 * 5).hasQueuedThreads()方法：在多个线程试图获取Lock的时候，只有一个线程能够正常获得，其他线程可能（如果使用tryLock()方法失败则不会进入阻塞）会进入阻塞，该方法的作用就是查询是否有线程正在等待获取锁。
 * 6).hasQueuedThread()方法：在等待获取锁的线程中是否包含某个指定的线程。
 * 6).getQueueLength()方法：返回当前有多少个线程正在等待获取锁。
 * <p>
 * 3.6.2 正确使用显式锁Lock
 * 显式锁Lock的底层实现相对来说比较复杂，但是站在使用者的角度来看却是比较简单的（本节不会出现非常多的代码片段用于演示如何使用显式锁Lock）。
 * 锁的存在，无论是Lock接口还是synchronized关键字，主要是帮助我们解决多线程资源的竞争问题，也就是说在同一时刻只能有一个线程对共享资源进行访问，
 * 即排他性，另外就是确保若干代码指令执行的原子性。
 * (1)确保已获取锁的释放
 * 使用synchronized关键字进行共享资源的同步时，JVM提供了两个指令monitor enter和monitor exit来分别确保锁的获取和释放操作，这与显式锁Lock的lock和unlock方法的作用是一致的。
 * <p>
 * 使用try...finally语句块可以确保获取到的lock将被正确释放，示例代码如下。
 * private final Lock lock = new ReentrantLock();
 * public void foo() {
 * lock.lock();
 * try{
 * //程序执行逻辑
 * }finally{
 * //finally语句块可以确保lock被正确释放
 * lock.unlock();
 * }
 * }
 * <p>
 * 上述代码中，我们将unlock()方法写在try...finally语句块中的目的是为了防止获取锁的过程中出现异常导致锁被意外释放，
 * 3.4.3节的“正确使用release”中进行过测试，发现未获取到许可证permit的线程也可以调用semaphore的release方法，
 * 使得当前的可用许可证permit数量增多，但是在lock中不存在这样的情况。源码片段：
 * protected final boolean tryRelease(int releases) {
 * int c = getState() - releases;
 * if (Thread.currentThread() != getExclusiveOwnerThread()) {
 * throw new IllegalMonitorStateException();
 * }
 * boolean free = false;
 * if (c == 0) {
 * free = true;
 * setExclusiveOwnerThread(null);
 * }
 * setState(c);
 * return free;
 * }
 * <p>
 * 通过上面的代码片段，我们可以看到lock不允许未获得锁的线程调用unlock()方法，lock和synchronized关键字一样都具备可重入性，
 * lock内部维护了hold计数器，而synchronized的内部则维护了monitor计数器，它们的作用都是一样的，若成功获取锁的初始值为1，
 * 那么持有该锁时再次获取锁除了会立即成功之外，对应的计数器也会随之自增，在使用synchronized关键字的时候，JVM会为我们担保这一切，
 * 但是显式锁的使用则需要程序员自行控制，下面看一段代码片段。
 * <p>
 * 从下面的代码中，很明显可以看到lock被重入（多次获取），每一次的重入都会在hold计数器原有的数量基础之上加一，显式锁lock需要程序员手动控制对锁的释放操作。
 * lock被第二次获取之后只进行了一次unlock操作，这就导致当前线程对该锁的hold数量仍为非0，因此并未完成对该锁的释放行为，进而导致其他线程无法获取该锁处于阻塞状态，
 * 若程序出现这样的情况则是非常危险的，因为匿名线程在生命周期结束之后，线程本身的对象引用还被AQS的exclusiveOwnerThread所持有，
 * 但是线程本身已经死亡，这样一来就没有任何线程能够对当前锁进行释放操作了，更谈不上获取了，下面通过JVM工具查看一下。
 */
public class ReentrantLockExample1 {

    public static void main(String[] args) throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread() + " acquired the lock.");
                //首次获取lock,hold的计数器为1
                assert lock.getHoldCount() == 1;
                //重入
                lock.lock();
                System.out.println(Thread.currentThread() + " acquired the lock again.");
                //lock重入，hold的计数器随之增加1个
                assert lock.getHoldCount() == 2;
            } finally {
                //释放lock，但是对应的hold计数器只能减一
                lock.unlock();
                System.out.println(Thread.currentThread() + " released the lock.");
                //因此当前线程还持有该锁
                assert lock.getHoldCount() == 1;
            }
        }).start();
        //休眠2秒，确保匿名线程能够启动并获取锁
        TimeUnit.SECONDS.sleep(2);
        //阻塞，永远不会获取锁
        lock.lock();
        System.out.println("main thread acquired the lock");
        lock.unlock();
        System.out.println("main thread released the lock");
    }

}