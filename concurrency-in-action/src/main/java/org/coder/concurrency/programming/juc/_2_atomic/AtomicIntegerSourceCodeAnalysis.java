package org.coder.concurrency.programming.juc._2_atomic;

/**
 * 2.1.3 AtomicInteger内幕
 * 经过了详细的AtomicInteger的使用方法的学习，本节就来看看AtomicInteger类的内部原理，以更加深入地了解AtomicInteger的内幕。
 * //Unsafe是由C++实现的，其内部存在着大量的汇编CPU指令等代码，JDK实现的
 * //Lock Free几乎完全依赖于该类
 * private static final Unsafe unsafe = Unsafe.getUnsafe();
 * //valueOffset 将用于存放value的内存地址偏移量
 * private static final long valueOffset;
 * static {
 * try {
 * //获取value的内存地址偏移量
 * valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
 * }catch (Exception ex) {
 * throw new new Error(ex);
 * }
 * }
 * //我们不止一次地说过，在AtomicInteger的内部有一个volatile修饰的int类型成员属性value
 * private volatile int value;
 * <p>
 * 1.compareAndSwapInt源码分析————CAS算法
 * CAS包含3个操作数：内存值V、旧的预期值A、要修改的新值B。当且仅当预期值A与内存值V相等时，将内存值V修改为B，否则什么都不需要做。
 * compareAndSwapInt方法是一个native方法，提供了CAS(CompareAndSwap)算法的实现，AtomicInteger类中的原子性方法几乎都借助于该方法实现。
 * ...
 * public final boolean weakCompareAndSet(int expect, int update) {
 * return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
 * }
 * ...
 * public final boolean compareAndSet(int expect, int update) {
 * return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
 * }
 * ...
 * public final int getAndIncrement() {
 * return unsafe.getAndAddInt(this, valueOffset, 1);
 * }
 * ...
 * //Unsafe 内部方法 getAndInt源码
 * public final int getAndAddInt(Object var1, long var2, int var4) {
 * int var5;
 * do {
 * <p>
 * } while(!this.compareAndSwapInt(var1, var2, var5, var5, + var4));
 * return var5;
 * }
 * 进入Unsafe源码中我们会看到compareAndersonSwapInt源码。
 * 由于该方法无法正常反编译，因此笔者在此将方法的入参名进行了一下修改，也许与大家看到的源码存在一些出入
 * object: 该入参是地址偏移量所在的宿主对象
 * valueOffset: 该入参是object对象某属性的地址偏移量，是由Unsafe对象获得
 * expectValue: 该值是我们期望value当前的值，如果expectValue与实际的当前值不相等，那么对value的修改
 * newValue: 新值
 * public final native boolean compareAndSwapInt(Object object, long valueOffset, int expectValue, int newValue);
 * <p>
 * 通过对compareAndersonSwapInt方法的简单分析，我们不禁会产生一个疑问，既然可以通过AtomicInteger获得当前值，那么为什么还会出现expectValue和AtomicInteger当前值不相等的情况呢？
 * 比如下面的代码片段。
 * AtomicInteger ai = new AtomicInteger(2);
 * ai.compareAndSet(ai.get(), 10);
 * <p>
 * 原因是相对于synchronized关键字、显式锁Lock，AtomicInteger所提供的方法不具备排他性，当A线程通过get()方法获取了AtomicInteger value的当前值后，B线程对value的修改已经顺利完成；
 * A线程试图再次修改的时候就会出现expectValue与value的当前值不相等的情况，因此会出现修改失败，这种方式也被称为乐观锁。对数据进行修改的时候，首先需要进行比较。
 * <p>
 * 由于compareAndSwapInt是本地方法，因此我们必须打开JDK的源码才能看到相关的C++源码，打开openjdk-jdk8u/hotspot/src/share/vm/prims/unsafe.cpp文件我们会找到相关的C++代码。
 * UNSAFE_ENTRY(jboolean, Unsafe_CompareAndSwapInt(JNIEnv *env, jobject unsafe, jobject obj, jlong offset, jint e, jint x))
 * UnsafeWrapper("Unsafe_CompareAndSwapInt");
 * oop p = JNIHandles::resolve(obj);
 * //根据地址偏移量获取内存地址
 * jint* addr = (jint *) index_oop_from_field_offset_long(p, offset);
 * //调用Atomic的成员方法
 * return (jint) (Atomic::cmpxchg(x, addr, e)) == e;
 * UNSAFE_END
 * <p>
 * 在C++代码中，我们不难发现Unsafe_CompareAndSwapInt方法依赖于Atomic::cmpxchg方法，该方法实际上会调用不同的CPU架构下的汇编代码（汇编代码主要用于执行相关的CPU指令）。
 * 下面打开基于x86架构的Atomic::cmpxchg源码文件openjdk-jdk8u/hotspot/src/os_cpu/bsd_x86/vm/atomic_bsd_x86.inline.hpp。
 * inline jint Atomic::cmpxchg (jint exchange_value, volatile jint* dest, jint compare_value) {
 * int mp = os::is_MP();
 * __asm__ volatile (LOCK_IF_MP(%4) "cmpxchg1 %1, (%3)"
 * : "=a" (exchange_value)
 * : "r" (exchange_value), "a" (compare_value), "r" (dest), "r" (mp)
 * : "cc", "memory");
 * return exchange_value;
 * }
 * <p>
 * cmpxchg是C++的一个内联函数，在其内部主要执行相关的汇编指令cmpxchgl，对汇编指令感兴趣的读者可以参阅Intel的CPU指令手册，其中就有对该指令的详细说明。
 * <p>
 * 2.自旋方法addAndGet源码分析
 * 由于compareAndersonSwapInt方法的乐观锁特性，会存在对value修改失败的情况，但是有些时候对value的更新必须要成功，比如调用incrementAndGet、addAndGet等方法，
 * 本节就来分析一下addAndGet方法的实现。
 * public final int addAndGet(int delta) {
 * //调用Unsafe的getAndAddInt方法
 * return unsafe.getAndAddInt(this, valueOffset, delta) + delta;
 * }
 * //Unsafe类中的getAndAddInt方法
 * public final int getAndAddInt(Object object, long valueOffset, int delta) {
 * int currentValue;
 * do {
 * //①
 * currentValue = this.getIntVolatile(object, valueOffset);
 * ②//
 * } while(!this.compareAndSwapInt(object, valueOffset, currentValue, currentValue + delta));
 * }
 * <p>
 * 1.在getAndAddInt方法中有一个直到型do..while循环控制语句，首先在注释①处获取当前被volatile关键字修饰的value值（通过内存偏移量的方式读取内存）。
 * 2.在注释②处执行compareAndSwapInt方法，如果执行成功则直接返回，如果执行是被则再次执行下一轮的compareAndSwapInt方法。
 * <p>
 * 通过上面源码的分析，incrementAndGet的执行结果有可能是11也可能是比11更多的值。
 * AtomicInteger ai = new AtomicInteger(10);
 * assert ai.incrementAndGet() == 11;
 * <p>
 * 自旋方法addAndGet的执行步骤如图2-1所示。
 * <p>
 * 2.1.4 AtomicInteger总结
 * 本节学习了AtomicInteger的使用方法，并且为大家揭露了AtomicInteger的内部实现原理，本节中所涉及的断言代码assertion是基于JDK的断言语句的，
 * 要想使断言语句生效，需要在JVM参数中增加-ea (enable assertion)参数。
 * <p>
 * 本节对于AtomicInteger的讲解非常细致甚至有些啰嗦，其主要目的是想让读者对原子类型的原理有一个比较深入的理解。
 * 另外，所有原子类型其内部都依赖于Unsafe类，2.8节将为大家介绍如何获取Unsafe实例，如何进行Java与C++的混合编程，以及如何使用Unsafe实现一些不可思议的功能。
 */
public class AtomicIntegerSourceCodeAnalysis {

    public static void main(String[] args) {
        int a = 10;
        assert a == 10;

    }

}