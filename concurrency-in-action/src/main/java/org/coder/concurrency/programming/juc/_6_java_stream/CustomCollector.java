package org.coder.concurrency.programming.juc._6_java_stream;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * 6.2.3 自定义Collector
 * 6.2.2节学习了Collectors提供的所有工厂方法，Collectors提供的静态方法几乎可以满足我们对Collector的所有需要，
 * 如果你觉得有些Collector无法满足你的需求，那么完全可以自行扩展，本节将学习如何自定义Collector。
 * 
 * 当然，实现一个自定义的Collector，第一件事情就是实现Collector接口，我们决定将Stream中元素T，通过collect聚合操作最终返回到list中，
 * 这一点与Collectors的toList工厂方法创建出来的Collector类似。
 * 
 * 根据6.2.2节对Collector接口方法的介绍，我们需要实现5个接口方法才能完成对Collector的实现。
 * 
 * 6.2.4 Collector总结
 * 本节非常详细地讲解了Collector接口，以及Collector接口的每一个接口方法。使用Stream的collect操作必须清晰地掌握Collector接口方法的使用，
 * 另外，我们还逐一学习了Collectors提供的所有静态方法，这些静态方法为开发者提供了非常多的、现成的Collector实现，我们可以在日常的开发中直接使用。
 * 本节的最后通过练习自定义Collector的实现加深了对Collector接口的理解。
 * 
 * 需要特别注意的是，Collector的特征值如果未指定可并行的特征，那么这将导致不仅在并行流中不会发挥并行计算所带来的好处，反而还会增加由于创建和销毁ForkJoinPool所带来的性能开销。
 * 
 * Collector会被并行执行的条件相对来说还算比较苛刻，下面来看一下Java中的源码，具体如下。
 * ...省略
 *	//必须是并行流
 *	if(isParallel()
 *		//Collector的特征值必须包含concurrent
 *		&& (collector.characteristics().contains(Collector.Characteristics.CONCURRENT))
 *		//该Stream未被进行排序操作，或者特征值中包含unordered，注意这里采用了“短路或”的逻辑操作
 *		&& (!isOrdered() || collector.characteristics().contains(Collector.Characteristics.UNORDERED))
 *	) {
 *		container = collector.supplier().get();
 *		BiConsumer<A, ? super P_OUT> accumulator = collector.accumulator();
 *		forEach(u -> accumulator.accept(container, u));
 *	}
 * ...省略
 */
public class CustomCollector<T> implements Collector<T, List<T>, List<T>> {
	/**
	 * 1).重写supplier()，用于在累加器方法中进行计算。
	 */
	@Override
	public Supplier<List<T>> supplier() {
		//返回Supplier<List<T>>
		return ArrayList::new;
	}

	/**
	 * 2).重写accumulator()累加器方法，用于对Stream中的每一个元素进行计算操作
	 */
	@Override
	public BiConsumer<List<T>, T> accumulator() {
		//Stream中的每一个元素都会被加入supplier()创建的容器之中
		return List::add;
	}

	/**
	 * 3).重写combiner()合并方法，用于将不同子任务的结果整合在一起。
	 */
	@Override
	public BinaryOperator<List<T>> combiner() {
		return (lList, rList) -> {
			//整合
			lList.addAll(rList);
			return lList;
		};
	}

	/**
	 * 4).重写finisher()方法，用于返回Collector的最终结果。
	 */
	@Override
	public Function<List<T>, List<T>> finisher() {
		return Function.identity();
	}

	/**
	 * 5).重写Collector的特征值characteristics()方法。
	 * 根据特征值的描述，我们定义的Collector在并行流中将会以多线程的方式运行（Fork Join）。
	 */
	@Override
	public Set<Characteristics> characteristics() {
		return EnumSet.of(Characteristics.UNORDERED, Characteristics.CONCURRENT, Characteristics.IDENTITY_FINISH);
	}

	/**
	 * 测试自定义的Collector。
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5, 6, 7);
		List<Integer> result = stream.collect(new CustomCollector<>());
		System.out.println(result);
	}

	/**
	 * 在Collectors的源码中，构建Collector的方式大多数都是通过构造CollectorImpl的方式来实现的，但是该类是一个静态内部类且是包可见的，
	 * 因此我们的应用程序无法对其 直接使用，但是我们可以通过Collector接口的静态方法实现 自定义Collector，代码如下所示。
	 */
	private static <T> Collector<T, List<T>, List<T>> custom() {
		return Collector.of(ArrayList::new, List::add, (lList, rList) -> {
					lList.addAll(rList);
					return lList;
				},
				Function.identity(),
				Characteristics.UNORDERED,
				Characteristics.CONCURRENT,
				Characteristics.IDENTITY_FINISH);
	}
}