package org.coder.concurrency.programming.juc._2_atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 2.2 AtomicBoolean 详解
 * AtomicBoolean提供了一种原子性地读写布尔类型变量的解决方案，通常情况下，该类将被用于原子性地更新状态标识位，比如flag。
 * <p>
 * 2.2.1 AtomicBoolean的基本用法
 * AtomicBoolean提供的方法比较少也比简单，本节只对其做简单介绍，其基本原理与AtomicInteger极为类似。
 * (1) AtomicBoolean的创建
 * (2) AtomicBoolean值的更新
 * 1.compareAndersonSet(boolean expect, boolean update):对比并且设置boolean最新的值，类似于AtomicInteger的compareAndSet方法，
 * 期望值与AtomicBoolean的当前值一致时执行新值的设置动作，若设置成功则返回true，否则直接返回false。
 * 2.weakCompareAndSet(boolean expect, boolean update):同上。
 * 3.set(boolean newValue):设置AtomicBoolean最新的value值，该新值的更新对其他线程立即可见。
 * 4.getAndSet(boolean newValue):返回AtomicBoolean的前一个布尔值，并且设置最新的值。
 * 5.lazySet(boolean newValue):设置AtomicBoolean的布尔值，关于lazySet方法的原理已经在2.1节中介绍过了，这里不再赘述。
 * (3)其他方法
 * 1.get():获取AtomicBoolean的当前布尔值。
 * <p>
 * 2.2.2 AtomicBoolean内幕
 * AtomicBoolean的实现方式比较类似于AtomicInteger类，实际上AtomicBoolean内部的value本身就是一个volatile关键字修饰符的int类型成员属性。
 * <p>
 * public class AtomicBoolean implements java.io.Serializable {
 * private static final long serialVersionUID = 4654671469794556979L;
 * //setup to use Unsafe.compareAndSwapInt for updates
 * private static final Unsafe unsafe = Unsafe.getUnsafe();
 * //valueOffset 将用于存放value的内存地址偏移量
 * private static final long valueOffset;
 * static {
 * try {
 * //获取value的内存地址偏移量
 * valueOffset = unsafe.class.getDeclareField("value"));
 * }catch (Exception ex) {
 * throw new Error(ex);
 * }
 * }
 * private volatile int value;
 * }
 */
public class AtomicBooleanTest {

    @Test
    public void atomicBoolean() {
        //AtomicBoolean 无参构造
        AtomicBoolean ab = new AtomicBoolean();
        assert !ab.get();
        //AtomicBoolean 无参构造，等价于AtomicBoolean(false)
        ab = new AtomicBoolean(false);
        assert !ab.get();
    }

    @Test
    public void compareAndSet() {
        //无参构造AtomicBoolean，默认为false
        AtomicBoolean ab = new AtomicBoolean();
        //更改失败
        assert !ab.compareAndSet(true, false);
        //ab.get() == false
        assert !ab.get();
        //更改成功
        assert ab.compareAndSet(false, true);
        //更改后的值为true
        assert ab.get();
    }

    @Test
    public void set() {
        //无参构造AtomicBoolean，默认为false
        AtomicBoolean ab = new AtomicBoolean();
        assert !ab.get();
        ab.set(true);
        assert ab.get();
    }

    @Test
    public void getAndSet() {
        //无参构造AtomicBoolean，默认为false
        AtomicBoolean ab = new AtomicBoolean();
        assert !ab.get();
        assert !ab.getAndSet(true);
        assert ab.get();
    }

    @Test
    public void lazySet() {
        //无参构造AtomicBoolean，默认为false
        AtomicBoolean ab = new AtomicBoolean();
        assert !ab.get();
        ab.lazySet(true);
        assert ab.get();

    }

    @Test
    public void get() {
        //无参构造AtomicBoolean，默认为false
        AtomicBoolean ab = new AtomicBoolean();
        assert !ab.get();
    }
}