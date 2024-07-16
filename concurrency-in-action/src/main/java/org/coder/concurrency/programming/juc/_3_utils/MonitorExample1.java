package org.coder.concurrency.programming.juc._3_utils;

import com.google.common.util.concurrent.Monitor;

/**
 * 3.10 Guava之Monitor详解
 * 除了Java自身版本的升级会为开发者提供一些比较好用的并发工具以外，某些第三方类库也提供了一些很好用的并发工具。
 * 比如Google的Guava，在本节和3.11节中将会为大家介绍Guava所提供的两个比较好用的并发工具Monitor和RateLimiter。
 * <p>
 * Google Guava 起源于2007年，孵化自Google之手，最初它主要提供一些数据结构、数据容器的Java实现，
 * 但是随着开发者对它喜爱程度的加深，它逐渐发展出了对文件I/O、高并发、函数式编程、Cache、EventBus等的支持。
 * 很多开发者对Guava是推崇备至的，笔者就是其中的一员，与其说Guava是一个工具集类库，还不如说Guava是对Java本身的一次优雅扩充。
 * 通过使用Guava甚至研习源码，你会发现Guava的开发者对Java语言是如此地了解和精通，由于本书并不是一本关于Google Guava的专著，
 * 因此并不会过多介绍Guava的使用（笔者在2018年年初推出过一套长达40多集的Google Guava课程，感兴趣的读者可以在互联网上自行搜索）。
 * <p>
 * 3.10.1 Monitor及Guard
 * 无论使用对象监视器的wait notify/notifyAll还是Condition的await signal/signalAll方法调用，
 * 我们首先都会对共享数据的临界值进行判断，当条件满足或者不满足的时候才会调用相关方法使得当前线程挂起，或者唤醒wait队列/set中的线程，
 * 因此对共享数据临界值的判断非常关键，Guava的Monitor工具提供了一种将临界值判断抽取成Guard的处理方式，
 * 可以很方便地定义若干个Guard也就是临界值的判断，以及对临界值判断的重复使用，除此之外Monitor还具备synchronized关键字和显式锁Lock的完整语义，
 * 下面来看一下示例代码。
 * <p>
 * 1).在下面的代码中，我们首先定义了一个Monitor对象，接着又将临界值的判断抽取成了Guard，我们只需要将临界值的判断逻辑写在isSatisfied()方法中即可，
 * 当共享数据的值大于10的时候无法对其再次进行自增操作。
 * 2).在main方法中(main线程中)，我们采用无限循环的方式对共享数据x进行自增操作，详见代码注释①处。
 * 3).在注释②处，对x进行操作之前先调用monitor.enterWhen()方法，该方法除了具备锁的功能之外还具备临界值判断的操作，
 * 因此只有当x满足临界值判断时当前线程才会对x进行自增运算，否则当前线程将会进入阻塞队列(其实在Guard内部使用的也是Condition)。
 * 4).对x的运算成功之后，调用leave()方法，注释③处，该方法除了释放当前的锁之外，还会通知唤醒与Guard关联的Condition阻塞队列中的某个阻塞线程。
 * <p>
 * 运行上面的代码，我们会发现临界值条件不满足时，当前线程(main线程)将会进入阻塞状态。
 * <p>
 * 当某个线程进入Monitor代码块时，实际上它首先要抢占与Monitor关联的Lock，当该线程调用了leave方法，
 * 实际上是需要释放与Monitor关联的Lock，因此在某个时刻仅有一个线程能够进入到Monitor代码块中(排他的)。
 * <p>
 * 3.10.2 Monitor的其他方法
 * 除了在3.10.1节中介绍过的enterWhen()方法之外，Monitor还提供了非常多的使用方法。
 * 1.enter()：该方法完全等价于Lock的lock()方法。
 * 2.enterIf(Guard guard)：该方法主要用于判断当前的Guard是否满足临界值的判断，也是使用比较多的一个操作，调用该方法，当前线程并不会进入阻塞之中。
 * 3.tryEnter()：等价于Lock的tryLock()方法。
 * 4.waitFor(Guard guard)：当前线程将会阻塞等待，直到Guard条件满足当前线程才会退出阻塞状态。
 * <p>
 * 3.10.3 Monitor总结
 * 当对共享数据进行操作之前，首先需要获得对该共享数据的操作权限(也就是获取锁的动作)，然后需要判断临界值是否满足，
 * 如果不满足，则为了确保数据的一致性需要将当前线程挂起(对象监视器的waitset或者Condition的阻塞队列)，
 * 这样的动作，前文中已经练习过很多次了，Monitor以及Monitor Guard则很好地将类似的一系列动作进行了抽象，
 * 隐藏了锁的获取、临界值判断、线程挂起、阻塞线程唤醒、锁的释放等操作。
 */
public class MonitorExample1 {
    //定义Monitor对象
    private static Monitor monitor = new Monitor();
    //共享数据，一个简单的int类型数据
    private static int x = 0;
    //定义临界值，共享数据的值不能超过MAX_VALUE
    private static final int MAX_VALUE = 10;
    //定义Guard并且实现isSatisfied方法
    private static final Monitor.Guard INC_WHEN_LESS_10 = new Monitor.Guard(monitor) {
        //该方法就相当于我们在写对象监视器或者Condition时的临界值判断逻辑
        @Override
        public boolean isSatisfied() {
            return x < MAX_VALUE;
        }
    };

    //注释①
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            //注释②
            monitor.enterWhen(INC_WHEN_LESS_10);
            try {
                x++;
                System.out.println(Thread.currentThread() + ": x value is: " + x);
            } finally {
                //注释③
                monitor.leave();
            }
        }

    }

}