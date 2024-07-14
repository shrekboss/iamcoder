package org.coder.concurrency.programming.juc._2_atomic;

import org.coder.concurrency.programming.juc._2_atomic.internal.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 2.7 AtomicFieldUpdater 详解
 * 截至目前我们已经知道，要想使得共享数据的操作具备原子性，目前有两种方案，第一，使用关键字synchronized进行加锁；第二，将对应的共享数据定义成原子类型，
 * 比如将Int定义成AtomicInteger，其他数据类型则没有与之直接对应的原子类型，我们可以借助于AtomicReference进行封装。前者提供了互斥的机制来保证
 * 在同一时刻只能有一个线程对共享数据进行操作，所以说它是一种悲观的同步方式；后者采用CAS算法提供的Lock Free方式，允许多个线程同时进行共享数据的操作，
 * 相比较synchronized关键字，原子类型提供了乐观的同步解决方案。
 * 
 * 但是如果你既不想使用synchronized 对共享数据的操作进行同步，又不想将数据类型声明成原子类型，那么这个时候应该如何进行操作呢?
 * 不用担心，在Java的原子包中提供了原子性操作进行同步，也无须将对应的数据类型声明成原子类型，在本节中，我们就来认识一下这种解决方案。
 * 
 * 2.7.1 原子性更新对象属性
 * 在Java的原子包中提供了三种原子性更新对象属性的类，分别如下所示。
 * 1.AtomicIntegerFieldUpdater:原子性地更新对象的int类型属性，该属性无须被声明成AtomicInteger。
 * 2.AtomicLongFieldUpdater：原子性地更新对象的long类型属性，该属性无须被声明成AtomicLong。
 * 3.AtomicReferenceFieldUpdater：原子性地更新对象的引用类型属性，该属性无须被声明成AtomicReference<T>。
 * 
 * 下面将通过示例的方式来演示使用原子性更新对象属性的操作，就以AtomicIntegerFieldUpdater为例。
 * 1.在注释①处，我们定义了AtomicIntegerFieldUpdater，在构造时传入class对象和需要原子更新的属性名。
 * 2.在注释②处，我们正常创建Alex对象实例。
 * 3.在注释③处，我们就可以使用原子性方法操作Alex对象的salary属性了。
 * 
 * 在AtomicIntegerFieldUpdater通过静态方法newUpdater成功创建之后，就可以使用AtomicIntegerFieldUpdater的实例来实现对应class属性的原子性操作了，就像我们直接使用原子类型一样。
 * 
 * 2.7.2 注意事项
 * AtomicFieldUpdater在使用上非常简单，其内部实现原理也是很容易理解的，但是并不是所有的成员属性都适合被原子性地更新，本节将通过单元测试的方式来演示一下。
 * (1)未被volatile关键字修饰的成员属性无法被原子性地更新。
 * (2)类变量无法被原子性地更新
 * (3)无法直接访问的成员属性不支持原子性地更新
 * (4)final修饰的成员属性无法被原子性地更新
 * (5)父类的成员属性无法被原子性地更新
 * 
 * 2.7.3 AtomicFieldUpdater总结
 * 本节学习了如何在不使用原子类型声明的情况下，使得某个对象的成员属性可以被原子性地操作，并且为大家介绍了在使用AtomicFieldUpdater时需要注意的地方。
 * 
 * 一般在什么情况下，我们才会使用这样的方式为成员属性提供原子性的操作呢？比如，使用的第三方类库某个属性不是被原子性修饰的，在多线程的环境中若不想通过加锁的方式则可以采用这种方式
 * (当然这对第三方类库的成员属性要求是比较苛刻的，最起码得满足可被原子性更新的所有条件)，另外，AtomicFieldUpdater的方式相比较直接使用原子类型更加节省应用程序的内存。
 *
 */
public class AtomicIntegerFieldUpdaterTest {
	//定义一个简单的类
	static class Alex {
		//int类型的salary，并不具备原子性的操作
		volatile int salary;
		public int getSalary() {
			return this.salary;
		}
	}
	
	public static void main(String[] args) {
		//① 定义AtomicIntegerFieldUpdater，通过newUpdater方法创建
		AtomicIntegerFieldUpdater<Alex> updater = AtomicIntegerFieldUpdater.newUpdater(Alex.class, "salary");
		//② 实例化Alex
		Alex alex = new Alex();
		//③ 原子性操作Alex类中的salary属性
		int result = updater.addAndGet(alex, 1);
		assert result == 1;
	}
	/**
	 * 通过运行这个单元测试，我们会发现要使成员属性可被原子性地更新，必须对该属性进行volatile关键字的修饰，否则将会抛出IllegalArgumentException异常。
	 */
	@Test(expected = IllegalArgumentException.class)
	public void test1() {
		AtomicIntegerFieldUpdater<Alex1> updater = AtomicIntegerFieldUpdater.newUpdater(Alex1.class, "salary");
		Alex1 alex1 = new Alex1();
		updater.addAndGet(alex1, 10);
		Assert.fail("should not process to here");
	}
	/**
	 * 虽然salary是受volatile关键字修饰的，但是该变量不是对象的成员属性，而是类变量，也就是被static修饰的变量，因此该变量也是无法支持被原子性更新的。
	 */
	@Test(expected = IllegalArgumentException.class)
	public void test2() {
		AtomicIntegerFieldUpdater<Alex2> updater = AtomicIntegerFieldUpdater.newUpdater(Alex2.class, "salary");
		Alex2 alex2 = new Alex2();
		updater.addAndGet(alex2, 10);
		Assert.fail("should not process to here.");
	}
	/**
	 * Class Alex所属的包为 org.coder.concurrency.programming.juc._2_atomic.internal.Alex3，
	 * 而是单元测试所属的包为 org.coder.concurrency.programming.juc._2_atomic，也就是说在单元测试中是无法直接访问Alex类中的salary属性的，
	 * 因此其不支持原子性更新。
	 */
	@Test(expected = RuntimeException.class)
	public void test3() {
		AtomicIntegerFieldUpdater<Alex3> updater = AtomicIntegerFieldUpdater.newUpdater(Alex3.class, "salary");
		Alex3 alex3 = new Alex3();
		updater.addAndGet(alex3, 10);
		Assert.fail("should not process to here.");
	}
	/**
	 * 这一点很容易理解，因为final修饰的是成员常量，不存在被更新这么一说，何况final修饰的属性也无法被volatile关键字修饰。
	 */
	@Test(expected = IllegalArgumentException.class)
	public void test4() {
		AtomicIntegerFieldUpdater<Alex4> updater = AtomicIntegerFieldUpdater.newUpdater(Alex4.class, "salary");
		Alex4 alex4 = new Alex4();
		updater.addAndGet(alex4, 10);
		Assert.fail("should not process to here.");
	}
	/**
	 * 虽然Alex继承自Parent，并且可以通过Alex的实例正常访问Parent的age属性，age属性同时又符合可被原子化更新的所有条件，
	 * 但是AtomicIntegerFieldUpdater<Alex>是不允许用来操作父类的成员属性的。
	 */
	@Test(expected = IllegalArgumentException.class)
	public void test5() {
		AtomicIntegerFieldUpdater<Alex5> updater = AtomicIntegerFieldUpdater.newUpdater(Alex5.class, "age");
		Alex5 alex5 = new Alex5();
		updater.addAndGet(alex5, 10);
		Assert.fail("should not process to here.");
	}
}