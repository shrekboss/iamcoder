package org.coder.concurrency.programming.juc._3_utils;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 3.2.3 CyclicBarrier的其他方法以及总结
 * 通过前面两个章节的学习，读者应该已经掌握了CyclicBarrier的基本用法，当然它还提供了一些其他的方法和构造方式，本节将统一进行整理和讲解。
 * 1.CyclicBarrier(int parties)构造器：构造CyclicBarrier并且传入parties。
 * 2.CyclicBarrier(int parties, Runnable barrieAction)构造器：构造CyclicBarrier不仅传入parties，而且指定一个Runnable接口，
 * 当所有的线程达到barrier point的时候，该Runnable接口会被调用，有时我们需要在所有任务执行结束之后执行某个动作，这时就可以使用这种CyclicBarrier的构造方式了。
 * 3.int getParties()方法：获取CyclicBarrier在构造时的parties，该值一经CyclicBarrier创建将不会被改变。
 * 4.await()方法：我们使用最多的一个方法，调用该方法之后，当前线程将会进入阻塞状态，等待其他线程执行await()方法进入barrier point，进而全部退出阻塞状态，
 * 当CyclicBarrier内部的count为0时，调用await()方法将会直接返回而不会进入阻塞状态。
 * 5.await(long timeout, TimeUnit unit)方法：该方法与无参的await方法类似，只不过增加了超时的功能，当其他线程在设定的时间内没有到达barrier point时，当前线程也会退出阻塞状态。
 * 6.isBroken()：返回barrier的broken状态，某个线程由于执行await方法而进入阻塞状态，如果该线程被执行了中断操作，那么isBroken()方法将会返回true。
 * 7.getNumberWaiting()方法：该方法返回当前barrier有多少个线程执行了await方法而不是还有多少个线程未到达barrier point，这一点需要注意。
 * 8.reset()方法：前面已经详细地介绍过这个方法，其主要作用是中断当前barrier，并且重新生成一个generation，还有将barrier内部的计数器count设置为parties值，
 * 但是需要注意的是，如果还有未到达barrier point的线程，则所有的线程将会被中断并且退出阻塞，此时isBroken()方法将返回false而不是true。
 * <p>
 * 3.2.4 CyclicBarrier VS. CountDownLatch
 * 截至目前，我们已经详细学习了CyclicBarrier 和 CountDownLatch的作用和用法，两者都可用于管理和控制子任务线程的执行，
 * 在某些场景下，它们都可以实现类似的功能，但是它们本质上存在着很多差别，包括但不限制下列差别。
 * 1.CountDownLatch的await方法会等待计数器被count down到0，而执行CyclicBarrier的await方法的线程将会等待其他线程到达barrier point。
 * 2.CyclicBarrier内部的计数器count是可被重置的，进而使得CyclicBarrier也可被重复使用，而CountDownLatch则不能。
 * 3.CyclicBarrier是由于Lock和Condition实现的，而CountDownLatch则是由同步控制器AQS(AbstractQueueSynchronizer)来实现的。
 * 4.在构造CyclicBarrier时不允许parties为0，而CountDownLatch则允许count为0。
 */
public class CyclicBarrierTest {

    @Test
    public void await() throws InterruptedException, BrokenBarrierException {
        final CyclicBarrier barrier = new CyclicBarrier(1);
        barrier.await();//barrier的count为0
        barrier.await();//直接返回
        barrier.await();//直接返回
    }

    /**
     * 当一个线程在执行CyclicBarrier的await方法进入阻塞而被中断时，CyclicBarrier会被broken这一点我们已经通过上面的代码证明过了，但是需要注意如下几点(非常重要)。
     * 1).当一个线程由于在执行CyclicBarrier的await方法而进入阻塞状态时，这个时候对该线程执行中断操作会导致CyclicBarrier被broken。
     * 2).被broken的CyclicBarrier此时已经不能再直接使用了，如果想要使用就必须使用reset方法对其重置。
     * 3).如果有其他线程此时也由于执行了await方法而进入阻塞状态，那么该线程会被唤醒并且抛出BrokenBarrierException异常。
     *
     * @throws InterruptedException
     */
    @Test
    public void isBroken() throws InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        Thread thread = new Thread(() -> {
            try {
                //thread将会进入阻塞状态
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("被中断");
                e.printStackTrace();
            }
        });

        thread.start();
        //两秒后在main线程中执行thread的中断操作
        TimeUnit.SECONDS.sleep(2);
        //输出barrier的broken状态，这种情况下该返回值肯定为false
        System.out.println(barrier.isBroken());
        //调用中断
        thread.interrupt();
        //短暂休眠，确保thread的执行动作发生在main线程读取broken状态之前
        TimeUnit.SECONDS.sleep(2);
        //输出barrier的broken状态，这种情况下该返回值肯定为true
        System.out.println(barrier.isBroken());

        Thread thread2 = new Thread(() -> {
            try {
                //thread将会进入阻塞状态
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("被中断2");
                e.printStackTrace();
            }
        });

        thread2.start();
        //两秒后在main线程中执行thread的中断操作
        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void reset() throws InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(3);
        Thread thread = new Thread(() -> {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("被中断");
                e.printStackTrace();
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(2);
        //执行reset方法，thread线程将会被中断
        barrier.reset();
        System.out.println(barrier.isBroken());
        //此时isBroken()为false而不是true
        assert !barrier.isBroken() : "broken state must false.";
    }
}