package org.coder.concurrency.programming.juc._4_colleciton;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 4.6 高并发无锁(Lock Free)数据结构的实现
 * 如果说实现一个高效的数据结构是一件复杂的事情，那么实现一个高性能的在高并发多线程下数据结构更是需要考虑非常多的因素
 * (例如，各种操作的时间复杂度、空间复杂度、线程一致性、锁的最小化粒度，甚至是无锁)，Java程序的开发者们是幸运的，
 * 因为有非常多的、优秀的、大师级的专家在这个领域为我们贡献了大量开箱即用的容器，为我们屏蔽掉了非常复杂琐碎的细节，
 * 这才使得我们可以轻而易举地基于这些容器开发出高效、高质量的应用软件和系统。
 * <p>
 * 本节将结合本书已经学习到的相关知识实现一个无锁的数据结构(提示，自己实现的无锁数据结构，除非已经经过了较长时间的性能测试，
 * 稳定性测试证明没有问题方可应用于生产环境之中，否则没有什么特殊情况还是直接使用JDK自带的高并发容器，
 * 本节将要展示的无锁链表是以教学目的为出发点，毕竟一个无锁的、高并发的、可应用于生产的数据结构不是轻而易举就能开发出来的)。
 * <p>
 * 4.6.1 高并发无锁链表的实现
 * 既然是链表，那么理所当然会有Node节点的存在了，与4.1节中所讲的节点类似，简单定义一个Node<E>，代码如下。
 * <p>
 * 这里同样采用FILO的栈结构，因此在该链表中需要存在一个属性代表当前链表的头，由于要使得链表头元素所有的操作均具备原子性，且是无锁的要求，
 * 因此我们使用AtomicStampedReference对Node节点进行原子性封装。
 * <p>
 * 链表结构之所以应用广泛，主要原因之一是通过节点(Node)引用组织链表中节点间的关系非常灵活，实现诸如clear、isEmpty等方法非常容易，
 * 只需要判断头节点即可，下面是我们实现的几个辅助方法。
 * <p>
 * 几个不是很复杂的辅助方法介绍完毕之后，我们需要完成对元素的增加和删除方法的实现，首先，看一下增加元素的方法，
 * 在增加元素的方法中，我们采用CAS+自旋的操作方式来确保数据可以被成功地插入链表之中。
 * <p>
 * Add方法看起来稍微有些复杂，但这些都是我们之前学习过的知识，相信掌握了的读者理解起来并不是很困难，下面再来实现移除头部节点的方法。
 * <p>
 * 至此，我们需要的几个主要功能都已经完成了，接下来对其进行测试：
 * 1)单线程下的功能测试；
 * 2)多线程下的功能测试(除了要确保线程安全之外还要保证数据的合理性，这一点是比较难测试的)。
 *
 * @param <E>
 */
public class LockFreeLinkedList<E> {
    //AtomicStampedReference既可以保证引用类型的读写原子性又可以避免ABA问题的出现
    private AtomicStampedReference<Node<E>> headRef = null;

    //构造函数
    public LockFreeLinkedList() {
        //初始化headRef，一个空的链表，head为null，并且指定初始化的stamp
        this.headRef = new AtomicStampedReference<>(null, 0);
    }

    //链表是否为空，只需要判断头节点即可
    public boolean isEmpty() {
        return this.headRef.getReference() == null;
    }

    //清空链表只需要将头节点的值设置为null即可
    public void clear() {
        this.headRef.set(null, headRef.getStamp() + 1);
    }

    //peek时只需要返回头节点的元素即可，当然如果此刻链表为空则返回null
    public E peekFirst() {
        return isEmpty() ? null : this.headRef.getReference().element;
    }

    //count方法与之前所说的ConcurrentLinkedQueue size类似，是一个效率比较低的方法，
    //第一，需要遍历全部的元素进行计算，第二，由于不加锁的缘故在多线程的情况下返回值只是一个近似值而不是精确值
    public long count() {
        long count = 0;
        Node<E> currentNode = this.headRef.getReference();
        //遍历全部元素，进行累加
        while (currentNode != null) {
            count++;
            currentNode = currentNode.next;
        }
        return count;
    }

    public void add(E element) {
        //不允许null值插入链表中
        if (null == element)
            throw new NullPointerException("The element is null");
        Node<E> previousNode;
        int previousStamp;
        Node<E> newNode;
        do {
            //首先获取头部节点
            previousNode = this.headRef.getReference();
            //其次获取headRef的stamp值
            previousStamp = this.headRef.getStamp();
            //创建新的节点(当然你也可以将其放到定义时再创建)
            newNode = new Node<>(element);
            //新节点的下一个元素为当前的头部节点
            newNode.next = previousNode;
            //那么这个时候我们需要让新节点成为头部节点，但是别忘了在多线程高并发的环境下，头部节点有可能已经被其他线程更改了，
            //因此我们需要通过自旋的方式多次尝试，直到成功
        } while (!this.headRef.compareAndSet(previousNode, newNode, previousStamp, previousStamp + 1));
    }

    public E removeFirst() {
        //如果当前链表为空，则直接返回，不做移除操作
        if (isEmpty())
            return null;
        Node<E> currentNode;
        int currentStamp;
        Node<E> nextNode;
        do {
            //获取头部节点
            currentNode = this.headRef.getReference();
            //获取当前的stamp
            currentStamp = this.headRef.getStamp();
            //移除头部节点，就是让头部节点的下一个节点成为头部节点
            if (currentNode == null) {
                break;
            }
            nextNode = currentNode.next;
            //同Add方法的自旋操作
        } while (!this.headRef.compareAndSet(currentNode, nextNode, currentStamp, currentStamp + 1));
        //返回头部节点的数据元素
        return currentNode == null ? null : currentNode.element;
    }

    //私有的静态内部类，关于node中的属性这里不做过多解释
    private static class Node<E> {
        E element;
        volatile Node<E> next;

        Node(E element) {
            this.element = element;
        }

        @Override
        public String toString() {
            return element == null ? "" : element.toString();
        }
    }
}