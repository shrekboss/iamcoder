package org.coder.concurrency.programming.juc._3_utils;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 3.重写onAdvance方法
 * 在构造CyclicBarrier的时候，如果给定一个Runnable作为回调，那么待所有的任务线程都到达barrier point之后，该Runnable接口的run方法将会被调用。
 * 同样，我们可以通过重写Phaser的onAdvance方法来实现类似的功能。在Phaser中，onAdvance方法是非常重要的，它在每一个Phase(阶段)中除了会在所有的分片都到达之后执行一次调用之外，
 * 更重要的是，他还会决定该Phaser是否被终止(当onAdvance方法的返回值为true时，则表明该Phaser将被终止，接下来将不能再使用)。
 * 我们先来看一个比较简单的例子，该例子会让Phaser也支持CyclicBarrier式的回调操作。
 * <p>
 * 下面程序的运行结果与3.5.1节的完全一样，只不过这里无须再将主线程注册到Phaser中，当然这只是Phaser onAdvance方法的使用场景之一。
 */
public class PhaserExample3 {

    public static void main(String[] args) {
        //使用我们自定义的Phaser，并且在构造时传入回调函数
        final Phaser phaser = new MyPhaser(() -> System.out.println(new Date() + ":" + Thread.currentThread() + "all of sub task completed work."));

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                phaser.register();
                try {
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(20));
                    phaser.arriveAndAwaitAdvance();
                    System.out.println(new Date() + ":" + Thread.currentThread() + " completed the work.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "T-" + i).start();
        }
    }

    //继承Phaser
    private static class MyPhaser extends Phaser {
        private final Runnable runnable;

        //在构造函数中传入Runnable接口作为回调函数使用
        private MyPhaser(Runnable runnable) {
            super();
            this.runnable = runnable;
        }

        //重写onAdvance方法，当parties个任务都到达某个phase时该方法将被调用执行
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            this.runnable.run();
            return super.onAdvance(phase, registeredParties);
        }
    }
}