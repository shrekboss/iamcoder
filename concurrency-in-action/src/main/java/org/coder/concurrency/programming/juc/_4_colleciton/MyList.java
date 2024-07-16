package org.coder.concurrency.programming.juc._4_colleciton;

/**
 * 第四章 Java并发包之并发容器详解
 * 本章将学习Java并发包中的并发容器部分，所谓并发容器是指在高并发应用程序的使用过程中，这些容器(数据结构)是线程安全的，而且在高并发的程序中运行它们会有高效的性能表现。
 * jdk的开发者通过反复的版本迭代(几乎每一次jdk版本的升级都会看到对这些数据结构、容器的改进和升级)和性能优化为开发者提供了很多拿来即用的容器类，在多线程高并发的环境中
 * 进行批量数据交互几乎无法离开对容器的使用，那么如何进行最小力度的加锁操作以确保共享资源的同步，甚至是在无锁的情况之下确保线程安全地使用共享资源，这些都是非常具有挑战性
 * 且难度很高的开发要求。回想我们接触过的生产者消费者模式，我们会对数据队列的每一个方法进行加锁同步的操作：使用LinkedList<E>作为共享数据的队列对生产者和消费者线程
 * 进行解耦，那么为了使得生产者与生产者线程之间，消费者与消费者线程之间，生产者与消费者线程之间对LinkedList<E>的访问是线程安全的，我们需要对操作与其相关的方法进行
 * 加锁同步保护，从而确保不同的线程之间看到的数据是一致的，进而排除线程安全相关的隐患。学习完本章之后，大家不需要再自行开发这样的线程安全容器，直接使用jdk自带的并发容器
 * 就能很好地解决诸如此类的问题。
 * <p>
 * 本章将学习到十几个高并发容器的使用，除此之外我们还将结合在本书中的内容开发一个无锁的线程安全的容器，需要声明的一点是：本书并不是一本关于数据结构的书，
 * 所以关于链表、栈、堆、树、红黑树、AVL树、2-3-4树、二叉树、B+树、图等的知识，请大家学习和参考相关的数据结构类书籍(与高并发一样，数据结构也是程序员必须修炼的内功之一，
 * 强烈建议每一位程序员熟练地掌握基本、常用的数据结构原理，如果能够不假思索地使用你所擅长的语言轻而易举地开发出对应的数据结构，那当然是最好了)。
 * <p>
 * 4.1 链表
 * 虽然本章在开头已经声明这不是一本关于数据结构的书，但是为了保证学习效果，这里还是要简单地介绍一下相关的数据结构，鉴于链表类型的数据结构在使用中最为广泛
 * (实际上无论是Java并发包中的数据结构，还是Java的并发类工具底层都有链表的使用场景)，因此本节将讲解链表数据结构的相关知识，如果读者对链表已经非常熟悉，
 * 则可以跳过本节的内容，并不会妨碍对本章内容的阅读和学习。
 * <p>
 * 4.1.1 基本的链表
 * 所谓链表，实际上就是线性表的链式存储方式，有别于数组连续式内存空间，链表并不是连续的内存存储结构。
 * 在链表的每一个节点中，至少包含着两个基本属性：数据本身和指向下一个节点的引用或者指针。
 * 从图4-1中我们可以很清晰地看出链表内部节点之间的存储关系，而根据链表节点元素的不同访问形式就可以演化出栈，即最先进入链表结构的元素最后一个被访问(FILO:first in last out或者last in first out)；
 * 还可以演化出队列，即最先进入链表结构的元素第一个被访问(FIFO或LILO)；此外在链表元素节点中多增加一个指针属性就可以演化出二叉树等，
 * 所以说链表数据结构是最基本、最常用的数据结构一点都不为过。链表还包含双向链表、循环链表等。
 * <p>
 * 在了解了链表的基本原理之后，下面使用Java程序实现一个简单的链表结构以加深读者对链表的认识。
 * 首先，定义一个Node节点，该节点类主要用于存储数据和指向下一个元素的引用。
 * (1)链表的结构
 * 链表中有一个非常重要的元素head，它代表当前节点元素的引用，当链表被初始化时，当前节点属性指向为null。
 * (2)链表数据的清空以及是否为空的判断
 * 有了当前节点的引用，再确认链表是否为空，或进行链表清空等操作就非常容易了，无须对链表中整个元素进行判断，只需要针对链表当前节点的引用进行相关的操作即可。
 * (3)向链表头部增加元素
 * 在链表中增加元素，相对于在数组中的操作来说，是非常灵活且简单的(无须进行数组拷贝)，只需要更改当前节点元素的引用即可实现。
 * (4)链表的peekFirst操作
 * peek操作不会对当前链表产生任何副作用，其只是简单地返回当前链表头部的数据。当然了，如果链表为空，则会抛出异常(为什么要抛出异常呢？返回null可不可以呢？
 * 其实这并没有什么不妥，链表可以存放任何类型的数据，当然它也可以存放值为null的数据，如果当前节点引用的value值恰巧为null，
 * 那么当peek操作在链表为空的情况下也返回null则势必会产生一些歧义，因此直接抛出异常将更加直观一些)。
 * (5)链表元素的弹出操作
 * 在链表结构中，插入元素、删除元素的实现都非常容易，只需要更新节点引用的指向即可，在本书的示例中其实更多地像是使用链表实现了一个栈的数据结构，
 * 因此当栈头元素弹出时，只需要让链表当前节点的引用指向已弹出节点的下一节点即可。
 * (6)其他方法
 * 在前面设计的简单单向链表中还提供了对size的获取方法，以及输出当前链表元素的方法。
 * (7)简单测试
 * 我们基本上实现了一个简单的链表，准确地讲更像是Stack(栈)数据结构基于链表的实现。
 * 当然，读者朋友还可以基于本书中的代码增加delete操作、find操作，根据下标进行insert操作等，
 * 限于篇幅，此处就不再赘述了，感兴趣的读者可以自行丰富完善，哪怕是当作一次练习也挺好的。
 * 下面对该链表进行一个简单的测试：
 * <p>
 * 运行上面的程序，一切都不如我们期望的那样正常输出，说明我们最基本的链表定义以及链表方法都没有什么问题，相信通过本节的讲解，大家对链表这样一个数据结构已有一个大概的了解。
 * 正如前文中所描述的那样，链表在数据结构中是非常基础、非常底层的线性结构，很多数据结构都可以基于链表构造和演化出来，因此想要熟练地掌握数据结构，链表始终是一道绕不开的门。
 *
 * @param <E>
 */
