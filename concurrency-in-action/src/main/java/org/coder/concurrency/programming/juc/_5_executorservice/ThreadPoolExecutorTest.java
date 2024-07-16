package org.coder.concurrency.programming.juc._5_executorservice;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * 第5章 Java并发包之ExecutorService详解
 * <p>
 * 笔者在2018年出版的书《Java高并发编程详解：多线程与架构设计》第8章“线程池原理以及自定义线程池”一章中讲述了线程池的原理及实现方法。
 * 相信学习了这部分的读者可以基本了解为什么要有线程池，以及线程池的基本原理，还有在一个线程池中有哪些至关重要的属性。
 * <p>
 * 在过去比较长的一段时间里(JDK并未提供线程池解决方案之前)，项目开发中用到线程重用以及线程管理时，都要自行开发类似的线程池解决方案，
 * 比如Quartz在其内部就有自己的线程池实现方案，但是自JDK1.5版本引入线程池相关的实现以后，Java程序员就不需要自己再开发了。同时，
 * 笔者强烈建议大家直接使用Java提供的线程池解决方案，主要基于如下这样几点考虑：
 * 1)Java自带的线程池解决方案足够优秀，能够满足大多数开发者的需求；
 * 2)随着JDK版本的不断的升级，相信这些工具也会不断地更新优化或者添入更多的特性；
 * 3)由于大家使用的是同一线程池服务，因此在遇到问题时相互交流不会出现“鸡同鸭讲”之类的信息不对称等情况。
 * <p>
 * 本章将讲述Executor&ExecutorService接口以及ThreadPoolExecutor、ScheduleThreadPoolExecutor等相关实现。
 * 除此之外还会介绍Future、Callback等接口，ForkJoinPool(JDK1.7版本引入)以及JDK1.8版本引入的CompletableFuture等相关内容。
 * <p>
 * 5.1 Executor&ExecutorService详解
 * 虽然大多数情况下，我们更喜欢将Executor或ExecutorService直接称之为“线程池”，但是事实上这两个接口只定义了任务(Runnable/Callable)被提交执行的相关接口。
 * 由于我们在开发过程中使用得最多的是任务被提交执行的线程池解决方案，因此很多人一看到Executor或ExecutorService就称其为“线程池”，这也就不足为奇了。
 * <p>
 * Executor接口的定义非常简单，仅有一个简单的任务提交方法，代码如下。
 * public interface Executor {
 * 		//执行任务，至于该任务以何种方式被执行，就要依赖于具体的实现了
 * 		void execute(Runnable command);
 * }
 * <p>
 * ExecutorService接口继承自Executor接口，并且提供了更多用于任务提交和管理的一些方法，比如停止任务的执行等，具体如图5-1所示。
 * <p>
 * 本章将详细讲述ExecutorService的两个重要实现ThreadPoolExecutor和ScheduledThreadPoolExecutor(均是间接实现)。
 * <p>
 * 5.1.1 ThreadPoolExecutor详解
 * ThreadPoolExecutor是ExecutorService最为重要、最为常用的一个实现之一，我们通常所说的Java并发包线程池指的就是ThreadPoolExecutor，
 * 该线程池与笔者在《Java高并发编程详解：多线程与架构设计》第8章“线程池原理以及自定义线程池”中讲解的线程池原理非常类似，当然其功能要远强大于我们自己的实现。
 * 在开始学习ThreadPoolExecutor之前，我们先简单看一下Java官方文档是如何描述ThreadPoolExecutor的。
 * <p>
 * Thread pools address two different problems: they usually provide improved performance when executing large numbers of asynchronous tasks, 
 * due to reduced per-task invocation overhead, and they provide a means of bounding and managing the resources, including threads, consumed 
 * when executing a collection of tasks. Each ThreadPoolExecutor also maintains some basic statistics, such as the number of completed tasks.
 * 线程池主要解决了两个不同的问题：由于任务的异步提交，因此在执行大量的异步任务时可以提升系统性能；另外它还提供了限制和管理资源的方法，包括线程池中的工作线程、线程池任务队列中的任务，
 * 除此之外， 每一个ThreadPoolExcecutor还维护了一些基本的统计信息，比如已经完成的任务数量等。
 * <p>
 * 通过官网的这段描述，我们可以得出如下几个关键信息：
 * 第一，在线程池中有一定数量的工作线程，并且线程数量以及任务数量会受到一定的控制和管理；
 * 第二，任务的执行将以异步的方式进行，也就是说线程提交执行任务的方法将会立即返回；
 * 第三，线程池会负责执行任务的信息统计。
 * <p>
 * 1.ThreadPoolExecutor 快速体验
 * 要想创建一个ThreadPoolExecutor相对来说会有些复杂，主要是因为其中的构造参数比较多，下面的代码示例是一个ThreadPoolExecutor的构造示例。
 * 1).在下述代码中，注释①处创建了一个ThreadPoolExecutor，需要7个构造函数，当然，我们可以借助于Executors来创建ThreadPoolExecutor，关于这一点，后文会介绍到。
 * 2).注释②向线程池提交Runnable类型的任务进行异步执行，execute Runnable接口将不会关注任务执行的结果，因为没有返回值。
 * 3).如果要关注返回，我们可以通过submit方法提交执行Runnable或者Callable，该方法将会返回一个Future接口作为凭据(注释③)，稍后可以根据该凭据获取返回值。
 * 4).注释④通过Future的get方法(该方法为阻塞方法)获取任务执行后的结果，调用该方法会使得当前线程进入阻塞。
 * 5).execute方法和submit方法均为立即执行方法，因此当前线程将不会进入阻塞。
 * <p>
 * 注意：下面的程序在执行以后，JVM进程不会退出，由于我们创建了线程池，因此这就意味着在线程池中有指定数量的活跃线程，
 * JVM进程正常退出最关键的条件之一是在JVM进程中不存在任何运行着的非守护线程。
 * <p>
 * 2.ThreadPoolExecutor的构造
 * 构造ThreadPoolExecutor所需要的参数是比较多的，同时，ThreadPoolExecutor中提供了四个构造函数的重载形式，
 * 但是最终真正被调用的构造函数是囊括了所有7个构造参数的构造函数，代码如下所示。
 * public ThreadPoolExecutor(
 * 		int corePoolsize,
 * 		int maximumPoolSize,
 * 		long keepAliveTime,
 * 		TimeUnit unit,
 * 		BlockingQueue<Runnable> workQueue,
 * 		ThreadFactory threadFactory,
 * 		RejectedExecutionHandler handler
 * ) {}
 * 1).corePoolSize:用于指定在线程池中维护的核心线程数量，即使当线程池中的核心线程不工作，核心线程的数量也不会减少(在JDK1.6版本及以后可以通过设置允许核心线程超时的方法allowCoreThreadTimeOut来改变这种情况)。
 * 2).maximumPoolSize:用于设置线程池中允许的线程数量的最大值。
 * 3).keepAliveTime:当线程池中的线程数量超过了核心线程数并且处于孔先生，线程池将回收一部分线程让出系统资源，该参数可用于设置超过corePoolSize数量的线程在多长时间后被回收，与unit配合使用。
 * 4).TimeUnit:用于设定keepAliveTime的时间单位。
 * 5).workQueue:用于存放已提交至线程池但未被执行的任务。
 * 6).ThreadFactory：用于创建线程的工厂，开发者可以通过自定义ThreadFactory来创建线程，比如，根据业务名为线程命名、设置线程优先级、设置线程是否为守护线程、设置线程所属的线程组等。
 * 7).RejectedExecutionHandler：当任务数量超时阻塞队列边界时，这个时候线程池就会拒绝新增的任务，该参数主要用于设置拒绝策略。
 * <p>
 * ThreadPoolExecutor的构造比较复杂，除了其对每一个构造参数都有一定的要求之外(比如，不能为null)，个别构造参数之间也存在一定的约束关系。
 * 1).TimeUnit、workQueue、ThreadFactory、RejectedExecutionHandler不能为null。
 * 2).corePoolSize可以设置为0，但不能小于0，并且corePoolSize不能大于线程的最大数量(maximumPoolSize)。
 * <p>
 * 3.执行任务方法详解
 * 线程池被成功构造后，其内部的运行线程并不会立即被创建，ThreadPoolExecutor的核心线程将采用一种Lazy(懒)的方式来创建并且运行，当线程池被创建，并且首次调用执行任务方法时才会创建，并且运行线程。
 * <p>
 * 线程在线程池中被创建、任务被存入阻塞(任务)队列中、任务被拒绝等这一系列动作都是在执行任务的方法execute内部完成的，下面这段代码摘自ThreadExecutorPool的execute方法源码。
 * public void execute(Runnable command) {
 * 		//不允许Runnable为null
 * 		if(command == null)
 * 			throw new NullPointerException();
 * 		int c = ctl.get();
 * 		//线程池中的运行线程小于核心线程数，创建新的线程
 * 		if(workerCountOf(c) < corePoolSize) {
 * 			//①创建新的worker线程立即执行command
 * 			if(addWorker(command, true))
 * 				return;
 * 			//②创建线程失败，再次获取线程池的状态
 * 			c = ctl.get();
 * 		}
 * <p>
 * 		//③如果线程池未被销毁，并且任务被成功存入workQueue阻塞队列中
 * 		if(isRunning(c) && workQueue.offer(command)){
 * 			//再次校验，防止在第一次校验通过后线程池闭关
 * 			int recheck = ctl.get();
 *			//如果线程池关闭，则在队列中删除task并拒绝task
 * 			if(!isRunning(recheck) && remove(command))
 * 				reject(command);
 *			//④如果线程数=0()，新建线程但并不执行任务，只是去轮询workQueue，以获取任务队列中的任务
 * 			else if(workerCountOf(recheck) == 0)
 * 				addWorker(null, false);
 * 		//⑤当线程队列已满时，创建新线程执行task，创建失败后拒绝该task
 * 		}else if(!addWorker(command, false))
 * 			reject(command);
 * }
 * <p>
 * execute方法的源码并不多，但是开发者写得很紧凑，所以读起来会有些吃力，但是关键的地方我们都增加了相应的注释来说明，接下来，我们通过代码的方式来验证方法的逻辑。
 * 1).线程池核心线程数量大于0，并且首次提交任务时，线程池会立即创建线程执行该任务，并且该任务不会被存入任务队列之中。
 * 2).当线程池中的活跃(工作)线程大于等于核心线程数量并且任务队列未满时，任务队列中的任务不会立即执行，而是等待工作线程空闲时轮询任务队列以获取任务。
 * 3).当任务队列已满且工作线程小于最大线程数量时，线程池会创建线程执行任务，但是线程数量不会超过最大线程数，下面将上一段代码的最大循环数修改为14(最大线程数 + 任务队列size)，会发现同时有4个线程在工作。
 * 4).当任务队列已满且线程池中的工作线程达到最大线程数量，并且此刻没有空闲的工作线程时，会执行任务拒绝策略，任务将以何种方式被拒绝完全取决于构造ThreadExecutorPool时指定的拒绝策略。
 * 	若将执行任务的循环最大次数更改为15，再次执行时会发现只有14个任务被执行，第15个任务被丢弃(这里指定的拒绝策略为丢弃)。
 * 5).若线程池中的线程是空闲的且空闲时间达到指定的keepAliveTime时间，线程会被线程池回收(最多保留corePoolSize数量个线程)，当然如果设置允许线程池中的核心线程超时，那么线程池中所有的工作线程都会被回收。
 * <p>
 * 在ThreadExecutorPool中提交并执行任务的方法有很多种，除了execute方法之外，其他的方法都与Future和Callable有关，相关详情会在5.2节中继续讲述。
 */
