package org.coder.concurrency.programming.juc._2_atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 2.4.2 AtomicReference的基本用法
 * 掌握了AtomicReference的使用场景之后，本节讲详细介绍AtomicReference的其他方法。
 * 1.AtomicReference的构造：AtmoicReference是一个泛型类，它的构造与其他原子类型的构造一样，也提供了无参和一个有参的构造函数。
 * 2.AtomicReference():当使用无参构造函数创建AtomicReference对象的时候，需要再次调用set()方法为AtomicReference内部的value指定初始值。
 * 3.AtomicReference(V initialValue):创建AtomicReference对象时顺便指定初始值。
 * 4.compareAndSet(V expect, V update):原子性地更新AtomicReference内部的value值，其中expect代表当前AtomicReference的value值，
 * 	update则是需要设置的新引用值。该方法会返回一个boolean的结果，当expect和AtomicReference的当前值不相等时，修改会失败，返回值为false，若修改成功则会返回true。
 * 5.getAndSet(V newValue):原子性地更新AtomicReference内部的value值，并且返回AtomicReference的旧值。
 * 6.getAndUpdate(UnaryOperator<V> updateFunction):原子性地更新value值，并且返回AtomicReference的旧值，该方法需要传入一个Function接口。
 * 7.updateAndGet(UnaryOperator<V> updateFunction):原子性地更新value值，并且返回AtomicReference更新后的新值，该方法需要传入一个Function接口。
 * 8.getAndAccumulate(V x, BinaryOperator<V> accumulatorFunction):原子性地更新value值，并且返回AtomicReference更新前的旧值。该方法需要传入两个参数，
 * 	第一个是更新后的新值，第二个是BinaryOperator接口。
 * 98.accumulteAndGet(V x, BinaryOperator<V> accumulatorFunction):原子性地更新value值，并且返回AtomicReference更新后的新值。该方法需要传入两个参数，
 * 	第一个是更新后的新值，第二个是BinaryOperator接口。
 * 10.get()：获取AtomicReference的当前对象引用值。
 * 11.set(V newValue):设置AtomicReference最新的对象引用值，该新值的更新对其他线程立即可见。
 * 12.lazySet(V value):设置AtomicReference的对象引用值。lazySet方法的原理已经在AtomicInteger中介绍过了，这里不再赘述。
 */
public class AtomicReferenceTest {

	@Test
	public void AtomicReference() {
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(new DebitCard("Alex", 0));
		DebitCard debitCard = debitCardRef.get();
		System.out.println(debitCard);
	}
	
	@Test
	public void compareAndSet() {
		DebitCard oldDebitCard = new DebitCard("Alex", 0);
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(oldDebitCard);
		DebitCard newDebitCard = new DebitCard("Alex", 10);
		assert debitCardRef.compareAndSet(oldDebitCard, newDebitCard);
	}
	
	@Test
	public void getAndSet() {
		DebitCard oldDebitCard = new DebitCard("Alex", 0);
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(oldDebitCard);
		DebitCard newDebitCard = new DebitCard("Alex", 10);
		assert oldDebitCard == debitCardRef.getAndSet(newDebitCard);
	}
	
	@Test
	public void getAndUpdate() {
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(new DebitCard("Alex", 0));
		DebitCard preDC = debitCardRef.get();
		DebitCard result = debitCardRef.getAndUpdate(dc -> new DebitCard(dc.getAccount(), dc.getAmount() + 10));
		assert preDC == result;
		assert result != debitCardRef.get();
	}
	
	@Test
	public void updateAndGet() {
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(new DebitCard("Alex", 0));
		DebitCard newDC = debitCardRef.updateAndGet(dc -> new DebitCard(dc.getAccount(), dc.getAmount() + 10));
		assert newDC == debitCardRef.get();
		assert newDC.getAmount() == 10;
	}
	
	@Test
	public void getAndAccumulate() {
		DebitCard initialVal = new DebitCard("Alex", 0);
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(initialVal);
		DebitCard newValue = new DebitCard("Alex2", 10);
		DebitCard result = debitCardRef.getAndAccumulate(newValue, (prev, newVal) -> newVal);
		assert initialVal == result;
		assert newValue == debitCardRef.get();
	}
	
	@Test
	public void accumulateAndGet() {
		DebitCard initialVal = new DebitCard("Alex", 0);
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>(initialVal);
		DebitCard newValue = new DebitCard("Alex2", 10);
		DebitCard result = debitCardRef.accumulateAndGet(newValue, (prev, newVal) -> newVal);
		assert newValue == result;
		assert newValue == debitCardRef.get();	
	}
	
	@Test
	public void set() {
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>();
		DebitCard initialVal = new DebitCard("Alex", 0);
		debitCardRef.set(initialVal);
		assert initialVal == debitCardRef.get();
	}
	
	@Test
	public void lazySet() {
		AtomicReference<DebitCard> debitCardRef = new AtomicReference<>();
		DebitCard initialVal = new DebitCard("Alex", 0);
		debitCardRef.lazySet(initialVal);
		assert initialVal == debitCardRef.get();
	}
}