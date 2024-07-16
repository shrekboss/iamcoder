package org.coder.concurrency.programming.juc._4_colleciton;

import org.junit.Test;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * 4.2.2 PriorityBlockingQueue
 * PriorityBlockingQueue优先级阻塞队列是一个“无边界”阻塞队列，与4.1.2节所讲的优先级链表类似的是，
 * 该队列会根据某种规则对插入队列尾部的元素进行排序，因此该队列将不会遵循FIFO的约束。
 * 虽然PriorityBlockingQueue同ArrayBlockingQueue都实现自同样的接口，拥有同样的方法，但是大多数方法的实现确实具有很大的差别，
 * PriorityBlockingQueue也是线程安全的类，适用于高并发多线程的情况下。
 * <p>
 * 1.排序且无边界的队列
 * 只要应用程序的内存足够使用，理论上，PriorityBlockingQueue存放数据的数量是“无边界”的，在PriorityBlockingQueue内部维护了一个Object的数组，
 * 随着数据量的不断增多，该数组也会进行动态扩容。在构造PriorityBlockingQueue时虽然提供了一个整数类型的参数，但是该参数所代表的含义与ArrayBlockingQueue完全不同，
 * 前者是构造PriorityBlockingQueue的初始化容量，后者指定的整数类型参数则是ArrayBlockingQueue的最大容量。
 * <p>
 * 通过下面的代码片段，我们更能理解构造PriorityBlockingQueue时指定的整数类型参数其作用只不过是队列的初始化容量，并不代表它最多能存放2个数据元素，
 * 同时remainingCapacity()的返回值被hard code(硬编码)为Integer.MAX_VALUE。
 * <p>
 * 根据我们的理解，既然是优先级排序队列，为何在构造PriorityBlockingQueue时并未指定任何数据排序相关的接口呢?
 * 事实上，如果没有显示地指定Comparator，那么它将只支持实现了Comparable接口的数据类型。
 * 在下例中，Integer类型是Comparable的子类，因此我们并不需要指定Comparator，
 * 默认情况下，优先级最小的数据元素将放在队列头部，优先级最大的数据元素将被放在队列尾部。
 * <p>
 * 如果在创建PriorityBlockingQueue队列的时候既没有指定Comparator，同时数据元素也不是Comparable接口的子类，
 * 那么这种情况下，会出现类型转换的运行时异常。
 * ...省略
 * private static <T> void siftUpComparable(int k, T x, Object[], array) {
 * //强制类型转换，如果不是Comparable接口子类，转换时将会出现异常
 * Comparable<? super T> key = (Comparable<? super T> x;
 * ...省略
 * array[k] = key;
 * }
 * ...省略
 * 2.不存在阻塞写方法
 * 由于PriorityBlockingQueue是“无边界”的队列，因此将不存在队列上限临界值的控制，在PriorityBlockingQueue中，添加数据元素的所有方法都等价于offer方法，
 * 从队列的尾部添加数据，但是该数据会根据排序规则对数据进行排序。
 * ...省略
 * public boolean and(E e) {
 * return offer(e);
 * }
 * <p>
 * public boolean offer(E e, long timeout, TimeUnit unit) {
 * return offer(e);//never block
 * }
 * <p>
 * public void put(E e) {
 * offer(e);//never block
 * }
 * ...省略
 * <p>
 * <p>
 * <p>
 * 3.优先级队列读方法
 * 优先级队列添加元素的方法不存在阻塞(由于是“无边界”的)，但是针对优先级队列元素的读方法则与ArrayBlockingQueue类似，为了节约篇幅，本节将不再赘述。
 */
public class PriorityBlockingQueueTest {

    @Test
    public void test() {
        PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>(2);
        assert queue.remainingCapacity() == Integer.MAX_VALUE;
        queue.offer(1);
        queue.offer(10);
        queue.offer(14);
        queue.offer(3);

        assert queue.size() == 4;

        assert queue.poll() == 1;
        assert queue.poll() == 3;
        assert queue.poll() == 10;
        assert queue.poll() == 14;
    }
}