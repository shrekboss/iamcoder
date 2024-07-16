package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3.8 Condition 详解
 * 如果说显式锁Lock可以用来替代synchronized关键字，那么Condition接口将会很好地替代传统的、通过对象监视器调用wait()、notify()、notifyAll()线程间的通信方式。
 * Condition对象是由某个显式锁Lock创建的，一个显式锁Lock可以创建多个Condition对象与之关联，Condition的作用在于控制锁并且判断某个条件(临界值)是否满足，如果不满足，
 * 那么使用该锁的线程将会被挂起等待另外的线程将其唤醒，于此同时被挂起的线程将会进入阻塞队列中并且释放对显式锁Lock的持有，这一点与对象监视器的wait()非常类似。
 * <p>
 * 3.8.1 初始Condition
 * Condition接口提供了比传统线程间通信方式更多的操作方法，Condition不能被直接创建，只能与某个显式锁Lock进行创建并且与之关联，
 * 下面我们快速地实现一个例子来体验一下Condition的使用方法。
 * 1.在本示例中我们将有两个线程分别进行数据的读与写。
 * 2.当数据发生改变时，读取数据的线程才会对其进行读取和做进一步的处理，当数据未发生改变时，读取数据的线程将会等待。
 * 3.当数据未被读取时，修改数据的线程将会进入阻塞等待，直到该数据被使用过才会进一步地产生新数据。
 * <p>
 * 运行上面的程序，会看到数据的更改与使用交替输出，不会出现数据未更改但多次使用的情况，以及数据未使用但多次更改的情况。
 * <p>
 * 程序的执行结果正如我们期望的那样，现在我们来分析一下ConditionExample1代码中比较关键的地方。
 * 1).shareData和dataUsed标识变量都是我们在该程序中的共享数据(资源)，同时dataUsed也是临界值，数据一致性的保护主要是针对这两个变量的。
 * 2).在注释①处，我们创建了显式锁Lock，该锁的作用主要是用于保护数据的一致性，然后使用该显式锁创建与之关联的Condition对象。
 * 3).在change()方法中，首先应获取对共享数据的访问权限(获取锁)，然后判断共享数据是否未被使用(注释②处)，如果还未被使用，那么当前线程将调用condition的await()方法进入阻塞队列，
 * 以阻塞等待被其他线程唤醒，调用condition的await()方法之后，当前线程会释放对显式锁Lock的持有，由于我们使用两个线程进行操作，因此这里的while循环完全可以使用if进行替代。
 * 4).当共享数据已经被使用，change()方法会进一步地修改共享数据，然后将状态标识设置为false，并且通知其他线程(主要是数据使用线程)对其进行使用(代码注释③处)。
 * 5).在use()方法中，同样是首先获取对共享数据的访问权限(获取锁)，然后判断共享数据是否已经被使用(注释④处)，如果数据已经被使用，那么当前线程会进入wait队列等待修改共享数据的线程将其唤醒。
 * 6).在注释⑤处，当正常使用了最新的共享数据时，当前线程则会通知数据更新线程可以继续对数据进行修改了。
 * <p>
 * 通过对Condition的简单使用以及运行过程的分析，我们对比对象monitor方式的线程间通信，可以发现两者在使用的过程中非常的相似，如表3-3所示。
 * 表3-3 Object Monitor vs Condition方法
 * ——————————————————————————————————————————————————————————————————————————————————
 * 操作			|	Object Monitor(对象监视器)				|	显式锁Lock
 * ——————————————————————————————————————————————————————————————————————————————————
 * 进入同步代码块	|	Synchronized关键字的#monitor enter 指令	|	Lock()方法
 * ——————————————————————————————————————————————————————————————————————————————————
 * 退出同步代码块	|	Synchronized关键字的#monitor exit 指令	|	Unlock()方法
 * ——————————————————————————————————————————————————————————————————————————————————
 * 进入wait		|	Monitor.wait()方法					|	Condition.await()方法
 * ——————————————————————————————————————————————————————————————————————————————————
 * 从wait中唤醒	|	Monitor.notify()方法					|	Condition.signal()方法
 * ——————————————————————————————————————————————————————————————————————————————————
 * 唤醒整个wait队列|	Monitor.notifyAll()方法				|	Condition.signalAll()方法
 * ——————————————————————————————————————————————————————————————————————————————————
 */
public class ConditionExample1 {
    //定义共享数据
    private static int shareData = 0;
    //定义布尔变量标识当前的共享数据是否已经被使用
    private static boolean dataUsed = false;
    //创建显式锁
    private static final Lock lock = new ReentrantLock();
    //①使用显式锁创建Condition对象并且与之关联
    private static final Condition condition = lock.newCondition();

    //对数据的写操作
    private static void change() {
        //获取锁，如果当前锁被其他线程持有，则当前线程会进入阻塞
        lock.lock();
        try {
            //②如果当前数据未被使用，则当前线程将进入wait队列，并且释放lock
            while (!dataUsed) {
                condition.await();
            }
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5));
            //修改数据，并且将dataUsed状态标识为false
            shareData++;
            dataUsed = false;
            System.out.println("produce the new value：" + shareData);
            //③通知并唤醒在wait队列中的其他线程————数据使用线程
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    //对数据进行使用
    private static void use() {
        //获取锁，如果当前锁被其他线程持有，则当前线程会进入阻塞
        lock.lock();
        try {
            //④如果当前数据已经使用，则当前线程将进入wait队列，并且释放lock
            while (dataUsed) {
                condition.await();
            }
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5));
            //使用数据，并且将dataUsed状态标识设置为true
            dataUsed = true;
            System.out.println("the shared data changed：" + shareData);
            //⑤通知并唤醒wait队列中的其他线程————数据修改线程
            condition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        // 创建并启动两个匿名线程
        new Thread(() -> {
            for (; ; ) {
                change();
            }
        }, "Producer").start();
        new Thread(() -> {
            for (; ; ) {
                use();
            }
        }, "Consumer").start();
    }

}