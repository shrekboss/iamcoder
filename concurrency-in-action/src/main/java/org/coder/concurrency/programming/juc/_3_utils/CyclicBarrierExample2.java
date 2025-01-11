package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 3.2.2 CyclicBarrier的循环特性
 * CyclicBarrier的另一个很好的特性是可以被循环使用，也就是说当其内部的计数器为0之后还可以在接下来的使用中重置而无须重新定义一个新的。
 * 下面我们看一个简单的例子，想必每个人都是非常喜欢旅游的，旅游的时候不可避免地需要加入某些旅行团。在每一个旅行团中都至少会有一个导游为我们进行向导和解说，
 * 由于游客比较多，为了安全考虑导游经常会清点人数以防止个别旅客由于自由活动出现迷路、掉队的情况。
 * <p>
 * Cyclic Barrier的循环使用图示如图3-3所示。
 * <p>
 * 通过图3-3，我们可以看到，只有在所有的旅客都上了大巴之后司机才能将车开到下一个旅游景点，当大巴到达旅游景点之后，导游还会进行人数清点已确认车上没有旅客由于睡觉而逗留，
 * 车才能开去停车场，进而旅客在该景点游玩。有此我们可以看出，所有乘客全部上车和所有乘客在下一个景点全部下车才能开始进一步地统一行动，下面写一个程序简单模拟一下。
 * <p>
 * 在上面的程序中，我们根据前文描述对游客上车后的统一发车，以及到达目的地下车后的统一行动进行控制。自始至终我们都是使用同一个CyclicBarrier来进行控制的，
 * 在这里需要注意的是，在主线程中的两次await中间为何没有对barrier进行reset的操作，那是因为在CyclicBarrier内部维护了一个count。
 * 当所有的await调用导致其值为0的时候，reset相关的操作会被默认执行。下面来看一下CyclicBarrier的await方法调用的相关源码，代码如下。
 * public int await() throw InterruptedException, BrokenBarrierException {
 * ...
 * //所有的await调用，事实上执行的是dowait方法
 * return dowait(false, 0L);
 * ...
 * }
 * private int dowait(boolean timed, long nanos) throws InterruptedException, BrokenBarrierException, TimeoutException {
 * ...省略
 * int index = --count;
 * if(index == 0) { //tripped
 * boolean ranAction = false;
 * try {
 * final Runnable command = barrierCommand;
 * if (command != null)
 * command.run();
 * ranAction = true;
 * nextGeneration();
 * return 0;
 * } finally {
 * if (!ranAction)
 * breakBarrier();
 * }
 * }
 * ...省略
 * }
 * private void nextGeneration() {
 * //唤醒阻塞中的所有线程
 * trip.signalAll();
 * //set up next generation
 * //修改count的值使其等于构造CyclicBarrier转入的parties值
 * count = parties;
 * //创建新的Generation
 * generation = new Generation();
 * }
 * 通过上面的代码片段，我们可以很清晰地看出，当count的值为0的时候，最后会重新生成新的Generation，并且将count的值设定为构造CyclicBarrier转入的parties值。
 * 那么在调用了reset方法之后呢？我们同样也可以看一下CyclicBarrier reset的源码片段。
 * public void reset() {
 * final ReentrantLock lock = this.lock;
 * lock.lock();
 * try {
 * //调用break barrier方法
 * breakBarrier();	//break the current generation
 * //重新生成新的generation
 * nextGeneration();	//start a new generation
 * } finally {
 * lock.unlock();
 * }
 * }
 * private void breakBarrier() {
 * //generation的broken设置为true，标识该barrier已经被broken了
 * generation.broken = true;
 * //重置count的值
 * count = parties;
 * //唤醒阻塞的其他线程
 * trip.signalAll();
 * }
 * <p>
 * 由于所有的子任务线程都已经顺利完成，虽然在reset方法中调用了breakBarrier方法和唤醒其他新阻塞线程，但是它们都会被忽略掉，根本不会影响到dowait方法中的线程(因为执行该方法的线程已经没有了)，
 * 紧接着generation又会被重新创建，因此在本节的例子中，主线程的两次await方法调用之间完全可以不用reset方法，当然你加入了reset方法也不会有什么影响。
 * <p>
 * 在程序运行的输出结果中，笔者用黑体下划线标注出了需要我们关注的两句非常重要的输出。
 * 通过输出可以发现，在不同的阶段控制中，一个CyclicBarrier就可以很好地实现我们的要求。
 */
public class CyclicBarrierExample2 {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        //定义CyclicBarrier，注意这里的parties值为11
        final CyclicBarrier barrier = new CyclicBarrier(11);
        //创建10个线程
        for (int i = 0; i < 10; i++) {
            //定义游客线程，传入游客编号和barrier
            new Thread(new Tourist(i, barrier)).start();
        }
        //主线程也进入阻塞，等待所有游客都上了旅游大巴
        barrier.await();
        System.out.println("Tour Guider:all of Tourist get on the bus.");
        //主线程也进入阻塞，等待所有游客都下了旅游大巴
        barrier.await();
        System.out.println("Tour Guider:all of Tourist get off the bus.");
    }

    private static class Tourist implements Runnable {

        private final int touristID;
        private final CyclicBarrier barrier;

        public Tourist(int touristID, CyclicBarrier barrier) {
            this.touristID = touristID;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            System.out.printf("Tourist:%d by bus\n", touristID);
            //模拟乘客上车的时间开销
            this.spendSeveralSeconds();
            //上车后等待其他同伴上车
            this.waitAndPrint("Tourist:%d Get on the bus, and wait other people reached.\n");
            System.out.printf("Tourist:%d arrival the destination\n", touristID);
            //模拟乘客下车的时间开销
            this.spendSeveralSeconds();
            //下车后稍作等待，等待其他同伴全部下车
            this.waitAndPrint("Tourist:%d Get off the bus, and wait other people get off.\n");
        }

        private void waitAndPrint(String message) {
            System.out.printf(message, touristID);
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

        //random sleep
        private void spendSeveralSeconds() {
            try {
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}