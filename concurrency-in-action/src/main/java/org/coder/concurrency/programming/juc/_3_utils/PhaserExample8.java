package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.Phaser;

/**
 * 3.5.3 Phaser层级关系
 * 在《Java高并发编程详解：多线程与架构设计》一书中，曾详细介绍了Thread。Thread中存在着一定的层级关系，也就是说某一个Thread类会有一个父Thread，
 * 同样在定义Phaser的时候也可以为其指定父Phaser，当我们在创建某个Phaser的时候若指定了父Phaser，那么它将 具有如下这些特性。
 * 1.子Phaser当前的Phase阶段编号会以父Phaser的编号为准。
 * 2.父Phaser的所有分片数量=父Phaser分片数量的自身注册数量+所有子Phaser的分片注册数量之和。
 * 3.调用当前Phaser的arriveAndAwaitAdvance方法时，首先会调用父Phaser的对应方法。
 * 4.直接调用子Phaser的arrive方法时，在某些情况下会出现bad arrive的错误。
 * <p>
 * 来看一段示例代码。
 * <p>
 * 通常情况下，我们不会借助有层级关系的Phaser去实现多线程任务的同步管理，因为这样可能会导致多线程的控制复杂化，因此在本节中只是简单地举例说明一下它的用法，
 * 如果读者对Phaser的层级关系的使用场景感兴趣，则可以自行翻阅相关文档进行学习。
 * <p>
 * 3.5.4 Phaser总结
 * 本节为大家详细介绍了Phaser的使用方法，在大多数时候，我们可以完全借助于Phaser替代CyclicBarrier和CountDownLatch的应用场景，
 * 相较于这两者，Phaser具有可动态改变的分片parties以及可被多次使用的特性等。
 * <p>
 * 另外，在本章中，笔者直接写出来了某些单词而并未对其进行翻译，比如Phaser在很多中文文章中被称为阶段器，有些则称之为相位器等，
 * 另外针对Phase，很多中文资料称之为阶段，有些则称之为栅栏，因此本章决定不翻译了，笔者也不建议大家将其翻译出来。
 */
public class PhaserExample8 {

    public static void main(String[] args) {
        // 定义只有一个分片的Phaser
        Phaser root = new Phaser(1);
        //对root Phaser进行断言
        assertState(root, 0, 1, 1);
        //root phaser调用arrive方法，使得root phaser进入下一个Phase(阶段)
        assert root.arrive() == 0;

        //定义两个子Phaser，分片个数分别为1
        Phaser child1 = new Phaser(root, 1);
        Phaser child2 = new Phaser(root, 1);

        //root Phaser的注册分片达到了3个
        assertState(root, 1, 3, 3);
        //子Phaser当前的Phase编号与父Phaser的Phase(阶段)编号一致
        assertState(child1, 1, 1, 1);
        assertState(child2, 1, 1, 1);
    }

    /**
     * 断言方法
     */
    private static void assertState(Phaser phaser, int phase, int parties, int unarrived) {
        assert phaser.getPhase() == phase;
        assert phaser.getRegisteredParties() == parties;
        assert phaser.getUnarrivedParties() == unarrived;
    }

}