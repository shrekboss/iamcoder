package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 3.4.4 扩展Semaphore增强release
 * 在笔者的《Java高并发编程详解：多线程与架构设计》一书中，很多例子都给出过与之相关的解决方案，
 * 比如该书的5.4节“自定义显式锁BooleanLock”，以及在本书的第一部分我们都有相关知识细节的介绍，
 * 本节将通过扩展Semaphore来实现优雅的许可证资源释放操作。
 * <p>
 * MySemaphore类是扩展自Semaphore的一个子类，该类中有一个重要的队列，该队列为线程安全的队列，那么，为什么要使用线程安全的队列呢？因为对MySemaphore的操作是由多个线程进行的。
 * 该队列主要用于管理操作Semaphore的线程引用，成功获取到许可证的线程将加入该队列之中，同时只有在该队列中的线程才有资格进行许可证的释放动作。
 * 这样你就不用担心try..finally语句块的使用会引起没有获取到许可证的线程释放许可证的逻辑错误了。
 * 注意：通常情况下，我们扩展的Semaphore的确可以进行正确释放许可证的操作，但是仍然存在一些违规操作(无论是从语法还是API的调用上看都没有问题，但是仍然会导致出现错误)导致release错误的情况发生，比如下面的场景。
 * <p>
 * 某个线程获取了一个许可证，但是它在释放的过程中释放了多于一个数量的许可证，当然通常情况下我们不会编写这样漏洞百出的代码。
 * 由于篇幅的原因，这里就不再进行进一步的扩充了，希望读者可以自己去完成这样一个功能。
 * <p>
 * 3.4.5 Semaphore总结
 * Semaphore(信号量)是一个非常好的高并发工具类，它允许最多可以有多少个线程同时对共享数据进行访问，
 * 本节首先通过一个登录系统的例子介绍了Semaphore该如何使用，然后又发现在许可证数量为1的情况下我们
 * 可以将Semaphore当成锁来使用，并且借助Semaphore的方法创建了一个显式锁————try锁。
 * 同时本节还非常详细地讲解了Semaphore的每一个方法，当然release方法的合理使用也是至关重要的，
 * 如果使用不得当将会出现很严重的后果，本节也通过一个示例演示了release不正确的使用方式并且提出了不同的解决方案。
 * 最后需要说明的一点是，虽然Semaphore可以控制多个线程对共享资源进行访问，但是对于共享资源的临界区以及线程安全性，
 * Semphore并不会提供任何保证。比如，你有5个线程想要同时操作某个资源，那么该资源的操作线程安全性则需要额外的实现。
 * 另外，如果采用尝试的方式也就是不阻塞的方式获取许可证，务必要做到对结果的判断，否则就会出现尝试失败但程序依然去执行对共享资源的操作，
 * 这样做的后果也是非常严重的。
 */
//通过继承的方式扩展Semaphore
public class MySemaphore extends Semaphore {

    private static final long serialVersionUID = 8933591558015803485L;

    //定义线程安全的、存放Thread类型的队列
    private final ConcurrentLinkedQueue<Thread> queue = new ConcurrentLinkedQueue<>();

    public MySemaphore(int permits) {
        super(permits);
    }

    public MySemaphore(int permits, boolean fair) {
        super(permits, fair);
    }

    @Override
    public void acquire() throws InterruptedException {
        super.acquire();
        //线程成功获取许可证，将其放入队列中
        this.queue.add(Thread.currentThread());
    }

    @Override
    public void acquireUninterruptibly() {
        super.acquireUninterruptibly();
        //线程成功获取许可证，将其放入队列中
        this.queue.add(Thread.currentThread());
    }

    @Override
    public boolean tryAcquire() {
        final boolean acquired = super.tryAcquire();
        if (acquired) {
            //线程成功获取许可证，将其放入队列中
            this.queue.add(Thread.currentThread());
        }
        return acquired;
    }

    @Override
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        final boolean acquired = super.tryAcquire(timeout, unit);
        if (acquired) {
            //线程成功获取许可证，将其放入队列中
            this.queue.add(Thread.currentThread());
        }
        return acquired;
    }

    @Override
    public void release() {
        final Thread currentThread = Thread.currentThread();
        //当队列中不存在该线程时，调用release方法将会被忽略
        if (!this.queue.contains(currentThread)) {
            return;
        }
        super.release();
        //成功释放，并且将当前线程从队列中剔除
        this.queue.remove(currentThread);
    }

    @Override
    public void acquire(int permits) throws InterruptedException {
        super.acquire(permits);
        //线程成功获取许可证，将其放入队列中
        this.queue.add(Thread.currentThread());
    }

    @Override
    public void acquireUninterruptibly(int permits) {
        super.acquireUninterruptibly(permits);
        //线程成功获取许可证，将其放入队列中
        this.queue.add(Thread.currentThread());
    }

    @Override
    public boolean tryAcquire(int permits) {
        boolean acquired = super.tryAcquire(permits);
        if (acquired) {
            //线程成功获取许可证，将其放入队列中
            this.queue.add(Thread.currentThread());
        }
        return acquired;
    }

    @Override
    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
        boolean acquired = super.tryAcquire(permits, timeout, unit);
        if (acquired) {
            //线程成功获取许可证，将其放入队列中
            this.queue.add(Thread.currentThread());
        }
        return acquired;
    }

    @Override
    public void release(int permits) {
        final Thread currentThread = Thread.currentThread();
        //当队列中不存在该线程时，调用release方法将会被忽略
        if (!this.queue.contains(currentThread)) {
            return;
        }
        super.release(permits);
        //成功释放，并且将当前线程从队列中剔除
        this.queue.remove(currentThread);
    }
}