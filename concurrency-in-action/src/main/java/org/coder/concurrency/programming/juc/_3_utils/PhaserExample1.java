package org.coder.concurrency.programming.juc._3_utils;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 3.5 Phaser 工具详解
 * 本章前面所学的CountDownLatch、CyclicBarrier、Exchanger、Semaphore这几个同步工具都是JDK在1.5版本中引入的，而本节将要学习到的Phaser是在JDK1.7版本中才加入的。
 * Phaser同样也是一个多线程的同步助手工具，它是一个可被重复使用的同步屏障，它的功能非常类似于本章已经学习过的CyclicBarrier和CountDownLatch的合集，
 * 但是它提供了更加灵活丰富的用法和方法，同时它的使用难度也要略微大于前两者。
 * <p>
 * 3.5.1 Phaser的基本用法
 * CountDownLatch可以很好地控制等待多个线程执行完子任务，但是它有一个缺点，那就是内部的计数器无法重置，也就是说CountDownLatch属于一次性的，使用结束后就不能再次使用。
 * CyclicBarrier倒是可以重复使用，但是一旦parties在创建的时候被指定，就无法再改变。Phaser则取百(两)家之所长于一身引入了两者的特性。
 * 本节将通过使用Phaser来实现CountDownLatch和CyclicBarrier的主要功能，从而帮助读者熟悉Phaser的基本用法。
 * <p>
 * 1.将Phaser当作CountDownLatch来使用
 * CountDownLatch所能完成的任务，在Phaser中照样可以很好地完成，我们看下面的代码。
 * <p>
 * 从执行结果上来看，主线程等待所有的子线程运行结束之后，才会接着执行下一步的任务，这看起来是不是非常类似于CountDownLatch呢？
 * 很明显是的，就目前这样的情况来看，使用Phaser可以完全替代CountDownLatch了。
 * 我们再来分析PhaserExample1中的代码执行过程(关键的地方都已经表明了注释)。
 * 1).在注释①处定义了一个Phaser，该Phaser内部也维护了一个类似于CyclicBarrier的parties，但是我们在定义的时候并未指定分片parties，
 * 因此默认情况下就是0，但是这个值是可以在随后的使用过程中更改的，这就是Phaser的灵活之处了。
 * 2).紧接着创建了10个线程，并且在线程的执行单元中第一行代码(注释②处)就调用了Phaser的register方法，该方法的作用其实是让Phaser内部的分片parties加一，
 * 也就是说待10个线程分别执行了register方法之后，此时的分片parties就成了10。
 * 3).如果我们采用当前线程随机休眠的方式来模拟线程真正的执行，那么每一个线程的运行时间开销肯定是不一样的。
 * 带每一个线程执行完相应的业务逻辑之后(在我们的代码中是休眠)会调用phaser的arrive()方法(注释③处)，该方法的作用与CountDownLatch的countdown()方法的语义一样，
 * 代表着当前线程已经到达了这个屏障，因此该方法不是阻塞方法，执行之后会立即返回，同时该方法会返回一个整数类型的数字，代表当前已经到达的Phase(阶段)编号，
 * 这个数字默认是从0开始的，后文中会专门针对Phase(阶段)编号进行讲解。
 * 4).在注释④处，主线程也执行了register方法，此刻Phaser的parties就为11了，紧接着主线程执行了phaser的arriveAndAwaitAdvance方法(注释⑤处)，
 * 该方法的作用除了表示当前线程已经到达了这个屏障之外，它还会等待其他线程也到达这个屏障，然后继续前行。因此该方法是一个阻塞方法，这就非常类似于CountDownLatch的await方法了，
 * 即等待所有子线程完成任务。
 * <p>
 * 注意：在主线程进行register操作之前，请务必保证所有的子线程都能够顺利register，否则就会出现phaser只注册了一个parties，并且很快arrive的情况，
 * 这会导致后面的断言语句出现失败的情况，因此我们在主线程进行register操作之前，需要通过休眠的方式确保所有的子线程顺利register(当然这并不是一种非常严谨的方式，
 * 给出的休眠时间也是来自我们日常的经验值，更加合理的方式是在定义Phaser的时候指定parties的值，关于这一点，后文中会为大家详细介绍)。
 */
public class PhaserExample1 {

    public static void main(String[] args) throws InterruptedException {
        //①定义一个Phaser，并未指定分片数量parties，此时在Phaser内部分片的数量parties默认为0，后面可以通过register方法动态增加
        final Phaser phaser = new Phaser();
        //定义10个线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                //②首先调用phaser.register()方法使得phaser内部的parties加一
                phaser.register();
                try {
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(20));
                    //③线程任务结束，执行arrive方法
                    phaser.arrive();
                    System.out.println(new Date() + ":" + Thread.currentThread() + "completed the work.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "T-" + i).start();
        }
        TimeUnit.SECONDS.sleep(10);
        //④主线程也调用注册方法，此时parties的数量为11=10+1
        phaser.register();
        //⑤主线程也arrive，但是它要等待下一阶段，等待下一个阶段的前提是所有的线程都arrive，也就是phaser内部当前phase的unarrived数量为0
        phaser.arriveAndAwaitAdvance();
        //通过下面的assertion就可以断言我们上面的判断
        assert phaser.getRegisteredParties() == 11 : "total 11 parties is registered.";
        System.out.println(new Date() + ": all of sub task completed work.");
    }

}