public class MyList<E> {
    //当前节点引用
    private Node<E> header;
    //链表元素的个数
    private int size;

    public MyList() {
        //当前元素节点为指向NULL的属性
        this.header = null;
    }

    //判断当前列表是否为空
    public boolean isEmpty() {
        //只需要判断当前节点引用是否为null即可得知
        return header == null;
    }

    //清除链表中的所有元素
    public void clear() {
        //显式设定当前size为0
        this.size = 0;
        //将当前节点引用设置为null即可，由于其他元素ROOT不可达，因此在稍后的垃圾回收中将会被回收
        this.header = null;
    }

    public void add(E e) {
        //定义新的node节点，并且其next引用指向当前节点所引用的header
        Node<E> node = new Node<>(e, header);
        //将当前节点header指向新的node节点
        this.header = node;
        //元素数量加一
        this.size++;
    }

    public E peekFirst() {
        //如果为空则直接抛出异常
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("The linked list is empty now, can't support peek operation");
        }
        //返回当前节点的元素数据
        return header.getValue();
    }

    public E popFirst() {
        //判断当前链表是否为空
        if (isEmpty()) {
            //如果为空则直接抛出异常
            throw new IndexOutOfBoundsException("The linked list is empty now, can't support pop operation");
        }
        //获取当前节点的数据，作为该方法的最终返回值
        final E value = header.getValue();
        //将链表的当前节点引用直接指向当前节点的下一个节点
        this.header = header.getNext();
        //元素数量减一
        this.size--;
        //返回数据
        return value;
    }

    //获取当前链表的元素个数
    public int size() {
        //直接返回内部成员属性size即可
        return this.size;
    }

    //重写toString方法，将列表中的每一个元素进行连接后输出
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

    //Node是一个泛型类，可用于存储任意类型元素的节点
    private static class Node<T> {
        //数据属性
        private final T value;
        //指向下一个节点的引用
        private final Node<T> next;

        private Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }

        public T getValue() {
            return value;
        }

        public Node<T> getNext() {
            return next;
        }
    }

    public static void main(String[] args) {
        //定义类型为Integer的链表，并且增加5个元素
        MyList<Integer> list = new MyList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        //对mylist进行测试
        System.out.println(list);
        System.out.println(list.size());
        System.out.println(list.isEmpty());
        System.out.println(list.peekFirst());
        System.out.println("=================================================");
        System.out.println(list.popFirst());
        System.out.println(list.size());
        System.out.println(list);
        System.out.println(list.peekFirst());
        System.out.println(list.popFirst());
        System.out.println(list.popFirst());
        System.out.println(list.popFirst());
        System.out.println(list.popFirst());
        System.out.println("=================================================");
        System.out.println(list.isEmpty());
        System.out.println(list.size());
        System.out.println(list);
    }
}