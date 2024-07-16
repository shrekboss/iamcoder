package org.coder.concurrency.programming.juc._4_colleciton;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 4.2 BlockingQueue(阻塞队列)
 * 本节即将学习和接触到的七种队列都可称为BlockingQueue，所谓Blocking Queue是指其中的元素数量存在界限，
 * 当队列已满时(队列元素数量达到了最大容量的临界值)，对队列进行写入操作的线程将被阻塞挂起，
 * 当队列为空时(队列元素数量达到了为0的临界值)，对队列进行读取的操作线程将被阻塞挂起，
 * 这是不是非常类似于本书的3.8.3节“使用Condition之生产者消费者”中的内容呢？
 * 实际上，BlockingQueue(LinkedTransferQueue除外)的内部实现主要依赖于显式锁Lock及其与之关联的Condition。
 * 因此本节中所涉及的所有BlockingQueue的实现都是线程安全的队列，在高并发的程序开发中，可以不用担心线程安全的问题而直接使用，
 * 另外，BlockingQueue在线程池服务中主要扮演着提供线程对任务取容器的角色，因此了解每一个BlockingQueue的使用场景和特点就是本节的主要目的之一。
 * <p>
 * 4.2.1 ArrayBlockingQueue
 * ArrayBlockingQueue是一个基于数组结构实现的FIFO阻塞队列，在构造该阻塞队列时需要指定队列中最大元素的数量(容量)。
 * 当队列已满时，若再次进行数据写入操作，则线程将会进入阻塞，一直等待直到其他线程对元素进行消费。
 * 当队列为空时，对该队列的消费线程将会进入阻塞，直到有其他线程写入数据。
 * 该阻塞队列中提供了不同形式的读写方法(注，本节在对方法的描述中，有多处语句的描述，类似于：“当队列已满时，当前线程会进入阻塞状态”，
 * “当队列为空时，当前线程将会进入阻塞状态”等，仅仅用于描述阻塞队列在对应临界值线程的挂起行为。
 * 由于阻塞队列常被应用于高并发多线程的环境中，因此当两个线程同时对队列头部数据进行获取操作时，势必会有一个线程进入短暂阻塞。
 * 为了节约篇幅，笔者没有提及该情况下的线程阻塞，但这并不代表该情况不会真实存在)。
 * <p>
 * 1.阻塞式写方法
 * 在ArrayBlockingQueue中提供了两个阻塞式写方法，分别如下(在该队列中，无论是阻塞式写方法还是非阻塞式写方法，都不允许写入null)。
 * void put(E e)
 * boolean offer(E e, long timeout, TimeUnit unit)
 * <p>
 * 2.非阻塞式写方法
 * 当队列已满时写入数据，如果不想使得当前线程进入阻塞，那么就可以使用非阻塞式的写操作方法。
 * boolean add(E e)
 * boolean offer(E e)
 * <p>
 * 3.阻塞式读方法
 * ArrayBlockingQueue中提供了两个阻塞式读方法，分别如下。
 * E take()
 * E poll(long timeout, TimeUnit unit)
 * <p>
 * 4.非阻塞式读方法
 * 当队列为空时读取数据，如果不想使得当前线程进入阻塞，那么就可以使用非阻塞式的读操作方法。
 * E poll()
 * E peek()
 * <p>
 * 5.生产者消费者
 * 高并发多线程的环境下对共享资源的访问，在绝大多数情况下都可以通过生产者消费者模式进行理论化概括化，无论是笔者的第一本书《Java高并发编程详解：多线程与架构设计》
 * 还是这本书里，很多地方都提及了该模式，因此在本书中，我们化繁为简给出ArrayBlockingQueue在高并发的环境中同时读写的代码片段即可，不在做过多解释。
 * <p>
 * 6.其他方法
 * 本节中介绍了大多数ArrayBlockingQueue的方法，除此之外，该阻塞队列还提供了一些其他方法，比如drainTo()排干队列中的数据到某个集合、remainingCapacity()获取剩余容量等，
 * ArrayBlockingQueue除了实现了BlockingQueue定义的所有接口方法之外它还是Collection接口的实现类，限于篇幅，关于更多的方法讲解请自行查阅JavaAPI帮助文档。
 */
public class ArrayBlockingQueueTest {
    /**
     * void put(E e):向队列的尾部插入新的数据，当队列已满时调用该方法的线程会进入阻塞，直到有其他线程对该线程执行了中断操作，或者队列中元素被其他线程消费。
     */
    @Test
    public void put() {
        //构造只有两个元素容量的ArrayBlockingQueue
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        try {
            queue.put("first");
            queue.put("second");
            //执行put将会使得当前线程进入阻塞
            queue.put("third");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }

    /**
     * boolean offer(E e, long timeout, TimeUnit unit):向队列尾部写入新的数据，当队列已满时执行该方法的线程在指定的时间单位内将进入阻塞，
     * 直到到了指定的超时时间后，或者在此期间有其他线程对队列数据进行了消费。当然了对由于执行该方法而进入阻塞的线程执行中断操作也可以使当前线程退出阻塞。
     * 该方法的返回值boolean为true时表示写入数据成功，为false时表示写入数据失败。
     */
    @Test
    public void offer() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        try {
            queue.offer("first", 10, TimeUnit.SECONDS);
            queue.offer("second", 10, TimeUnit.SECONDS);
            System.out.println(new Date() + "-start");
            //该方法会进入阻塞，10秒之后当前线程将会退出阻塞，并且对third数据的写入将会失败
            assert !queue.offer("third", 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new Date() + "-end");
    }

    /**
     * boolean add(E e):向队列尾部写入新的数据，当队列已满时不会进入阻塞，但是该方法会抛出队列已满的异常。
     */
    @Test
    public void add() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        //写入元素成功
        assert queue.add("first");
        assert queue.add("second");
        try {
            //写入失败，抛出异常
            queue.add("third");
        } catch (Exception e) {
            //断言异常
            assert e instanceof IllegalStateException;
        }
    }

