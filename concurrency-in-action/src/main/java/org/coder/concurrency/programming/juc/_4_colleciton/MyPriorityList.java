package org.coder.concurrency.programming.juc._4_colleciton;

import java.util.Comparator;
import java.util.Objects;

/**
 * 4.1.2 优先级链表
 * 在某些场景下，我们需要对队列或者栈中的元素根据某种特定的顺序进行排序，比如，在移动电话台席中，全球通用户的业务受理将具有优先权；
 * 在银行等金融类业务中，等级越高的个人用户，受理业务的优先权也就越高，等等。本节将基于4.1.1节中基本链表的实现，增加对元素排序的支持，
 * 也就是说链表中的元素具备了某种规则下的优先级。
 * <p>
 * 首先，需要重新改造一下优先级链表的泛型类型和Node节点的定义。
 * <p>
 * 除此之外，我们还需要改造add方法，在该方法中，每一个新元素的加入都需要进行比较，比较的目的当然是要在遍历已经存储于链表中的元素后，找到适当的位置将其加入。
 * <p>
 * add方法比4.1.1节中介绍的方法要复杂的很多，具体的原理无外乎就是找到合适的位置，然后将新的节点存入在链表中。
 * 现在进行一下简单的测试，验证我们的优先级链表是否能够正常工作。
 *
 * @param <E>
 */
//增加泛型约束，每一个被加入该链表中的元素都必须实现Comparable接口，就像基本数据类型String一样
public class MyPriorityList<E extends Comparable<E>> {
    private Node<E> header;
    private int size;
    //增加了Comparator接口属性
    private final Comparator<E> comparator;

    //在构造函数中强制要求必须要有Comparator接口
    public MyPriorityList(Comparator<E> comparator) {
        this.comparator = Objects.requireNonNull(comparator);
        this.header = null;
    }

    public void add(E e) {
        //定义一个新的node节点，其指向下一个节点的引用值为null
        final Node<E> newNode = new Node<>(e);
        //当前链表节点引用
        Node<E> current = this.header;
        //上一个节点的引用，初始值为null，在稍后的计算中会得到
        Node<E> previous = null;
        //循环遍历链表(当前节点不为null，既不是空的链表)
        while (current != null && e.compareTo(current.getValue()) > 0) {
            //前一个节点为当前节点
            previous = current;
            //当前节点为当前节点的下一个节点
            current = current.getNext();
        }
        //如果链表为空
        if (previous == null) {
            //链表的当前节点引用将直接作为新构造的节点
            this.header = newNode;
        } else {
            //将新的节点插入前一个节点之后
            previous.setNext(newNode);
        }
        //新节点的下一个节点为current节点
        newNode.setNext(current);
        this.size++;
    }

    public boolean isEmpty() {
        return header == null;
    }

    public E popFirst() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("The linked list is empty now, can't support pop operation");
        }
        final E value = header.getValue();
        this.header = header.getNext();
        this.size--;
        return value;
    }

    @Override
    public String toString() {
        Node<E> node = this.header;
        final StringBuilder builder = new StringBuilder("[");
        while (node != null) {
            builder.append(node.getValue().toString()).append(",");
            node = node.getNext();
        }
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }

    //Node节点的泛型类型同样增加了相关的约束，并且取缔了value和next字段不可变的特性
    private static class Node<T extends Comparable<T>> {
        private T value;
        private Node<T> next;

        private Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }

        private Node(T value) {
            this(value, null);
        }

        public T getValue() {
            return value;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }
    }

    public static void main(String[] args) {
        // 定义MyPriorityList，并且指定Comparator
        MyPriorityList<Integer> list = new MyPriorityList<>((o1, o2) -> o1 - o2);
        list.add(45);
        System.out.println(list);
        System.out.println("=======================================");
        list.add(456);
        list.add(4);
        list.add(48);
        list.add(500);
        System.out.println(list);
        System.out.println("pop first：" + list.popFirst());
        System.out.println(list);
    }

}