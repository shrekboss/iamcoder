package org.coder.concurrency.programming.juc._2_atomic;

import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

/**
 * 3.类的加载
 * 借助于Unsafe还可以实现对类的加载，下面我们先来看看一个比较简单的类，然后将其编译生成class字节码文件。
 * 
 * 使用Unsafe的defineClass方法完成对类的加载，代码如下。
 * 
 * 2.8.4 sun.misc.Unsafe 总结
 * 本节学习了如何获取Unsafe对象实例，并且通过一个简单的JNI编程详细描述了Java调用C/C++程序的全过程，以便让大家清晰地了解Unsafe是如何工作的，
 * 最后借助于Unsafe的其他方法完成了危险的操作，如果你对Linux C/C++编程非常熟悉，那么不妨打开JVM源码阅读一下Unsafe的源码，不仅对提高Java
 * 有帮助，对提高C/C++的水平也是大有裨益。
 * 
 * 2.9 本章总结
 * 本章非常详细地讲解了Java原子类型包中的所有原子类型的原理以及用法，原子类型包为我们提供了一种无锁的 原子性操作共享数据 的方式，
 * 无锁的操作方式可以减少线程的阻塞，减少CPU上下文的切换，提高程序的运行效率，但是这并不是一条放之四海皆准的规律，比如，
 * 同样被synchronized关键字同步的共享数据和原子类型的数据在单线程运行的情况下，synchronized关键字的效率却要高很多，
 * 究其原因是synchronized关键字是由jVM提供的相关指令所保证的，因此在Java程序运行期优化时可以将同步擦除，
 * 而原子类是由本地方法和汇编指令来提供保障的，在Java程序运行期间是没有办法被优化的。
 * 
 * 本章的最后顺便为大家解密了Java和C/C++程序的混合编程，即在Java程序中如何调用C/C++程序，Java程序员没有必要掌握C/C++程序如何开发，
 * 也不用在日常的开发中使用这种混合编程的方式，但是了解Java本地方法接口(Java Native Interface)的原理对于进一步了解Unsafe也是有一定的好处的。
 */
public class UnsafeExample3 {

	private static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (Exception e) {
			throw new RuntimeException("can't initial the unsafe instance.", e);
		} 
	}
	
	public static void main(String[] args) throws Exception {
		byte[] classContents = getClassContent();
		//调用defineClass方法完成对A的加载
		Class c = getUnsafe().defineClass(null, classContents, 0, classContents.length, null, null);
		Object result = c.getMethod("getI").invoke(c.newInstance(), null);
		assert (Integer) result == 10;
	}
	//读取class文件的二进制数组
	private static byte[] getClassContent() throws Exception {
		File f = new File("/Users/crayzer/workspaces/iamcoder/concurrency-in-action/target/classes/org/coder/concurrency/programming/juc/_2_atomic/A.class");
		try(FileInputStream input = new FileInputStream(f)){
			byte[] content = new byte[(int)f.length()];
			input.read(content);
			return content;
		}
	}

}