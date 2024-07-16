package org.coder.concurrency.programming.juc._4_colleciton;

/**
 * 4.6.2 Lock Free 数据结构的测试
 * 1.基本功能测试
 * 基本功能测试主要用于测试我们实现的无锁链表的几个基本方法是否能够正常工作，相对来说，在单线程的环境中进行测试即可。
 * <p>
 * 运行下面的代码(记得要加JVM参数“-ea”)发现程序顺利执行，并没有什么错误出现，说明我们的无锁链表满足了既定的基本功能。
 */
public class LockFreeLinkedListTest {

    public static void main(String[] args) {
        //创建一个LockFreeLinkedList
        final LockFreeLinkedList<Integer> list = new LockFreeLinkedList<>();
        //初始化状态的断言
        assert list.isEmpty();
        assert list.count() == 0;
        assert list.peekFirst() == null;
        assert list.removeFirst() == null;
        //增加三个元素
        list.add(1);
        list.add(2);
        list.add(3);
        //再次断言
        assert !list.isEmpty();
        assert list.count() == 3;
        assert list.removeFirst() == 3;
        assert list.removeFirst() == 2;
        assert list.count() == 1;
        //将链表清空
        list.clear();
        //再次断言
        assert list.isEmpty();
        assert list.count() == 0;
        assert list.peekFirst() == null;
        assert list.removeFirst() == null;
    }

}