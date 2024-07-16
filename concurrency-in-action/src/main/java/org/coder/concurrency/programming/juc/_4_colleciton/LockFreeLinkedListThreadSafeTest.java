package org.coder.concurrency.programming.juc._4_colleciton;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2.线程安全性测试
 * 线程安全性的测试需要特别注意一下几点：
 * 1).在多线程中进行链表操作时会不会出现死锁；
 * 2).在多线程中进行链表操作时会不会出现数据不一致的问题；
 * 3).对数据结构的使用要有删有增。
 * 设计这样的测试方案会稍有些难度，我们需要借助于其他的工具类来完成，请看下面的代码。
 * <p>
 * 上面的测试代码在注释中已经写得非常详细了，这里就不再赘述了，但是有几个数字是比较关键的，
 * 我们会用10个线程同时对无锁链表进行增删操作，链表中最大的元素个数为100万，同时这样的测试会被执行100个批次，下面就来执行测试程序。
 * <p>
 * 4.6.3 本节总结
 * 本节结合本书中的其他知识点开发了一个简单的Lock Free链表数据结构，通过测试我们发现它是线程安全的，并且能够保证实时数据一致性，
 * 相信通过本节内容的学习，读者可以窥探出Lock Free算法的一些端倪，并且可以加深以往所学的知识，如果你对LockFree算法比较感兴趣，
 * 那么建议参考下面几篇文章和论文。
 * 1.https://www.cs.cmu.edu/~410-s05/lectured/L31_LockFree.pdf
 * 2.http://concurrencykit.org/presentations/lockfree_introduction/#/
 * 3.http://www.rossbencina.com/code/lockfree
 * <p>
 * 4.7 本章总结
 * 在本章中，我们首先了解了链表数据结构的基本原理，并且开发了一个较为复杂的多层链表即跳表，然后学习了几种不同特性的阻塞队列，阻塞队列可以很好地应用于多线程高并发的场景之中。
 * 基于此，我们无须再使用synchronized关键字或者显式锁Lock对非线程安全的集合容器(如ArrayList或者LinkedList)进行同步化封装，并且提供线程间的唤醒通知。
 * <p>
 * 当然，并发容器在Java程序日常的开发中也经常会用到，在本章中，我们也花费一些篇幅对其进行了相关介绍，最后实现了一个最基本最简单的无锁(LockFree)链表数据结构。
 * 相信通过对这些知识的了解和掌握，开发者便可以对JDK提供的并发容器运用自如。
 * <p>
 * 以笔者日常的教学、工作还有在社区中讨论来看，大多数人连最起码的数据结构都不能掌握，一上来就研读并发包的底层代码，只会使你感到越来越困惑，
 * 喜欢专研是一件很好的事情，但是循序渐进地深入学习才是比较好的方法，这也是我们在本书中多次提到数据结构是一个程序员必须修炼的基本功的原因。
 */
public class LockFreeLinkedListThreadSafeTest {

    public static void main(String[] args) throws InterruptedException {
        //测试100个批次
        for (int iteration = 0; iteration < 100; iteration++) {
            //在每个批次中都有定义一个新的LockFreeLinkedList
            LockFreeLinkedList<Integer> list = new LockFreeLinkedList<>();
            //ConcurrentSkipListSet主要用于接下来的数据验证动作
            final ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();
            //用于生成LockFreeLinkedList将要存放的数据
            final AtomicInteger factory = new AtomicInteger();
            //在向LockFreeLinkedList写数据的同时，也会发生删除操作，因此该原子类型主要用于对已删除的数据元素做计数操作
            final AtomicInteger deleteCount = new AtomicInteger();
            //启动10个线程，同时对LockFreeLinkedList进行增删操作
            final CountDownLatch latch = new CountDownLatch(10);
            //数据量为100万
            final int MAX_CAPACITY = 1_000_000;

            //启动10个线程
            for (int i = 0; i < 10; i++) {
                new Thread(() -> {
                    while (true) {
                        int data = factory.getAndIncrement();
                        if (data < MAX_CAPACITY) {
                            list.add(data);
                            //模拟随机删除元素的操作
                            if (data % 2 == 0) {
                                list.removeFirst();
                                //当元素被删除时，deleteCount计数器增加
                                deleteCount.incrementAndGet();
                            }
                        } else {
                            break;
                        }
                    }
                    latch.countDown();
                }).start();
            }
            //等待所有线程运行结束
            latch.await();
            //第一次断言：list中的元素个数应该等于(100万 - 已被删除的数据个数)
            assert list.count() == (MAX_CAPACITY - deleteCount.get());
            //将所有数据存入set中，主要是看数据是否正确(Set中的数据不允许重复)
            while (!list.isEmpty()) {
                set.add(list.removeFirst());
            }
            //第二次断言：如果一切顺利，set中的元素个数也应该等于(100万 - 已被删除的数据个数)
            assert set.size() == (MAX_CAPACITY - deleteCount.get());
            //输出该批次顺序通过测试
            System.out.printf("The iteration %d passed concurrent testing %n", iteration + 1);
        }
    }
}