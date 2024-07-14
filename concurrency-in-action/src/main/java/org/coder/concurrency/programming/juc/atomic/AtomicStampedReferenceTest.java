package org.coder.concurrency.programming.juc.atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 2.5 AtomicStampedReference 详解
 * 截至目前我们已经学习了AtomicInteger、AtomicBoolean、AtomicLong、AtomicReference这些原子类型，
 * 它们无一例外都采用了基于volatile关键字+CAS算法无锁的操作方式来确保共享数据在多线程操作下的线程安全性。
 * 1.volatile关键字保证了线程间的可见性，当某线程操作了被volatile关键字修饰的变量，其他线程可以立即看到该共享变量的变化。
 * 2.CAS算法，即对比交换算法，是由UNSAFE提供的，实质上是通过操作CPU指令来得到保证的。CAS算法提供了一种快速失败的方式，当某线程修改已经被改变的数据时会快速失败。
 * 3.当CAS算法对共享数据操作失败时，因为有自旋算法的加持，我们对共享数据的更新终究会得到计算。
 * 总之，原子类型用自旋+CAS的无锁操作保证了共享变量的线程安全性和原子性。
 * 绝大多数情况下，CAS算法并没有什么问题，但是在需要关心变化值的操作中会存在ABA的问题，比如一个值原来是A，变成了B，后来又变成了A，那么CAS检查时会发现它的值没有发生变化，但是实际上却是发生了变化的。
 * 
 * 2.5.1 CAS算法ABA问题
 * 上文提到了CAS算法在需要关注变化的操作中将会存在ABA的问题，本节就将通过图示的方式详细地解释一下。
 * 假设此时我们的LinkedStack有两个元素，经过了push B 和 push A 的操作之后，栈的数据元素如图2-2所示。	A <- B
 * 假设此时线程T1想要将栈顶元素A弹出，实际上就是用A.next(B)替换top，在线程T1即将用A.next(B)替换A时，线程T2进入了执行，线程T2对A、B元素分别进行了弹出操作，
 * 然后又执行了D、C、A元素的push操作，线程T2执行成功之后的LinkedStack元素图2-3所示。	A <- C <-D
 * 
 * B元素去哪里了呢?很明显，B元素此时已经变成了游离状态，但是栈顶元素仍然还是A，此时线程T1成功执行了将A元素替换为A.next(B)元素的操作，因此LinkedStack中的元素如图2-4所示。
 * 此时栈顶元素成为了B，但是B.next=null，也就是说，C元素不再被栈顶元素引用，C元素和D元素就这样无辜地被去掉了。
 * 这就是所谓的ABA问题，也是在CAS操作中ABA问题带来的潜在危害。
 * 
 * 2.5.2 AtomicStampedReference 详解
 * 如何避免CAS算法带来的ABA问题呢?针对乐观锁在并发情况下的操作，我们通常会增加版本号，比如数据库中关于乐观锁的实现方式，以此来解决并发操作带来的ABA问题。
 * 在Java原子包中也提供了这样的实现AtomicStampedReference<E>。
 * 
 * AtomicStampedReference在构建的时候需要一个类似于版本号的int类型变量stamped，每一次针对共享数据的变化都会导致该stamped的增加
 * (stamped的自增维护需要应用程序自身去负责，AtomicStampedReference并不提供)，因此就可以避免ABA问题的出现，
 * AtomicStampedReference的使用也是极其简单的，创建时我们不仅需要指定初始值，还需要设定stamped的初始值，
 * 在AtomicStampedReference的内部会将这两个变量封装成Pair对象，代码如下。
 * private static class Pair<T> {
 * 		final T reference;
 * 		final int stamp;
 * 		private Pair(T reference, int stamp) {
 * 			this.reference = reference;
 * 			this.stamp = stamp;
 * 		}
 * 		static <T> Pair<T> of(T reference, int stamp) {
 * 			return new Pair<T>(reference, stamp);
 * 		}
 * 	}
 * 
 * 	private volatile Pair<V> pair;
 * 	public AtomicStampedReference(V initialRef, int initialStamp) {
 * 		pair = Pair.of(initialRef, initialStamp);
 * 	}
 * 
 * 1.AtomicStampedReference构造函数：在创建AtomicStampedReference时除了指定引用值的初始值之外还要给定初始的stamp。
 * 2.getReference():获取当前引用值，等同于其他原子类型的get方法。
 * 3.getStamp():获取当前引用值的stamp数值。
 * 4.V get(int[] stampHolder):这个方法的意图是获取当前值以及stamp值，但是Java不支持多值的返回，并且在AtomicStampedReference内部Pair被定义为私有的，
 * 	因此这里就采用了传参的方式来解决(个人觉得这样的方法设计不算优雅，作者如果不想暴露Pair，完全可以再定义一个专门用于返回value和stamp对的public对象)。
 * 5.compareAndSet(V expectedReference, V newReference, int expectedStamp, int newStamp):对比并且设置当前的引用值，
 * 	这与其他的原子类型CAS算法类似，只不过多了expectStamp和newStamp，只有当expectedReference与当前的Reference相等，
 * 	且expectedStamp与当前引用值的stamp相等时才会发生设置，否则set动作将会直接失败。
 * 6.weakCompareAndSet(V expectedReference, V newReference, int expectedStamp, int newStamp):同上。
 * 7.set(V newReference, int newStamp):设置新的引用值以及stamp。
 * 8.attemptStamp(V expectedReference, int newStamp):该方法的主要作用是为当前的引用值设置一个新的stamp，该方法为原子性方法。
 * 
 * 2.5.3 AtomicStampedReference 总结
 * 本节学习了AtomicStampedReference，该类的使用比较简单，其源码也是非常容易理解的，读者可以自行阅读。
 * AtomicStampedReference的出现是为了解决CAS算法中的ABA问题，它通过为引用值增加一个stamp戳的方式来避免ABA问题的发生，
 * 熟悉数据库开发的朋友肯定知道在多线程或者多系统中，同时对数据库的某条记录进行更改的时候，我们一般是采用乐观锁的方式，
 * 即为该记录增加版本号字段，比如如下的更新操作，其实AtomicStampedReference的实现原理也是这样的。
 * UPDATE TABLE TAB SET X = newValue, VERSION = version + 1 WHERE X = oldValue AND VERSION = expectedVersion
 */
