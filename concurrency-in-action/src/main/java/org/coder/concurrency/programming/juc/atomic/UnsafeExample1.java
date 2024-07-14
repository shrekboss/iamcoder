package org.coder.concurrency.programming.juc.atomic;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 2.8.3 危险的Unsafe
 * Unsafe非常强大，它可以帮助我们获得某个变量的内存偏移量，获取内存地址，在其内部更是运行了汇编指令，为我们在高并发编程中提供Lock Free的解决方案，提高了并发程序的执行效率。
 * 但是Unsafe正如它的名字一样是很不安全的，如果使用错误则会出现很多灾难性的问题(本地代码所属的内存并不在JVM的堆栈中)，本节就来看一下借助于Unsafe可以实现哪些功能呢？
 * 
 * 1.绕过类构造器函数完成对象创建
 * 我们都知道，主动使用某个类会引起类的加载过程发生直到该类完成初始化，最典型的例子是当我们通过关键字new进行对象的创建时，对应的构造函数肯定会被执行，这是毫无疑问的，
 * 但是Unsafe可以绕过构造函数完成对象的创建，我们来看下面的例子。
 * (1)注释①和②处，我们分别使用new关键字以及反射获得了Example对象的实例，这会触发无参构造函数的执行，x的值将会被赋予10，因此断言肯定能够顺利通过。
 * (2)在注释③处，我们借助于Unsafe的allocateInstance方法获得了Example的实例，该操作并不会导致Example构造函数的执行，因此x将不会被赋予10。
 */
public class UnsafeExample1 {

	private static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (Exception e) {
			throw new RuntimeException("can't initial the unsafe instance.", e);
		} 
	}
	public static void main(String[] args) throws InstantiationException {
//		Unsafe unsafe = Unsafe.getUnsafe();
//		Exception in thread "main" java.lang.SecurityException: Unsafe
//		at sun.misc.Unsafe.getUnsafe(Unsafe.java:90)
//		at org.coder.concurrency.programming.juc.atomic.UnsafeTest.main(UnsafeTest.java:7)

//		Unsafe unsafe = getUnsafe();
		
		//① new 关键字
		Example example1 = new Example();
		assert example1.getX() == 10;
		//② 反射
		Example example2 = new Example();
		assert example2.getX() == 10;
		//③ 使用Unsafe
		Example example3 = (Example) getUnsafe().allocateInstance(Example.class);
		assert example3.getX() == 0;
	}

	static class Example {
		private int x;
		
		public Example() {
			System.out.println("---------------");
			this.x = 10;
		}
		
		private int getX() {
			return x;
		}
	}
}