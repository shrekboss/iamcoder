package org.coder.concurrency.programming.juc._4_colleciton;

import java.util.Objects;
import java.util.Random;

/**
 * 1.跳表(SkipList)
 * 通过上面的基准测试可知，链表数据结构在二分法查找的性能测试中几乎被数组链表完爆，此时你可能会想到使用树这样的数据结构，比如B+树，红黑树这样的平衡树会使检索速度提升不少，实际上也的确是这样的。
 * 但是红黑树这样的数据结构因为实现起来比较复杂，而且这样的平衡树在更新数据时还需要完成节点平衡等操作，所以人们在寻找一种更便捷的方法。于是跳表(SkipList)这种数据结构就应运而生了，
 * 其是由William Pugh于1990年提出的，感兴趣的读者可以查找William Pugh在当时发表的论文《Skip Lists: A Probabilistic Alternative to Balanced Trees》
 * (跳表：平衡树的另一个选择)，跳表也是Redis的主要数据结构之一。
 * 那么什么是跳表，跳表又是一种什么样的数据存储形式呢？这正是本节将要学习的内容。根据4.1.1节链表相关的内容，我们知道想要从一个链表中找到一个特定的元素，必须从header开始逐个进行对比和查找，
 * 直到找到包含数据的那个节点为止，这样一来，时间复杂度则为O(n)，如果要查找的元素刚好就在链表头部，那么时间复杂度为O(1)。
 * <p>
 * 对链表结构稍加改造，增加多个层级进行数据存储和查找，这种以空间换取时间的思路能够加快元素的查找速度。
 * <p>
 * 在增加了多个层级的链表中查找76这个元素会经历怎样的过程呢？首先到最高一层的链表中查找并且对比，可以发现100大于76；
 * 接下来直接来到第二层级发现33小于76，并且33的下一个元素100大于76因此再次来到下一个层级；
 * 对比33的下一个元素发现其就是76，正是我们需要寻找的那个。
 * <p>
 * 跳表(SkipList)正是受这种多层链表的想法启发而设计出来的。实际上，按照上面生成链表的方式来看，上面每一层链表的节点个数，都会是下面一层节点个数的一半左右，
 * 这样查找过程就非常类似于一个二分查找，使得查找的时间复杂度可以降低到O(log n)。另外，跳表中的元素在插入时就已经是根据排序规则进行排序的，在进行查找时无须再进行排序。
 * <p>
 * 2.跳表(SkipList)的实现
 * 大致了解了跳表的原理之后，是时候实现一个相应的数据结构了。首先，定义节点类，用于存储数据以及维护其与其他节点的关系。
 * <p>
 * 节点类的定义相较于在4.1.1节和4.1.2节中的Node显得有些复杂，的确，由于多层级链表的关系，需要维护更多的节点引用，因此会显得有些复杂。
 * 现在我们来定义在SimpleSkipList中的需要维护的属性。
 * <p>
 * 一切准备就绪，现在需要实现向跳表中增加元素的方法了。只不过在增加元素之前，首先需要为新元素找到合适的存放位置或者说是邻近的节点，
 * 在跳表中，最低的一层链表存放着全部的元素。因此想要找到合适的位置，是要从最高一层的head节点开始向最下一层查找，从而为新节点找到合适的位置。
 * <p>
 * 代码稍微有些复杂，下面通过图示的方式说明一下查找的过程，如图4-10所示。
 * L4 ————> 头节点 —————————————————————————————> 21 ———————————————————————> 尾节点
 * |									|							|
 * L3 ————> 头节点 —————————————————————————————> 21 —————————————> 211 ————> 尾节点
 * |									|				  |			|
 * L2 ————> 头节点 ————————————————————> 15 ————> 21 —————————————> 211 ————> 尾节点
 * |						   |		|				  |			|
 * L1 ————> 头节点 ————> 2 ————> 7 ————> 15 ————> 21 ————> 54 ————> 211 ————> 尾节点
 * 假设我们要为新的数据元素23找到一个恰当的位置，会经历怎样的过程呢？
 * 1).从head节点开始，head.right!=尾节点，并且head.right的value(21)<23。
 * 2).在while循环中尝试向右前行，发现21右边的节点是尾节点，因此退出while循环。
 * 3).第四层的节点21继续下移(current=current.down)至第三层。
 * 4).在while循环中，节点21右边的节点虽然不是尾节点，但是21.right.value(211)>23，因此不满足向右迁移的条件。
 * 5).第三层的节点21继续下移(current=current.down)至第二层。
 * 6).第二层的处理逻辑同第三层。
 * 7).在第一层中，21.right.value(54)>23，因此不会继续前行，另外第一层中，21.down=null，所以会终止整个大循环(for(;;))，那么我们为23找到的新位置就在节点21附近。
 * <p>
 * 跳表增加元素的操作我们已经完成了，下面再增加几个查询类方法，比如获取size，判断元素是否在跳表中等。
 */
