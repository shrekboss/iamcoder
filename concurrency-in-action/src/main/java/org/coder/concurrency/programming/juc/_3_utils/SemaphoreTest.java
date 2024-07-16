package org.coder.concurrency.programming.juc._3_utils;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 3.4.3 Semaphore其他方法详解
 * 相对于前面三个并发工具类(CountDownLatch、CyclicBarrier、Exchanger)，Semaphore提供的方法更多更丰富一些，本节将详细介绍Semaphore的每一个方法应该如何使用。
 * 1.Semaphore的构造
 * Semaphore包含了两个构造方法，具体如下所示。
 * 1).public Semaphore(int permits):定义Semaphore指定许可证数量，并且指定非公平的同步器，因此new Semaphore(n)实际上是等价于new Semaphore(n, false)的。
 * 2).public Semaphore(int permits, boolean fair):定义Semaphore指定许可证的数量的同时给定非公平或是公平同步器。
 * <p>
 * 2.tryAcquire方法
 * tryAcquire方法尝试向Semaphore获取许可证，如果此时许可证的数量少于申请的数量，则对应的线程会立即返回，结果为false表示申请失败，tryAcquire包含如下四种重载方法。
 * 1).tryAcquire():尝试获取Semaphore的许可证，该方法只会向Semaphore申请一个许可证，
 * 在Semaphore内部的可用许可证数量大于等于1的情况下，许可证会获取成功，反之获取许可证则失败，并且返回结果为false。
 * 2).boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException：该方法与tryAcquire无参方法类似，同样也是尝试获取一个许可证，但是增加了超时参数。
 * 如果在超时时间内还是没有可用的许可证，那么线程就会进入阻塞状态，直到到达超时时间或者在超时时间内有可用的证书(被其他线程释放的证书)，或者阻塞中的线程被其他线程执行了中断。
 * 3).boolean tryAcquire(int permits): 在使用无参的tryAcquire时只会向Semaphore尝试获取一个许可证，但是该方法会向Semaphore尝试获取指定数目的许可证。
 * 4).boolean tryAcquire(int permits, long timeout, TimeUnit):该方法与第二个方法类似，只不过其可以指定尝试获取许可证数量的参数，这里就不再赘述了，读者可以自行测试。
 * <p>
 * 3.acquire方法
 * acquire方法也是向Semaphore获取许可证，但是该方法比较偏执一些，获取不到就会一直等(陷入阻塞状态),Semaphore为我们提供了acquire方法的两种重载形式。
 * 1).void acquire():该方法会向Semaphore获取一个许可证，如果获取不到就会一直等待，直到Semaphore有可用的许可证为止，或者被其他线程中断。当然，如果有可用的许可证则会立即返回。
 * 2).void acquire(int permits)：该方法会向Semaphore获取指定数量的许可证，如果获取不到就会一直等待，直到Semaphore有可用的相应数量的许可证为止，或者被其他线程中断。
 * 同样，如果有可用的permits个许可证则会立即返回。
 * <p>
 * 4.acquireUninterruptibly
 * 如果说acquire的获取方式比较“倔犟”，但最起码还是可以“听得进别人的劝阻”中途放弃等待(中断该线程)，那么acquireUninterruptibly的获取方式就“固执得可怕”了，
 * 其不仅会在没有可用许可证的情况下执着地等待，而且对于“别人的劝阻”他还会直接无视，因此在使用这一类方法进行操作时请务必小心。因为该方法很容易出现大规模的线程阻塞
 * 进而导致Java进程出现假死的情况，Semaphore中提供了acquireUniterruptibly方法的两种重载形式。
 * 1).void acquireUninterruptible()：该方法会向Semaphore获取 一个许可证，如果获取不到就会一直等待，与此同时对该线程的任何中断操作都会被无视，
 * 直到Semaphore有可用的许可证为止。当然，如果有可用的许可证则会立即返回。
 * 2).void acquireUninterruptibly(int permits)：该方法会向Semaphore获取一个许可证，如果获取不到就会一直等待，与此同时对线程的任何中断操作都会被无视，
 * 直到Semaphore有可用的许可证为止。同样，如果有可用的permit个许可证则会立即返回。
 * <p>
 * 5.正确使用release
 * 在一个Semaphore中，许可证的数量可用于控制在同一时间允许多少个线程对共享资源进行访问，所以许可证的数量是非常珍贵的。
 * 因此当一个线程结束对Semaphore许可证的使用之后应该立即将其释放，允许其他线程有机会争抢许可证，下面是Semaphore提供的许可证释放方法。
 * 1).void release()：释放一个许可证，并且在Semaphore的内部，可用许可证的计数器会随之加一，表明当前有一个新的许可证可被使用。
 * 2).void release(int permits)：释放指定数量(permits)的许可证，并且在Semaphore内部，可用许可证的计数器会随之增加permits个，
 * 表明当前又有permits个许可证可被使用。
 * <p>
 * 6.其他方法
 * 本节进行到这里，关于Semaphore的主要方法基本上已经介绍完毕，其还包含一些其他的方法，我们在这里做个简单介绍即可。
 * 1).boolean isFair()：对Semaphore许可证的争抢采用公平还是非公平的方式，对应到内部的实现类为FairSync(公平)和NonfairSync(非公平)。
 * 2).int availablePermits()：当前的Semaphore还有多少个可用的许可证。
 * 3).int drainPermits()：排干Semaphore的所有的许可证，以后的线程将无法获取到许可证，已经获取到许可证的线程将不受影响。
 * 4).boolean hasQueueThreads()：当前是否有线程由于要获取Semaphore许可证而进入阻塞？（该值为预估值）
 * 5).int getQueueLength()：如果有线程由于获取Semaphore许可证而进入阻塞，那么它们的个数是多少呢？（该值为预估值）
 */
