package org.coder.concurrency.programming.juc._3_utils;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 2.将Phaser当作CyclicBarrier来使用
 * 上文中，通过Phaser实现了类似于CountDownLatch的功能，既然说Phaser吸取了CyclicBarrier和CountDownLatch的特点，
 * 那么我们也可以借助于Phaser来完成CyclicBarrier的主要功能，即所有的子线程共同到达一个barrier point。示例代码如下：
 * <p>
 * 下面的程序代码与3.5.1节中的代码基本类似，只不过在子线程中，我们将不再使用arrive方法表示当前线程已经完成任务，取而代之的是arriveAndAwaitAdvance方法，
 * 该方法会等待在当前Phaser中所有的part(子线程)都完成了任务才能使线程退出阻塞，当然也包括主线程自身，因为主线程也进行了register操作。
 * 运行上面的程序我们会发现，几乎所有的输出语句都是在同一时间输出的，这也就完全符合CyclicBarrier等待所有的子线程都到达barrier point这一特性了。
 */
public class PhaserExample2 {

    public static void main(String[] args) throws InterruptedException {
        //定义一个分片parties为0的Phaser
        final Phaser phaser = new Phaser();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                //子线程调用注册方法，当10个子线程都执行了register，parties将为10
                phaser.register();
                try {
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(20));
                    //调用arriveAndAwaitAdvance方法等待所有线程arrive，然后继续前行
                    phaser.arriveAndAwaitAdvance();
                    System.out.println(new Date() + ":" + Thread.currentThread() + "completed the work.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "T-" + i).start();
        }
        //休眠以确保其他子线程顺利调用register方法
        TimeUnit.SECONDS.sleep(10);
        //主线程调用register方法，此时phaser内部的parties为11
        phaser.register();
        phaser.arriveAndAwaitAdvance();
        assert phaser.getRegisteredParties() == 11 : "total 11 parties is registered.";
        System.out.println(new Date() + ": all of sub task completed work.");
    }

}