public class AtomicStampedReferenceTest {
	
	@Test
	public void atomicStampedReference() {
		AtomicStampedReference<String> reference = new AtomicStampedReference<>("Hello", 1);
	}
	
	@Test
	public void getReference() {
		AtomicStampedReference<String> reference = new AtomicStampedReference<>("Hello", 1);
		assert reference.getReference().equals("Hello");
	}
	
	@Test
	public void getStamp() {
		AtomicStampedReference<String> reference = new AtomicStampedReference<>("Hello", 1);
		assert reference.getStamp() == 1;
	}
	
	@Test
	public void get() {
		AtomicStampedReference<String> reference = new AtomicStampedReference<>("Hello", 1);
		int[] holder = new int[1];
		String value = reference.get(holder);
		assert value.equals("Hello");
		assert holder[0] == 1;
	}
	
	@Test
	public void compareAndSet() {
		AtomicStampedReference<String> reference = new AtomicStampedReference<>("Hello", 1);
		assert !reference.compareAndSet("Hello", "World", 2, 3);
		assert reference.compareAndSet("Hello", "World", 1, 2);
		assert reference.getReference().equals("World");
	}
	
	@Test
	public void set() {
		AtomicStampedReference<String> reference = new AtomicStampedReference<>("Hello", 1);
		reference.set("World", reference.getStamp() + 1);
		assert reference.getReference().equals("World");
	}
	
	@Test
	public void attemptStamp() {
		AtomicStampedReference<String> reference = new AtomicStampedReference<>("Hello", 1);
		assert reference.attemptStamp("Hello", 1);
		assert !reference.attemptStamp("World", 2);
		assert reference.getStamp() == 1;
		assert reference.attemptStamp("Hello", 2);
		assert reference.getStamp() == 2;
	}
}