public class SimpleSkipList {
    //每一层头部节点的标记符
    private final static byte HEAD_BIT = (byte) -1;
    //每一层尾部节点的标记符
    private final static byte TAIL_BIT = (byte) 1;
    //每一层数据节点的标记符
    private final static byte DATA_BIT = (byte) 0;

    //元素节点类，该节点中只存放整数类型
    private static class Node {
        //数据
        private Integer value;
        //每一个节点的周围节点引用(上下左右)
        private Node up, down, left, right;
        //节点类型
        private byte bit;

        public Node(Integer value) {
            this(value, DATA_BIT);
        }

        public Node(Integer value, byte bit) {
            this.value = value;
            this.bit = bit;
        }

        @Override
        public String toString() {
            return value + " bit:" + bit;
        }
    }

    //定义头部节点属性
    private Node head;
    //定义尾部节点属性
    private Node tail;
    //元素个数
    private int size;
    //跳表层高
    private int height;
    //随机数，主要用于通过随机的方式决定元素应该被放在第几层
    private Random random;

    //构造函数
    public SimpleSkipList() {
        //初始化头部和尾部节点
        this.head = new Node(null, HEAD_BIT);
        this.tail = new Node(null, TAIL_BIT);
        //头部节点的右边节点为尾部节点
        head.right = tail;
        //尾部节点的左边元素为头部节点
        tail.left = head;
        this.random = new Random(System.currentTimeMillis());
    }

    private Node find(Integer element) {
        //从head节点开始寻找
        Node current = head;
        for (; ; ) {
            //当前节点的右边节点不是尾节点，并且当前节点的右边节点数据小于element
            while (current.right.bit != TAIL_BIT && current.right.value <= element) {
                //继续朝右前行
                current = current.right;
            }
            //当current节点存在down节点
            if (current.down != null) {
                //开始向下一层
                current = current.down;
            } else {
                //到达最底层，终止循环
                break;
            }
        }
        return current;
    }

    public void add(Integer element) {
        //根据element找到合适它的存储位置，也就是邻近的节点，需要注意的是，此刻该节点在整个跳表的第一层
        Node nearNode = this.find(element);
        //定义一个新的节点
        Node newNode = new Node(element);
        //新节点的左节点为nearNode
        newNode.left = nearNode;
        //新节点的右节点为nearNode.right，相当于将新节点插入到了nearNode和nearNode.right中间
        newNode.right = nearNode.right;
        //更新nearNode.right.left节点为新节点
        nearNode.right.left = newNode;
        //nearNode.right节点为新节点
        nearNode.right = newNode;
        //当前层级为0，代表最底层第一层
        int currentLevel = 0;
        //根据随机值判断是否将新的节点放到新的层级，在跳表算法描述中，该动作被称为抛投硬币
        while (random.nextDouble() < 0.5d) {
            //如果currentLevel大于整个跳表的层高，则需要为跳表多增加一层链表
            if (currentLevel >= height) {
                height++;
                //定义新层高的head和tail
                Node dumyHead = new Node(null, HEAD_BIT);
                Node dumyTail = new Node(null, TAIL_BIT);
                //指定新层高head和tail的关系
                dumyHead.right = dumyTail;
                dumyHead.down = head;
                head.up = dumyHead;
                dumyTail.left = dumyHead;
                dumyTail.down = tail;
                tail.up = dumyTail;
                head = dumyHead;
                tail = dumyTail;
            }
            //在新的一层中增加element节点，同样要维护上下左右之间的关系
            while (nearNode != null && nearNode.up == null) {
                nearNode = nearNode.left;
            }
            nearNode = nearNode.up;
            Node upNode = new Node(element);
            upNode.left = nearNode;
            upNode.right = nearNode.right;
            upNode.down = newNode;
            nearNode.right.left = upNode;
            nearNode.right = upNode;
            newNode.up = upNode;
            newNode = upNode;
            currentLevel++;
        }
        //元素个数自增
        size++;
    }

    public boolean contains(Integer element) {
        Node node = this.find(element);
        if (node == null) {
            return false;
        } else {
            return Objects.equals(element, node.value);
        }
    }

    public Integer get(Integer element) {
        Node node = this.find(element);
        if (node == null) {
            return null;
        } else {
            return Objects.equals(element, node.value) ? node.value : null;
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SimpleSkipList skipList = new SimpleSkipList();
        assert skipList.isEmpty();
        skipList.add(10);
        skipList.add(23);
        skipList.add(56);
        assert skipList.size() == 3;
        assert skipList.contains(10);
        assert skipList.get(23) == 23;
    }

}