public class SemaphoreTest {

    @Test
    public void tryAcquire1() {
        //定义只有一个permit的Semaphore
        final Semaphore semaphore = new Semaphore(1, true);
        //第一次获取许可证成功
        assert semaphore.tryAcquire() : "acquire permit successfully.";
        //第二次获取失败
        assert !semaphore.tryAcquire() : "acquire permit failure.";
    }

    /**
     * 从上面的代码片段中，我们很清晰地可以看到匿名线程首先获取到了仅有的一个许可证之后休眠了10秒的时间，紧接着主线程想要尝试获取许可证，并且指定了3秒的超时时间，
     * 很显然主线程在被阻塞了3秒的时间之后退出阻塞，但还是不能够获取到许可证书，因为匿名线程并未释放，如果将主线程的超时时间改为30秒，那么主线程肯定能够在10秒以后获取到许可证。
     *
     * @throws InterruptedException
     */
    @Test
    public void tryAcquire2() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(1, true);
        //定义一个线程
        new Thread(() -> {
            //获取许可证
            boolean gotPermit = semaphore.tryAcquire();
            //如果获取成功就休眠10秒的时间
            if (gotPermit) {
                try {
                    System.out.println(Thread.currentThread() + " get one permit.");
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //10秒以后将释放Semaphore的许可证
                    semaphore.release();
                }
            }
        }).start();
        //短暂休眠1秒的时间，确保上面的线程能够启动，并且顺利获取许可证
        TimeUnit.SECONDS.sleep(1);
        //主线程在3秒之内肯定是无法获取许可证的，那么主线程将在阻塞3秒之后返回获取许可证失败
        assert !semaphore.tryAcquire(3, TimeUnit.SECONDS) : "can't get the permit";
    }

    @Test
    public void tryAcquire3() {
        //定义许可证数量为5的Semaphore
        final Semaphore semaphore = new Semaphore(5, true);
        //尝试获取5个许可证，成功
        assert semaphore.tryAcquire(5) : "acquire permit successfully.";
        //此时Semaphore中已经没有可用的许可证了，尝试获取将会失败
        assert !semaphore.tryAcquire() : "acquire permit failure.";
    }

    /**
     * 既然在该方法的使用中可以传入我们期望获取的许可证数量，那么传入的数量能否大于Semaphore中许可证的数量呢？
     * 这一点在代码的编写中当然是允许的，但是事实却是无法成功获取许可证，运行下面的代码块将会出现断言失败的错误。
     */
    @Test
    public void tryAcquire4() {
        //定义许可证数量为5的Semaphore
        final Semaphore semaphore = new Semaphore(5, true);
        //尝试获取10个许可证，这里的断言将会失败，并且抛出断言错误的异常
        assert semaphore.tryAcquire(10) : "acquire permit successfully.";
    }

    /**
     * 通过下面的程序代码片段，我们可以看到acquire方法在没有可用许可证(permit)时将会一直等待，直到出现可用的许可证(permit)为止，同时该方法允许被中断，
     * 但是上面代码处理方式存在着非常严重的问题，甚至是灾难性的，关于这个问题我们会在3.4.3节继续探讨。
     *
     * @throws InterruptedException
     */
    @Test
    public void acquire() throws InterruptedException {
        //定义permit=1的Semaphore
        final Semaphore semaphore = new Semaphore(1, true);
        //主线程直接抢先申请成功
        semaphore.acquire();
        Thread t = new Thread(() -> {
            try {
                //线程t会进入阻塞，等待当前有可用的permit
                semaphore.acquire();
                System.out.println("The thread t acquired permit from semaphore.");
            } catch (InterruptedException e) {
                System.out.println("The thread t is interrupted");
            }
        });
        t.start();
        TimeUnit.SECONDS.sleep(10);
        //主线程休眠10秒后释放permit，线程t才能获取到permit
        semaphore.release();
    }

    @Test
    public void acquireUninterruptibly() throws InterruptedException {
        //创建一个permit为1的Semaphore
        final Semaphore semaphore = new Semaphore(1, true);
        //主线程抢先得到仅有的一个许可证
        semaphore.acquire();
        //创建线程，并且使用acquireUninterruptibly方法获取permit
        Thread thread = new Thread(semaphore::acquireUninterruptibly);
        thread.start();
        TimeUnit.SECONDS.sleep(10);
        //执行线程thread的中断
        thread.interrupt();
    }

    /**
     * release方法非常简单，是吧？但是该方法往往是很多程序员容易出错的地方，而且一旦出现错误在系统运行起来之后，排查是比较困难的，
     * 为了确保能够释放已经获取到的许可证，我们第一反应是将其放到try..finally..语句块中，这样无论在任何情况下都能确保将已获得的许可证释放，
     * 但是恰恰是这样的操作会导致对Semaphore的使用不当，我们一起来看一下下面的例子。
     * <p>
     * 先不要急着运行上面的代码，我们根据所学的知识一起来分析一下上述程序的执行流程，首先可以百分之百地确认当前的JVM有三个非守护线程(t1、t2以及主线程(main线程))，
     * 根据上面代码片段的注释我们可以肯定，线程t1将会首先获取Semaphore的一个许可证，并且在一个小时之后将其释放，线程t2启动之后将会被阻塞(由于当前没有可用的许可证，
     * 因此执行acquire()方法的t2线程将会陷入阻塞等待可用的许可证)，很快，在主线程中线程t2被中断，那么此时在主线中执行acquire方法获取许可证是否会成功呢？
     * <p>
     * 理论上是不会成功的，或者最起码根据我们的期望，无论线程t2是被中断还是在阻塞中，主线程都不应该成功获取到许可证，
     * 但是由于我们对release方法的错误使用，导致了主线程成功获取了许可证，这个时候再来运行上述代码会看到如下的输出结果。
     * <p>
     * The thread t1 acquired permit from semaphore.
     * The thread t2 acquired permit from semaphore.
     * The main thread acquired permit.
     * <p>
     * 天呐！什么！主线程竟然获取到了一个许可证，可是我们的许可证书仅有一个，而且其已经被线程t1获取了，为什么主线程还会成功获取许可证呢？
     * 一切看起来似乎并不受我们的控制，试想一下如果一切发生在正在运行的系统中，由于上述程序不会出现错误，不会出现死锁，并且还会正常地运行，
     * 那么在海量的代码面前我们该如何排查呢？很明显在定位问题的时候将会困难重重，比较好的方式是在编写开发阶段就规避掉发生这种情况的可能性，
     * 正确地使用release方法。看到这里想必不用详细分析，大家应该也能看出什么问题了吧，对！就是finally语句块导致的问题，
     * 当线程t2被其他线程中断或者因自身原因出现异常的时候，它释放了原本不属于自己的许可证，导致在Semaphore内部的可用许可证计数器增多，
     * 其他线程才有机会获取到原本不该属于它的许可证。
     * <p>
     * 这难道是Semaphore的设计缺陷？其实并不是，打开Semaphore的官方文档，其中对release方法的描述如下：“There is no requirement
     * that a thread that release a permit must have acquired that permit by calling acquire().
     * Correct usage of a semaphore is established by programming convention in the application.”
     * 有此可以 看出，设计并未强制要求执行release操作的线程必须是执行了acquire的线程才可以，而是需要开发人员自身具有相应的编程约束来确保
     * Semaphore的正确使用，不管怎样，我们对上面的代码稍作修改，具体如下。
     * <p>
     * 程序修改之后再次运行，当线程t2被中断之后，它就无法再进行许可证的释放操作了，因此主线程也将不会再意外获取到许可证，
     * 这种方式是确保能够解决许可证被正确释放的思路之一，同样在3.4.4节中将会通过扩展Semaphore的方式增强release方法。
     *
     * @throws InterruptedException
     */
    @Test
    public void release() throws InterruptedException {
        //定义只有一个许可证的Semaphore
        final Semaphore semaphore = new Semaphore(1, true);
        //创建线程t1
        Thread t1 = new Thread(() -> {
            try {
                //获取Semaphore的许可证
                semaphore.acquire();
                System.out.println("The thread t1 acquired permit from semaphore.");
                //霸占许可证一个小时
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("The thread t1 is interrupted");
            } finally {
                //在finally语句块中释放许可证
                semaphore.release();
            }
        });
        //启动线程t1
        t1.start();
        //为了确保线程t1已经启动，在主线程中休眠1秒稍作等待
        TimeUnit.SECONDS.sleep(1);
        //创建线程t2
        Thread t2 = new Thread(() -> {
//			try {
//				//阻塞式地获取一个许可证
//				semaphore.acquire();
//			} catch (InterruptedException e) {
//				System.out.println("The thread t2 acquired permit from semaphore.");
//			} finally {
//				//同样在finally语句块中释放已经获取的许可证
//				semaphore.release();
//			}
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                System.out.println("The thread t2 is interrupted");
                return;
            }
            try {
                System.out.println("The thread t2 acquired permit from semaphore.");
            } finally {
                semaphore.release();
            }
        });
        //启动线程t2
        t2.start();
        //休眠2秒
        TimeUnit.SECONDS.sleep(2);
        //对线程t2执行中断操作
        t2.interrupt();
        //主线程获取许可证
        semaphore.acquire();
        System.out.println("The main thread acquired permit.");
    }

    @Test
    public void isFair() {
        Semaphore semaphore = new Semaphore(1, true);
        System.out.println(semaphore.isFair());
        Semaphore semaphore2 = new Semaphore(1, false);
        System.out.println(semaphore2.isFair());
    }

    @Test
    public void availablePermits() {
        Semaphore semaphore = new Semaphore(1, true);
        System.out.println(semaphore.availablePermits());
    }

    @Test
    public void drainPermits() {
        Semaphore semaphore = new Semaphore(1, true);
        System.out.println(semaphore.availablePermits());
        semaphore.drainPermits();
        System.out.println(semaphore.availablePermits());
    }

    @Test
    public void hasQueuedThreads() {
        Semaphore semaphore = new Semaphore(1, true);
        System.out.println(semaphore.hasQueuedThreads());
    }

    @Test
    public void getQueueLength() {
        Semaphore semaphore = new Semaphore(1, true);
        System.out.println(semaphore.getQueueLength());
    }
}