public class ThreadPoolExecutorTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		//① 创建ThreadPoolExecutor，7个构造参数
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
		//② 提交执行异步任务，不关注返回值
		executor.execute(() -> System.out.println(" execute the runnable task"));
		//③ 提交执行异步任务，关注返回值
		Future<String> future = executor.submit(() -> " Execute the callable task and this is the result");
		//④获取并输出callable任务的返回值
		System.out.println(future.get());
	}

	@Test
	public void test2() {
		//新建线程池(本节中的线程池都将使用一致的构造参数)
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
		//当前线程池中并没有运行的线程
		assert executor.getActiveCount() == 0;
		assert executor.getMaximumPoolSize() == 4;
		assert executor.getCorePoolSize() == 2;
		//提交任务并执行
		executor.execute(() -> System.out.println("print task"));
		//当前线程池中有了一个运行线程，只不过该线程目前处于空闲状态
		assert executor.getActiveCount() == 1;
		assert executor.getMaximumPoolSize() == 4;
		assert executor.getCorePoolSize() == 2;
	}

	@Test
	public void test3() throws InterruptedException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
		//线程池会立即创建线程并执行任务
		executor.execute(() -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Task finish done.");
		});
		assert executor.getActiveCount() == 1;
		assert executor.getQueue().isEmpty();
		executor.shutdown();
		TimeUnit.SECONDS.sleep(15);
		System.out.println("end.");
	}

	/**
	 * 运行程序时会看到只有两个工作线程在工作，即使我们构造线程池时指定最大的线程数量为4，但是，这种情况下线程池也不会再创建多余的两个线程执行任务，
	 * 这一点与execute源码注释③处的逻辑完全一致。
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void test4() throws InterruptedException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
		//在线程池中执行12个任务
		for (int i = 0; i < 12; i++) {
			executor.execute(() -> {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Task finish done by " + Thread.currentThread());
			});
		}

		TimeUnit.SECONDS.sleep(10);
		System.out.println("end.");
	}

	@Test
	public void test5() throws InterruptedException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
		for (int i = 0; i < 14; i++) {
			executor.execute(() -> {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Task finish done by " + Thread.currentThread());
			});
		}
		TimeUnit.SECONDS.sleep(10);
		System.out.println("end.");
	}

	@Test
	public void test6() throws InterruptedException {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
		//设置允许核心线程超时
		executor.allowCoreThreadTimeOut(true);
		for (int i = 0; i < 15; i++) {
			executor.execute(() -> {
				System.out.println("Task finish done by " + Thread.currentThread());
			});
		}
		//休眠，使工作线程空闲时间达到keepAliveTime
		TimeUnit.MINUTES.sleep(2);
		assert executor.getActiveCount() == 0;
		System.out.println("end.");
	}

}