    /**
     * boolean offer(E e):向队列尾部写入新的数据，当队列已满时不会进入阻塞，并且会立即返回false。
     */
    @Test
    public void offer2() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        assert queue.offer("first");
        assert queue.offer("second");
        //写入失败
        assert !queue.offer("third");
        //第三次offer操作失败，此时队列的size为2
        assert queue.size() == 2;
    }

    /**
     * E take():从队列头部获取数据，并且该数据会从队列头部移除，当队列为空式时执行take方法的线程将进入阻塞，直到有其他线程写入新的数据，或者当前线程被执行了中断操作。
     */
    @Test
    public void take() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        assert queue.offer("first");
        assert queue.offer("second");
        try {
            //由于是队列，因此这里的断言语句也遵从FIFO，第一个被take出来的数据是first
            assert queue.take().equals("first");
            assert queue.take().equals("second");
            //进入阻塞
            queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new Date() + "-end");
    }

    /**
     * E poll(long timeout, TimeUnit unit):从队列头部获取数据并且该数据会从队列头部移除，如果队列中没有任何元素时则执行该方法，当前线程会阻塞指定的时间，
     * 直到在此期间有新的数据写入，或者阻塞的当前线程被其他线程中断，当线程由于超时退出阻塞时，返回值为null。
     */
    @Test
    public void poll() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        assert queue.offer("first");
        assert queue.offer("second");
        try {
            //由于是队列，因此这里的断言语句也遵从FIFO，第一个被poll出来的数据是first
            assert queue.poll(10, TimeUnit.SECONDS).equals("first");
            assert queue.poll(10, TimeUnit.SECONDS).equals("second");
            System.out.println(new Date() + "-start");
            //10秒以后线程退出阻塞，并且返回null值。
            assert queue.poll(10, TimeUnit.SECONDS) == null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new Date() + "-end");
    }

    /**
     * E poll()：从队列头部获取数据并且该数据会从队列头部移除，当队列为空时，该方法不会使得当前线程进入阻塞，而是返回null值。
     */
    @Test
    public void poll2() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        assert queue.offer("first");
        assert queue.offer("second");
        //FIFO
        assert queue.poll().equals("first");
        assert queue.poll().equals("second");
        System.out.println(new Date() + "-start");
        //队列为空，立即返回但是结果为null
        assert queue.poll() == null;

        System.out.println(new Date() + "-end");
    }

    /**
     * E peek():peek的操作类似于debug操作(仅仅debug队列头部元素，本书的第6章将讲解针对Stream的操作，大家将从中学习到针对整个Stream数据元素的peek操作)，
     * 它直接从队列头部获取一个数据，但是并不能从队列头部移除数据，当队列为空时，该方法不会使得当前线程进入阻塞，而是返回null值。
     */
    @Test
    public void peek() {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        assert queue.offer("first");
        assert queue.offer("second");
        //第一次peek，从队列头部读取数据
        assert queue.peek().equals("first");
        //第一次peek，从队列头部读取数据，同第一次
        assert queue.peek().equals("first");
        //清除数据，队列为空
        queue.clear();
        //peek操作返回结果为null
        assert queue.peek() == null;
    }

    /**
     * 在下面的程序中，有22个针对queue的操作线程，我们并未提供对共享数据queue的线程安全保护措施，甚至没有进行任何临界值的判断与线程的挂起/唤醒动作，
     * 这一切都由该阻塞队列内部实现，因此开发者再也无需实现类似的队列，进行不同类型线程的数据交换和通信，运行上面的代码将会看到生产者与消费者在不断地交替输出。
     */
    @Test
    public void PAndC() {
        //定义阻塞队列
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        //启动11个生产数据的线程，向队列的尾部写入数据
        IntStream.rangeClosed(0, 10).boxed().map(i -> new Thread("P-Thread-" + i) {
            @Override
            public void run() {
                while (true) {
                    try {
                        String data = String.valueOf(System.currentTimeMillis());
                        queue.put(data);
                        System.out.println(Thread.currentThread() + " produce data: " + data);
                    } catch (InterruptedException e) {
                        System.out.println("Receied the interrupt signal.");
                    }
                }
            }
        }).forEach(Thread::start);

        //启动11个消费数据的线程，从队列的头部移除数据
        IntStream.rangeClosed(0, 10).boxed().map(i -> new Thread("C-Thread-" + i) {
            @Override
            public void run() {
                while (true) {
                    try {
                        String data = queue.take();
                        System.out.println(Thread.currentThread() + " produce data: " + data);
                    } catch (InterruptedException e) {
                        System.out.println("Receied the interrupt signal.");
                    }
                }
            }
        }).forEach(Thread::start);
    }
}