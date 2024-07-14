package org.coder.concurrency.programming.juc.atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;

/**
 * 2.1.2 AtomicInteger 的基本用法
 * 与int的引用类型Integer继承Number类一样，AtomicInteger也是Number类的一个子类，除此之外，AtomicInteger还提供了很多原子性的操作方法，
 * 本节将为大家逐一介绍。在AtomicInteger的内部有一个被volatile关键字修饰的成员变量value，实际上，AtomicInteger所提供的所有方法主要都是针对该变量value进行的操作。
 * <p>
 * 1.AtomicInteger的创建
 * ①public AtomicInteger()：创建AtomicInteger的初始值为0。
 * ②public AtomicInteger(int initialValue)：创建AtomicInteger并且指定初始值，无参的AtomicInteger对象创建等价于AtomicInteger(0)。
 * <p>
 * 2.AtomicInteger的Incremental操作
 * x++或者x=x+1这样的操作是非原子性的，要想使其具备原子性的特性，我们可以借助AtomicInteger中提供的原子性Incremental的操作方法。
 * <p>
 * 3.AtomicInteger的Decremental操作
 * x--或者x=x-1这样的自减操作同样也是非原子性的，要想使其具备原子性的特性，我们可以借助AtomicInteger中提供的原子性Decremenal的操作方法。
 * <p>
 * 4.原子性地更新value值
 * <p>
 * 5.AtomicInteger 与函数式接口
 * 自JDK1.8增加了函数式接口之后，AtomicInteger也提供了对函数式接口的支持。
 * <p>
 * 6.其他方法
 */
public class AtomicIntegerTest {
    /**
     * int getAndIncrement()：返回当前int类型的value值，然后对value进行自增运算(在2.1.3节中我们将学习到该方法的内部原理)，该操作方法能够确保对value的原子性增加操作。
     */
    @Test
    public void getAndIncrement() {
        final AtomicInteger ai = new AtomicInteger(5);
        //返回AtomicInteger的int值，然后自增(在多线程的情况下，下面的断言未必正确)
        assert ai.getAndIncrement() == 5;
        //获取自增后的结果(在多线程的情况下，下面的断言未必正确)
        assert ai.get() == 6;
    }

    /**
     * int incrementAndGet()：直接返回自增后的结果，该操作方法能够确保对value的原子性增量操作。
     */
    @Test
    public void incrementAndGet() {
        //定义AtomicInteger，初值为5
        final AtomicInteger ai = new AtomicInteger(5);
        //返回value自增后的结果
        assert ai.incrementAndGet() == 6;
        assert ai.get() == 6;
    }

    /**
     * int getAndDecrement()：返回当前int类型的value值，然后对value进行自减运算(在2.1.3节中我们将学习到该方法的内部原理)，该操作对方法能够确保对value的原子性减量操作。
     */
    @Test
    public void getAndDecrement() {
        AtomicInteger ai = new AtomicInteger(5);
        assert ai.getAndDecrement() == 5;
        assert ai.get() == 4;
    }

    /**
     * int decrementAndGet()：直接返回自减后的结果，该操作方法能够确保对value的原子性减量操作。
     */
    @Test
    public void decrementAndGet() {
        AtomicInteger ai = new AtomicInteger(5);
        assert ai.decrementAndGet() == 4;
        assert ai.get() == 4;
    }

    /**
     * boolean compareAndSet(int expect, int update)：原子性地更新AtomicInteger的值，其中expect代表当前的AtomicInteger数值，update则是需要设置的新值，
     * 该方法会返回一个boolean的结果：当expect和AtomicInteger的当前值不相等时，修改会失败，返回值为false，若修改成功则会返回true。
     * <p>
     * boolean weakCompareAndSet(int expect, int update)：目前版本JDK中的该方法与compareAndSet完全一样，源码如下所示。
     * <p>
     * public final boolean compareAndSet(int expect, int update) {
     * return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
     * }
     * <p>
     * public final boolean weakCompareAndSet(int expect, int update) {
     * return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
     * }
     * <p>
     * 通过源码我们不难发现两个方法的实现完全一样，那么为什么要有这两个方法呢?
     * 其实在JDK 1.6版本以前双方的实现是存在差异的，compareAndSet方法的底层主要是针对Intel x86 架构下的CPU指令CAS:cmpxchg(sparc-TSO，ia64的CPU架构也支持)，
     * 但是ARM CPU 架构下的类似指令为LL/SC:ldrex/strex(ARM架构下的CPU主要应用于当下的移动互联网设备，比如在智能手机终端设备中，高通骁龙、华为麒麟等系列都是基于ARM架构和指令集下的CPU产品)，
     * 或许在运行Android的JVM设备上这两个方法底层存在着差异。
     */
    @Test
    public void compareAndSet() {
        //定义一个AtomicInteger类型的对象ai并且指定初值为10
        AtomicInteger ai = new AtomicInteger(10);
        //调用compareAndSet方法，expect的值为100，修改肯定会失败
        assert !ai.compareAndSet(100, 12);
        //修改并未成功，因此新值不等于12
        assert ai.get() != 12;
        //执行了compareAndSet更新方法之后，ai的返回值依然为10，因为修改失败
        assert ai.get() == 10;
        //调用compareAndSet方法，expect的值为10，修改成功(多线程情况下并不能担保百分之百成功，关于2.1.3节中会为大家讲解)
        assert ai.compareAndSet(10, 12);
        //断言成功
        assert ai.get() == 12;
    }

