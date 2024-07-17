package org.coder.concurrency.programming.juc._7_metrics;

import com.codahale.metrics.*;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 7.5 深入Metrics源码
 * 前文中曾简单提到过，Metrics之所以被大量应用在一些开源项目和平台中，除了它严谨科学的指标数据度量方法，最重要的还要得益于它优雅的软件设计哲学，
 * 正如我们见到过太多基于Junit衍生出来的很多TDD（测试驱动开发）、BDD（行为驱动开发）、DDT（数据驱动测试）工具一样，比如Mockito、
 * EasyMock、PowerMock、Cucumber、Jbehave、Concordion这些针对测试类的工具框架，都可以非常容易地与Junit结合在一起使用，
 * 究其原因就是因为Junit软件架构虽然小巧，但是可扩展性却极强。
 * 
 * 因此笔者个人极力推荐读者应该学习和研究一下Metrics的源码，当然其中有些数学算法的实现，如果不是，很熟悉统计学的知识，看起来就会感觉不是很容易理解，
 * 但是这些都不重要，我们主要了解它的实现原理，以及能将软件设计中比较好的地方为我们所用即可。
 * 
 * 本章将揭露Metrics的部分源码，为大家说清道明它的工作原理和细节（当然，由于篇幅的关系，不可能编写太多关于源码的内容），
 * 如果你已经对Metrics的原理理解透彻了，或者只想知道它如何使用而不想关心它的内部，那么跳过这部分内容也没有任何关系。
 * 
 * 7.5.1 MetricRegistry 如何工作
 * 首先，我们来看一下MetricRegistry，所谓MetricRegistry，从字面意思来看，就是一个存放Metric的注册表，事实上，它的作用也正是如此，
 * 在MetricRegistry的内部维护了一个ConcurrentMap。
 * 
 * 1.Metric在注册表中的存取
 * private final ConcurrentMap<String, Metric> metrics;
 * 
 * public MetricRegistry(){
 * 		this.metrics = buildMap();
 * 		...省略
 * }
 * protected ConcurrentMap<String, Metric> buildMap(){
 * 		//使用ConcurrentHashMap存放注册至注册表的Metric
 * 		return new ConcurrentHashMap<String, Metric>();
 * }
 * 
 * 当我们调用注册方法的时候，对应的Metric会被加入到注册表中，示例代码如下所示。
 * public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
 * 		if(metric instanceof MetricSet){
 *			// 如果Metric是MetricSet类型，则将调用registerAll方法
 * 			registerAll(name, (MetricSet) metric);
 * 		}else {
 * 			final Metric existing = metrics.putIfAbsent(name, metric);
 * 			if(existing == null){
 * 				//在顺利加入之后会触发对应的Listener
 * 				onMetricAdded(name, metric);
 * 			}else {
 * 				//不允许同名的Metric
 * 				throw new IllegalArgumentException("A metric named " + name + " already exists");
 * 			}
 * 		}
 * 		return metric;
 * }
 * 
 * public void registerAll(MetricSet metrics) throws IllegalArgumentException {
 * 		registerAll(null, metrics);
 * }
 * 
 * private void registerAll(String prefix, MetricSet metrics) throws IllegalArgumentException {
 * 		for(Map.Entry<String, Metric> entry : metrics.getMetrics().entrySet()){
 * 			if(entry.getValue() instanceof MetricSet){
 * 				registerAll(name(prefix, entry.getKey()), (MetricSet) entry.getValue());
 * 			}else {
 * 				register(name(prefix, entry.getKey()), entry.getValue());
 * 			}
 * 		}
 * }
 * 
 * 通过Register Metric方法，我们可以得出如下几个结论。
 * 1.在Metrics注册表中不允许出现同名的Metric，哪怕它们是不同类型的Metric。
 * 2.MetricSet是一种特殊的Metric，它是若干个Metric的集合，同时又是Metric的子类（接口）。
 * 3.MetricSet不仅可以存放Metric，还可以存放另一个MetricSet。
 * 4.Metric在注册表中的存放是扁平化的，即以Key（Metric的名称）Value（Metric的实例）对的形式出现。
 * 
 * 通过上面的代码，我们知道所有的Metric在注册表中都是扁平化存放的，并没有额外的层级结构。回顾我们之前所有的代码演示，
 * 你会发现度量指标的输出都是进行了分门别类的，这就会涉及Metric读取逻辑了，示例代码如下。
 * 
 * @SuppressWarnings("unchecked")
 * private <T extends Metric> SortedMap<String, T> getMetrics(Class<T> klass, MetricFilter filter) {
 *		//定义一个TreeMap，用于存放某种类型的Metric
 *		//①代码略微有点瑕疵
 * 		final TreeMap<String, T> timers = new TreeMap<String, T>();
 * 		//循环遍历concurrentMap
 * 		for(Map.Entry<String, Metric> entry : metrics.entrySet()){
 * 			//是某种Metric的实例，且MetricFilter成功匹配则作为返回对象
 * 			if(klass.isInstance(entry.getValue()) && filter.matches(entry.getKey(), entry.getValue())){
 * 				timers.put(entry.getKey(), (T) entry.getValue());
 * 			}
 * 		}
 * 		//不允许修改
 * 		return Collections.unmodifiableSortedMap(timers);
 * }
 *	//获取所有的Gauge Metric
 * public SortedMap<String, Gauge> getGauges() {
 * 		//给定MetricFilter.All
 * 		return getGauges(MetricFilter.ALL);
 * }
 *	//最终会调用getMetrics方法
 * public SortedMap<String, Gauge> getGauges(MetricFilter filter){
 * 		return getMetrics(Gauge.class, filter);
 * }
 * 
 * 上面的代码很清晰地阐述了某种类型的Gauge的获取过程，当然，其他类型的Metric获取也与此如出一辙，在代码注释的标记①处，
 * 这块的代码存在一点瑕疵，就是命名为timers会让人误以为获取的都是Timer类型的Metric。
 * 除此之外，代码逻辑比较清晰，调用关系简洁明了，这也是我们在代码编写中可以参考的地方。
 * 
 * 2.Metric的命名
 * 在将某个Metric注册至Metric Registry的时候，我们需要为它给定一个名称，这个名称可以通过hard code的方式给出，
 * 如果担心会出现重复（若重复则会出现错误），那么完全可以借助Metric Registry的name方法为你的Metric命名，示例代码如下。
 * //给定一个前缀和若干个可变参数
	public static String name(String name, String ... names) {
		final StringBuilder builder = new StringBuilder();
		append(builder, name);
		if(names != null) {
			for(String s : names) {
				append(builder, s);
			}
		}
		return builder.toString();
	}
	//使用某个class进行命名
	public static String name(Class<?> klass, String ... names) {
		return name (klass.getName(), names);	
	}
	//多个可变参数将会以“.”的形式进行连接
	private static void append(StringBuilder builder, String part) {
		if(part != null && !part.isEmpty()) {
			if(builder.length() > 0) {
				builder.append('.');
			}
			builder.append(part);
		}
	}
	
 * 3.Metric的创建
 * 在第7.2节“五大Metric详解”中，通过关键字new创建了对应的Metric，然后将其注册至MetricRegistry中，
 * 除了这种方式之外，还可以借助于Metirc提供的方法进行Metric的创建，使用MetricRegistry的创建方法除了会创建出某种类型的Metric之外，
 * 还会直接将其注册至MetricRegistry中。下面就以Timer的创建作为示例来分析它的源码，其它类型的都与之类似。
 * 
 *	//给定一个Metric名字，即可创建一个新的Timer并且注册到注册表中
 * public Timer timer(String name) {
 * 		//调用getOrAdd方法，稍后我们会具体解释该方法
 * 		return getOrAdd(name, MetricBuilder.TIMERS);
 * }
 * 
 * //给定一个Metric名字的同时，给定一个MetricSupplier
 * public Timer timer(String name, final MetricSupplier<Timer> supplier) {
 * 		//调用getOrAdd方法，稍后我们会具体解释该方法
 * 		return getOrAdd(name, new MetricBuilder<Timer>(){
 * 			//调用supplier的newMetric方法创建
 * 			@Override
 * 			public Timer newMetric(){
 * 				return supplier.newMetric();
 * 			}
 * 			//判断是否为Timer的实例
 * 			@Override
 * 			public boolean isInstance(Metric metric) {
 * 				return Timer.class.isInstance(metric);
 * 			}
 * 		});
 * }
 * 
 * 通过源码和关键地方的注释，我们可以看到，创建某种类型的Metric事实上是通过getOrAdd方法进行的，具体实现方法如下面的示例代码所示。
 * @SuppressWarnings("unchecked")
 * private <T extends Metric> T getOrAdd(String name, MetricBuilder<T> builder) {
 * 		//如果该Metric B存在，并且类型一致，则直接返回在注册表中已经存在的Metric实例
 * 		final Metric metric = metric.get(name);
 * 		if(builder.isInstance(metric)) {
 * 			return (T) metric;
 * 		}else if(metric == null) {
 * 			try{
 * 				return register(name, builder.newMetric());
 * 			}catch(IllegalArgumentException e) {
 * 				final Metric added = metrics.get(name);
 * 				if(builder.isInstance(added)){
 * 					return (T) added;
 * 				}
 * 			}
 * 		}
 * 		throw new IllegalArgumentException(name + " is already used for a different type of metric");
 * }
 * 
 * 经过前面几段代码的分析，我们分别看到了在MetricSupplier和MetricBuilder中都存在对应的newMetric方法，
 * 事实上，MetricSupplier只是对外进行使用的，而MetricBuilder则是在内部进行工作的具体接口，MetricBuilder方法代码如下。
 * private interface MetricBuilder<T extends Metric> {
		MetricBuilder<Counter> COUNTERS = new MetricBuilder<Counter>() {
			@Override
			public Counter newMetric() {
				return new Counter();
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Counter.class.isInstance(metric);
			}
		};
		
		MetricBuilder<Histogram> HISTOGRAMS = new MetricBuilder<Histogram>() {
			@Override
			public Histogram newMetric() {
				return new Histogram(new ExponentiallyDecayingReservoir());
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Histogram.class.isInstance(metric);
			}
		};
		
		MetricBuilder<Meter> METERS = new MetricBuilder<Meter>() {
			@Override
			public Meter newMetric() {
				return new Meter();
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Meter.class.isInstance(metric);
			}
		};
		
		MetricBuilder<Timer> TIMERS = new MetricBuilder<Timer>() {
			@Override
			public Timer newMetric() {
				return new Timer();
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Timer.class.isInstance(metric);
			}
		};
		T newMetric();
		boolean isInstance(Metric metric);
	}
 * 
 * MetricBuilder接口提供了newMetric方法和isInstance方法，并且提供了4种不同类型的Metric Builder可供直接使用。
 * 
 * MetricRegistry的源码虽然足够简单，但是这并不能掩饰它的优雅、简洁和清爽，一口气阅读下来会让人感觉像是在阅读散文诗集一样，
 * 作为一个老程序员，强烈建议大家培养阅读源码的习惯，尤其是优秀的开源项目的源码。
 * 
 * 7.5.2 Reporter如何工作
 * 在Metric中，除了JmxReporter之外，其他的几个内置Reporter都具备定时输出注册表中对应Metric度量指标数据的功能。
 * 另外，Reporter也是在其他开源项目中被扩展最多的组件之一，比如，将Metric中数据写入数据库，或者中间件，发送至远程的某个TCP端口等，
 * 都可以借助于自定义Reporter来实现。如图7-11所示的是Metrics内置Reporter之间的继承关系。
 * 
 * Reporter只是一个标记接口，并未提供任何方法，因此本节将主要讲解具有定时输出能力的Reporter。
 * 
 * 在ScheduledReporter的执行过程中需要引入ScheduledExecutorService，这也是所有的指标度量信息能够根据某个特定的周期进行输出的关键，
 * ScheduledReporter提供了对ScheduledExecutorService的定制，比如自定义ThreadFactory，示例代码如下。
 * private static class NamedThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		
		private NamedThreadFactory(String name) {
			final SecurityManager s = System.getSecurityManager();
			this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.namePrefix = "metrics-" + name + "-thread-";
		}
		@Override
		public Thread newThread(Runnable r) {
			final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			//注释①
			t.setDaemon(true);
			if(t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
 * 在注释①处，所有的线程将会被指定为守护线程，请大家思考一下，JVM进程之所以能够正常退出，最主要的一个原因就是进程中没有正在运行的非守护线程，
 * Metrics的Reporter提供了定时线程服务，如果不将其内部的工作线程设定为守护线程，则会导致JVM永远无法停止，除非在应用程序中对Reporter进行手动管理。
 * 
 * 调用Reporter的start方法，实际上是执行了ScheduledExecutorService的scheduleAtFixedRate方法。start方法的实现代码具体如下。
 * 
 * synchronized public void start(long initialDelay, long period, TimeUnit unit) {
		start(initialDelay, period, unit, new Runnable() {

			@Override
			public void run() {
				try {
					report();
				}catch (Throwable ex) {
					LOG.error("Exception thrown from {}#report. Exception was suppressed.", ScheduledReporter.this.getClass().getSimpleName(), ex);
				}
			}
		});
	}
	private void start(long initialDelay, long period, TimeUnit unit, Runnable runnable) {
		if(this.scheduledFuture != null) {
			throw new IllegalArgumentException("Reporter already started");
		}
		this.scheduledFuture = executor.scheduleAtFixedRate(runnable, initialDelay, period, unit);
	}
 * 
 * scheduleAtFixedRate的执行过程，实际上是按照固定的时间周期调用report方法，然后输出MetricRegistry中的所有指标度量数据，示例代码具体如下。
 * public void report() {
		synchronized (this) {
			report(registry.getGauges(filter), registry.getCounters(filter), registry.getHistograms(filter), registry.getMeters(filter), registry.getTimers(filter));
		}
	}
 * 
 * 有参的report方法则是一个抽象方法，需要具体的子类对其进行实现，比如，Console或者Logger，由于代码比较简单，因此这里将不再进行讲解，读者可以自行阅读。
 */
public class SourceCodeAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	//给定一个前缀和若干个可变参数
	public static String name(String name, String ... names) {
		final StringBuilder builder = new StringBuilder();
		append(builder, name);
		if(names != null) {
			for(String s : names) {
				append(builder, s);
			}
		}
		return builder.toString();
	}
	//使用某个class进行命名
	public static String name(Class<?> klass, String ... names) {
		return name (klass.getName(), names);	
	}
	//多个可变参数将会以“.”的形式进行连接
	private static void append(StringBuilder builder, String part) {
		if(part != null && !part.isEmpty()) {
			if(builder.length() > 0) {
				builder.append('.');
			}
			builder.append(part);
		}
	}
	
	private interface MetricBuilder<T extends Metric> {
		MetricBuilder<Counter> COUNTERS = new MetricBuilder<Counter>() {
			@Override
			public Counter newMetric() {
				return new Counter();
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Counter.class.isInstance(metric);
			}
		};
		
		MetricBuilder<Histogram> HISTOGRAMS = new MetricBuilder<Histogram>() {
			@Override
			public Histogram newMetric() {
				return new Histogram(new ExponentiallyDecayingReservoir());
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Histogram.class.isInstance(metric);
			}
		};
		
		MetricBuilder<Meter> METERS = new MetricBuilder<Meter>() {
			@Override
			public Meter newMetric() {
				return new Meter();
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Meter.class.isInstance(metric);
			}
		};
		
		MetricBuilder<Timer> TIMERS = new MetricBuilder<Timer>() {
			@Override
			public Timer newMetric() {
				return new Timer();
			}
			
			@Override
			public boolean isInstance(Metric metric) {
				return Timer.class.isInstance(metric);
			}
		};
		T newMetric();
		boolean isInstance(Metric metric);
	}
	
	private static class NamedThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;
		
		private NamedThreadFactory(String name) {
			final SecurityManager s = System.getSecurityManager();
			this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.namePrefix = "metrics-" + name + "-thread-";
		}
		@Override
		public Thread newThread(Runnable r) {
			final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
			//注释①
			t.setDaemon(true);
			if(t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
	
//	synchronized public void start(long initialDelay, long period, TimeUnit unit) {
//		start(initialDelay, period, unit, new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					report();
//				}catch (Throwable ex) {
//					LOG.error("Exception thrown from {}#report. Exception was suppressed.", ScheduledReporter.this.getClass().getSimpleName(), ex);
//				}
//			}
//		});
//	}
//	private void start(long initialDelay, long period, TimeUnit unit, Runnable runnable) {
//		if(this.scheduledFuture != null) {
//			throw new IllegalArgumentException("Reporter already started");
//		}
//		this.scheduledFuture = executor.scheduleAtFixedRate(runnable, initialDelay, period, unit);
//	}
//	
//	public void report() {
//		synchronized (this) {
//			report(registry.getGauges(filter), registry.getCounters(filter), registry.getHistograms(filter), registry.getMeters(filter), registry.getTimers(filter));
//		}
//	}
}