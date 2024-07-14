package org.coder.concurrency.programming.juc._2_atomic;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 2.3 AtomicLong 详解
 * 与AtomicInteger非常类似，AtomicLong提供了原子性操作long类型数据的解决方案，AtomicLong同样也继承自Number类，
 * AtomicLong所提供的原子性方法在使用习惯上也与AtomicInteger非常一致。为了节约篇幅，本节将不会详细解释每一个方法如何使用，
 * 也不会给出代码示例，读者可以根据2.1节中的代码示例方式去实操AtomicLong的具体用法。
 *
 * AtomicInteger类中最为关键的方法为compareAndSwapInt，对于该方法，2.1.3节的第1小节中已经进行了非常详细的分析，
 * 同样，在AtomicLong类中也提供了类似的方法compareAndSwapLong，但是该方法要比compareAndSwapInt复杂很多。
 *
 * //AtomicLong.java中的compareAndSet方法
 * public final boolean compareAndSet(long expect, long update) {
 * 		return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
 * }
 * //对应于Unsafe.java中的compareAndSwapLong方法
 * public final native boolean compareAndSwapLong(Object var1, long var2, long var4, long var6);
 *
 * 打开openjdk的unsafe.cpp文件，具体路径为openjdk-jdk8u/hotspot/src/share/vm/prims/unsafe.cpp。
 *
 * unsafe_entry(jboolean, Unsafe_CompareAndSwapLong(JNIEnv *env, jobject unsafe, jobect obj, jlong offset, jlong e, jlong x))
 * 		UnsafeWrapper("Unsafe_CompareAndSwapLong");
 * 		Handle p (THREAD, JNIHandles::resolve(obj));
 * 		jlong* addr = (jlong*) (index_oop_from_field_offset_long(p(), offset));
 * 		#ifdef SUPPORTS_NATIVE_CX8
 * 			return (jlong)(Atomic::cmpxchg(x, addr, e)) == e;
 * 		#else
 * 			if (vm_version::supports_cx8())
 * 				return (jlong)(Atomic::cmpxchg(x, addr, e)) == e;
 * 			else {
 * 				jboolean success = false;
 * 				MutexLockerEx mu(UnsafeJlong_lock, Mutex::_no_safepoint_check_flag);
 * 				jlong val = Atomic::load(addr);
 * 				if (val == e) { Atomic::store(x, addr); success = true; }
 * 				return success;
 * 			}
 * 		#endif
 * UNSAFE_END
 *
 * 相对于compareAndSwapInt方法，在unsafe.cpp中，compareAndSwapLong方法多了条件编译SUPPORTS_NATIVE_CX8。
 * SUPPORTS_NATIVE_CX8主要用于判断机器硬件是否支持8字节数字的cmpxchg CPU指令，如果机器硬件不支持，
 * 比如32位的CPU肯定不支持8字节64位数字的cmpxchg CPU指令，那么此时就需要判断当前JVM版本是否支持8字节数字的cmpxchg操作；
 * 如果机器硬件与当前JVM的版本都不支持，那么实际上针对long型数据的原子性操作将不会是Lock Free的，而是需要采用加锁的方式确保原子性。
 *
 * openjdk-jdk8u/hotspot/src/os_cpu/bsd_x86/vm/atomic_bsd_x86.inline.hpp中提供了cmpxchg的重载方法，同样也是使用汇编语言实现的CPU操作。
 *
 * inline jlong Atomic::cmpxchg(jlong exchange_value, volatile jlong* dest, jlong compare_value) {
 * 		bool mp = os::is_MP();
 * 		__asm__ __volatile__ (LOCK_IF_MP(%4) "cmpxchgq %1, (%3)"
 * 								: "=a" (exchange_value)
 * 								: "r" (exchange_value), "a" (compare_value), "r" (dest), "r" (mp)
 * 								: "cc", "memory");
 * 		return exchange_value;
 * }
 *
 * 我们再回过头来看看AtomicLong的部分源码，不难发现VM_SUPPORT_LONG_CAS在AtomicLong中定义，其作用与SUPPORT_NATIVE_CX8及VM_Version::supports_cx8()是一致的。
 * (条件编译，在编译JDK版本的时候就已经可以根据不同的硬件环境以及操作系统进行不同JDK版本的编译，因此在JDK的编译阶段就已经知道当前的JDK版本是否支持AtomicLong Lock Free的CAS操作了。)
 * static　final　boolean　VM_supports_long_cas = VMSupportsCS8();
 * private static native boolean VMSupportsCS8();
 * 通过下面的代码，我们将会看到自己机器上安装的JDK是否支持8字节数字(长整型)的Lock Free CAS操作。
 * public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
 * 		Field vm_supports_long_cas = AtomicLong.class.getDeclaredField("VM_SUPPORTS_LONG_CAS");
 * 		vm_supports_long_cas.setAccessible(true);
 * 		boolean isSupport = (boolean) vm_supports_long_cas.get(null);
 * 		System.out.println(isSupport);
 * }
 *
 * 如果你的机器和JDK版本不支持8字节数字的Lock Free CAS操作，那么对它的原子性保证将由synchronized关键字来承担，
 * 比如，我们在2.7节中将要学到的AtomicLongFieldUpdater类中会首先判断AtomicLong是否支持8字节数字的CAS操作。
 * ...省略
 * public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> tclass, String fieldName) {
 * 		Class<?> caller = Reflection.getCallerClass();
 * 		if (AtomicLong.VM_SUPPORT_LONG_CAS)
 * 			return new CASUpdater<U>(tclass, fieldName, caller);
 * 		else
 * 			return new LockedUpdater<U>(tclass, fieldName, caller);
 * }
 * ...省略
 * //下面是LockedUpdater的实现代码片段
 * public boolean compareAndSet(T obj, long expect, long updater) {
 * 		if (obj == null || obj.getClass() != tclass || cclass != null) fullCheck(obj);
 * 		//synchronized关键字的使用
 * 		synchronized(this) {
 * 			long v = unsafe.getLong(obj, offset);
 * 			if (v != expect)
 * 				return false;
 * 			unsafe.putLong(obj, offset, update);
 * 			return true;
 * 		}
 * }
 * ...省略
 *
 */
public class AtomicLongReferenceSourceCodeAnalysis {

    public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field vm_supports_long_cas = AtomicLong.class.getDeclaredField("VM_SUPPORTS_LONG_CAS");
        vm_supports_long_cas.setAccessible(true);
        boolean isSupport = (boolean) vm_supports_long_cas.get(null);
        System.out.println(isSupport);
    }
}