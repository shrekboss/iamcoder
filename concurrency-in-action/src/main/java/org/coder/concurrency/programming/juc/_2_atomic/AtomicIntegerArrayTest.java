package org.coder.concurrency.programming.juc._2_atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * AtomicArray 详解
 * int类型的数据可以封装成AtomicInteger类型，从此便可以通过原子性方式对int类型进行操作，如果高并发应用程序想要原子性地操作一个int类型的数组中的int数据时，那么该如何操做呢？
 * <p>
 * 或许你的第一反应是我们可以创建一个AtomicInteger类型的数组，其中的每一个元素都是AtomicInteger类型的，这样在高并发的应用程序中就可以原子性地操作数组中的某个元素了。
 * <p>
 * 事实上我们并不需要这样做，因为在Java原子包中提供了相应的原子性操作数组元素相关的类(如图2-5所示)。
 * 1.AtomicIntegerArray：提供了原子性操作int数据类型数组元素的操作。
 * 2.AtomicLongArray：提供了原子性操作long数据类型数组元素的操作。
 * 3.AtomicReferenceArray：提供了原子性操作对象引用数组元素的操作。
 * <p>
 * AtomicArray的使用方法比较简单，下面仅以AtomicIntegerArray为例简单地示范一下相关用法即可，为了节约篇幅避免重复内容，对上面三个原子数组类的操作方法希望读者能够自行学习。
 */
public class AtomicIntegerArrayTest {

    @Test
    public void AtomicIntegerArray() {
        //定义int类型的数组并且初始化
        int[] intArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        //创建AtomicIntegerArray并且传入int类型的数组
        AtomicIntegerArray intAtomicArr = new AtomicIntegerArray(intArray);
        //原子性地为intAtomicArr的第二个元素加10
        assert intAtomicArr.addAndGet(1, 10) == 12;
        //第二个元素更新后值为12
        assert intAtomicArr.get(1) == 12;
    }

    @Test
    public void getAndAccumulate() {

    }

    @Test
    public void accumulateAndGet() {

    }

    @Test
    public void getAndAdd() {

    }

    @Test
    public void addAndGet() {

    }

    @Test
    public void compareAndSet() {

    }

    @Test
    public void weakCompareAndSet() {

    }

    @Test
    public void getAndDecrement() {

    }

    @Test
    public void decrementAndGet() {

    }

    @Test
    public void getAndIncrement() {

    }

    @Test
    public void incrementAndGet() {

    }

    @Test
    public void getAndUpdate() {

    }

    @Test
    public void updateAndGet() {

    }

    @Test
    public void get() {

    }

    @Test
    public void getAndSet() {

    }

    @Test
    public void set() {

    }

    @Test
    public void lazySet() {

    }

}