package org.coder.concurrency.programming.juc._4_colleciton;

/**
 * 4.2.6 LinkedBlockingDeque
 * LinkedBlockingDeque是一个基于链表实现的双向(Double Ended Queue，Deque)阻塞队列，双向队列支持在队尾写入数据，读取移除数据；在队头写入数据，读取移除数据。
 * LinkedBlockingDeque实现自BlockingDeque(BlockingDeque又是BlockingQueue的子接口)，并且支持可选“边界”，与LinkedBlockingQueue一样，
 * 对边界的指定在构造LinkedBlockingDeque时就已经确定了。双向队列如图4-12所示。
 * <p>
 * 既然是双向队列，那么LinkedBlockingDeque所提供的操作方法要比单向队列丰富很多，为了节省篇幅，此处将不再展开介绍，对逐个方法进行讲述，
 * 读者可以通过阅读JDK官方文档或者其他方式进行学习和掌握。
 */
public class LinkedBlockingDequeTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}