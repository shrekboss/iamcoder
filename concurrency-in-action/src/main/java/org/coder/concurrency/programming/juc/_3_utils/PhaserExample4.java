package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.Phaser;

/**
 * 就像前文中所描述的那样，该方法更重要的作用其实是决定Phaser的生死，下面来看一个简单的示例代码片段。
 * <p>
 * 下述代码很清晰地为大家演示了通过重写onAdvance方法可以控制Phaser是否被终止(生死)。在Phaser被终止之后，调用相关的方法不会出现异常，但是也并不会工作。
 * 比如我们在注释①处调用arriveAndAwaitAdvance()方法并不会等待其他分区任务到达，而是直接返回，这一点非常重要，如果想要借助于Phaser进行资源访问控制，
 * 则需要重点留意类似于这样的情况。
 */
public class PhaserExample4 {

    public static void main(String[] args) {
        //定义Phaser的同时指定了2个分片(parties)
        final Phaser phaser = new Phaser(2) {
            //重写onAdvance方法
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                System.out.println("phase:" + phase);
                //当Phase(阶段)编号超过1的时候，该Phaser将会被销毁
                return phase >= 1;
            }
        };
        //调用两次arrive方法，表示两个分片均已到达
        phaser.arrive();
        System.out.println(1);
        phaser.arrive();
        System.out.println(2);
        //此时phase为1
        assert phaser.getPhase() == 1 : "so far, the phaser number is 1.";
        //但是此时phaser并未销毁，原因是Phaser首次的phase编号为0，在执行了onAdvance方法之后，才会产生新的Phase(阶段)编号
        assert !phaser.isTerminated() : "phaser is not terminated.";
        //再次调用两次arrive方法，表示两个分片均已到达
        phaser.arrive();
        System.out.println(3);
        phaser.arrive();
        System.out.println(4);
        // 在所有的分片都arrive之后，onAdvance方法会被调用，此时返回值很明显为true，这就表明目前的Phaser已经不可用了，同样再
        // 次获取 phase 编号时会为负数
        assert phaser.getPhase() < 0 : "so far, the phase number is negative value.";
        assert phaser.isTerminated() : "phaser is terminated now.";
        //①下面的方法将不会再工作
        //invoke below method will not work.
        phaser.arriveAndAwaitAdvance();
        System.out.println("end");
    }

}