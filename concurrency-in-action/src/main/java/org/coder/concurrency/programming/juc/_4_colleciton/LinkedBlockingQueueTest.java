package org.coder.concurrency.programming.juc._4_colleciton;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 4.2.3 LinkedBlockingQueue
 * ArrayBlockingQueue是基于数组实现的FIFO“有边界”队列，PriorityBlockingQueue也是基于数组实现的，但它是“无边界”的优先级队列，由于存在对数据元素的排序规则，
 * 因此PriorityBlockingQueue并不能提供FIFO的约束担保(当然，如果想要使其具备FIFO的特性，需要约束PriorityBlockingQueue的排序规则为R，并且对其写入数据的顺序也为R，这样就可以保证FIFO)，
 * 本节将要介绍的LinkedBlockingQueue是“可选边界”基于链表实现的FIFO队列。截至目前，本章所学习到的阻塞队列都是通过显式锁Lock进行共享数据的同步，
 * 以及与Lock关联的Condition进行线程间通知，因此该队列也适用于高并发的多线程环境中，是线程安全的类。
 * <p>
 * LinkedBlockingQueue队列的边界可选性是通过构造函数来决定的，当我们在创建LinkedBlockingQueue对象时，
 * 使用的是默认的构造函数，那么该队列的最大容量将为Integer的最大值(所谓的“无边界”),
 * 当然开发者可以通过指定队列最大容量(有边界)的方式创建队列。
 * <p>
 * 在使用方式上，LinkedBlockingQueue与ArrayBlockingQueue极其相似，因此这里将不再逐一赘述。
 */
public class LinkedBlockingQueueTest {

    @Test
    public void test() {
        //无参构造函数
        LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        //“无边界”
        assert queue.remainingCapacity() == Integer.MAX_VALUE;
        //构造LinkedBlockingQueue时指定边界
        LinkedBlockingQueue<Integer> queue2 = new LinkedBlockingQueue<>(10);
        assert queue2.remainingCapacity() == 10;
    }
}