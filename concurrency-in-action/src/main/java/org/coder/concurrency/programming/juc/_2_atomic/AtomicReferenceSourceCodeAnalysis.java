package org.coder.concurrency.programming.juc._2_atomic;

/**
 * 2.4 AtomicReference 详解
 * AtomicReference类提供了对象引用的非阻塞原子性读写操作，并且提供了其他一些高级的用法。
 * 众所周知，对象的引用其实是一个4字节的数字，代表着在JVM堆内存中的引用地址，对一个4个字节数字的读取操作和写入操作本身就是原子性的，
 * 通常情况下，我们对对象引用的操作一般都是获取该引用或者重新赋值(写入操作)，我们也没有办法对对象引用的4个字节数字进行加减乘除运算，
 * 那么为什么JDK还要提供AtomicReference类用于支持引用类型的原子性操作呢？
 * 
 * 本节将结合实例为大家解释AtomicReference的用途，在某些场合下该类可以完美地替代synchronized关键字和显示锁，实现在多线程的非阻塞操作。
 * 
 * 2.4.3 AtomicReference的内幕
 * 在AtomicReference类中，最关键的方法为compareAndSet()，下面来一探该方法的内幕。
 * //AtomicReference.java中的compareAndSet方法
 * public final boolean compareAndSet(V expect, V update) {
 * 		return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
 * }
 * //对应于Unsafe.java中的compareAndersonSwapObject方法
 * public final native boolean compareAndSwapObject(Object obj, long offset, Object exceptRef, Object newRef);
 * 
 * 打开openjdk的unsafe.cpp文件，具体路径为openjdk-jdk8u/hotspot/src/share/vm/prims/unsafe.cpp。
 * UNSAFE_ENTRY(jboolean, Unsafe_CompareAndSwapObject(JNIEnv *env, jobject unsafe, jobject obj, jlong offset, jobject e_h, jobject x_h))
 * 		UnsafeWrapper("Unsafe_CompareAndSwapObject");
 * 		oop x = JNIHandles::resolve(x_h);
 * 		oop e = JNIHandles::resolve(e_h);
 * 		oop p = JNIHandles::resolve(p_h);
 * 		HeapWord* addr = (HeapWord *)index_oop_from_field_offset_long(p, offset);
 * 		opp res = oopDesc::atomic_compare_exchange_oop(x, addr, e, true);
 * 		jboolean success = (res == e);
 * 		if (success)
 * 			update_barrier_set((void*)addr, x);
 * 		return success;
 * UNSAFE_END
 * 在unsafe.cpp中，我们找到了对应的Unsafe_CompareAndSwapObject方法，该方法调用了另外一个C++方法oopDesc::atomic_compare_exchange_oop。
 * 
 * 打开另外一个C++文件，我们会发现在内联函数中，当UseCompressedOops为true时将会调用执行与AtomicInteger一样的CAS函数Atomic::cmpxchg()，文件路径为
 * openjdk-jdk8u/hotspot/src/share/vm/oops/oop.inline.hpp。
 * inline oop oopDesc::atomic_compare_exchange_oop(oop exchange_value,
 * 													volatile HeapWord *dest,
 * 													oop compare_value,
 * 													bool prebarrier) {
 * 		if(UseCompressOops) {
 * 			if (prebarrier) {
 * 				update_barrier_set_pre((narrowOop*)dest, exchange_value);
 * 			}
 * 			//encode exchange and compare value from oop to T
 * 			narrowOop val = encode_heap_oop(exchange_value);
 * 			narrowOop cmp = encode_heap_oop(compare_value);
 * 
 * 			narrowOop old = (narrowOop) Atomic::cmpxchg(val, (narrowOop*)dest, cmp);
 * 			//decode old from T to oop
 * 			return decode_heap_oop(old);
 * 		}else {
 * 			if (prebarrier) {
 * 				update_barrier_set_pre((oop*)dest, exchange_value);
 * 			}
 * 			return (oop)Atomic::cmpxchg_ptr(exchange_value, (oop*)dest, compare_value);
 * 		}
 * }
 * UseCompressedOops参数是JVM用于控制指针压缩的参数，一般情况下，64位的JDK版本基本上都是默认打开的(对于32位JDK的版本，该参数无效)，
 * 大家可以根据jinfo-JPID查看你自己运行的JVM参数。关于Atomic::cmpxchg方法，AtomicInteger和AtomicLong中已经做过了介绍，这里不再赘述。
 * 
 * 2.4.4 AtomicReference总结
 * 虽然AtomicReference的使用非常简单，但是很多人依然很难理解它的使用场景，网上大量的文章也只是讲述API如何使用，容易让人疑惑它存在的价值。
 * 因此本书的一开始，我们便通过一个应用场景的演进为大家展示AtomicReference原子性操作对象引用(在并发的场景之下)所带来的性能提升，
 * 进而说明AtomicReference存在的价值和意义，紧接着我们又详细介绍了AtomicReference API方法的使用，并重点介绍了CAS算法的底层C++实现
 * (其实在64位JDK版本中使用的汇编指令与AtomicInteger是完全一致的)。
 */
public class AtomicReferenceSourceCodeAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}