    /**
     * int getAndAdd(int delta): 原子性地更新AtomicInteger的value值，更新后的value为value和delta之和，方法的返回值为value的前一个值，
     * 该方法实际上是基于自旋 + CAS算法实现的(Compare And Swap)原子性操作。
     */
    @Test
    public void getAndAdd() {
        //定义一个AtomicInteger类型的对象ai并且指定初值为10
        AtomicInteger ai = new AtomicInteger(10);
        //调用getAndAdd方法，返回value的前一个值为10
        assert ai.getAndAdd(2) == 10;
        //调用get方法返回AtomicInteger的value值，当前返回值为12
        assert ai.get() == 12;
    }

    /**
     * int addAndGet(int delta):该方法与getAndAdd(int delta)一样，也是原子性地更新AtomicInteger的value值，
     * 更新后的结果value为value和delta之和，但是该方法会立即返回更新后的value值。
     */
    @Test
    public void addAndGet() {
        //定义一个AtomicInteger类型的对象ai并且指定初值为10
        AtomicInteger ai = new AtomicInteger(10);
        //调用addAndGet方法，返回当前value的值
        assert ai.addAndGet(2) == 12;
        //调用get方法返回AtomicInteger的value值，当前返回值为12
        assert ai.get() == 12;
    }

    /**
     * int getAndUpdate(IntUnaryOperator updateFunction):原子性地更新AtomicInteger的值，方法入参为IntUnaryOperator接口，返回值为value更新之前的值。
     *
     * @FunctionalInterface public interface IntUnaryOperator {
     * //入参为被操作数，对应于AtomicInteger的当前value值
     * int applyAsInt(int operand);
     * }
     * IntUnaryOperator为函数式接口，有且仅有一个接口方法(非静态，非default)，接口方法的返回值即AtomicInteger被更新后的value的最新值。
     */
    @Test
    public void getAndUpdate() {
        //定义一个AtomicInteger类型的对象ai并且指定初值为10
        AtomicInteger ai = new AtomicInteger(10);
        //调用getAndUpdate方法并且传入lambda表达式，返回结果为value的前一个值
        assert ai.getAndUpdate(x -> x + 2) == 10;
        //调用get方法返回AtomicInteger的value值，当前返回值为12
        assert ai.get() == 12;
    }

    /**
     * int updateAndGet(IntUnaryOperator updateFunction): 原子性地更新AtomicInteger的值，方法入参为IntUnaryOperator接口，
     * 该方法会立即返回更新后的value值。
     */
    @Test
    public void updateAndGet() {
        //定义一个AtomicInteger类型的对象ai并且指定初值为10
        AtomicInteger ai = new AtomicInteger(10);
        //调用updateAndGet方法并且传入lambda表达式，方法结果为value更新后的值
        assert ai.updateAndGet(x -> x + 2) == 12;
        //调用get方法返回AtomicInteger的value值，当前返回值为12
        assert ai.get() == 12;
    }

    /**
     * 原子性地更新AtomicInteger的值，方法入参为IntBinaryOperator接口和delta值x，返回值为value更新之前的值。
     *
     * @FunctionalInterface public interface IntBinaryOperator {
     * //该接口在getAndAccumulate方法中，left为AtomicInteger value的当前值，right为delta值，返回值将被用于更新AtomicInteger的value值
     * int applyAsInt(int left, int right);
     * }
     * IntBinaryOperator为函数式接口，有且仅有一个接口方法（非静态，非default），接口方法的返回值即AtomicInteger被更新后的value的最新值。
     */
    @Test
    public void getAndAccumulate() {
        //定义一个AtomicInteger类型的对象ai并且指定初值为10
        AtomicInteger ai = new AtomicInteger(10);
        int result = ai.getAndAccumulate(5, new IntBinaryOperator() {

            @Override
            public int applyAsInt(int left, int right) {
                assert left == 10;
                assert right == 5;
                return left + right;
            }
        });
        assert result == 10;
        assert ai.get() == 15;
    }

    /**
     * int accumulateAndGet(int x, IntBinaryOperator accoumulatorFunction):该方法与getAndAccumulate类似，只不过会立即返回AtomicInteger的更新值。
     */
    @Test
    public void accumulateAndGet() {
        //定义一个AtomicInteger类型的对象ai并且指定初值为10
        AtomicInteger ai = new AtomicInteger(10);
        int result = ai.accumulateAndGet(5, Integer::sum);
        assert result == 15;
        assert ai.get() == 15;
    }

}