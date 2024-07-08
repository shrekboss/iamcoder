## Thread Pool 设计模式

### 模式简介

> 线程不仅在其执行任务时消耗 CPU 时间和内存等资源，线程对象(Thread 实例)本身以及线程所需要的调用栈(Call Stack)也占用内存，并且
> Java 中创建一个线程往往意味着 JVM 会创建相应的依赖于宿主机操作系统的本地线程(Native Thread)。
>
> Thread Pool 模式的核心思想是使用队列对待处理的任务进行缓存，并复用一定数量的工作者线程去取队列中的任务进行执行。
>
> Thread Pool 模式的本质使用极其有限的资源去处理相对无限的任务。

### 模式的评价与实现考量

- 抵消线程创建的开销，提高响应性。
    - 创建线程的消耗不仅包括线程对象本身以及其调用栈所需的内存空间，也包括创建依赖于 JVM 宿主机操作系统的本地线程。
- 封装了工作者线程生命周期管理
- 减少销毁线程的开销

#### 工作队列的选择

工作队列分类：

- 有界队列 - Bounded Queue
    - 适合在提交给线程池执行的各个任务之间时相互独立(而非有依赖关系)的情况下使用。
- 无界队列 - Unbounded Queue
    - 无界队列可能导致系统的不稳定，适合在任务占用的内存空间以及其他稀缺资源比较少的情况下使用。
- 直接交接队列 - SynchronousQueue
    - 如果应用程序确实需要比较大的工作队列容量，而又想避免无界工作队列可能导致的问题不妨考虑 SynchronousQueue。
    - SynchronousQueue 实现上并不使用缓存空间。
    - 使用 SynchronousQueue 作为工作队列，工作队列本身并不限制执行的任务的数量。但此时需要限定线程池的最大大小为一个合理的有限制，而不是
      Integer.MAX_VALUE，否则可能导致线程池中的工作者线程的数量一直增加到系统资源所无法承受为止。

#### 线程池大小调整

合理的线程池大小取决于该线程池所要处理的任务的特性、系统资源状况以及任务所使用的稀缺资源状况等因素。

- 系统资源状况
    - 主要考虑系统 CPU 个数以及 JVM 堆内存大小。
    - `Runtime.getRuntime().availableProcessors()`
- 任务的特性
    - I/O 密集型：N(CPU个数) * 2
    - CPU 密集型：N(CPU个数) + 1 ： 考虑即便是 CPU 密集型的任务其执行线程也可能在某一个时刻由于某种原因，如缺页中断(Page
      fault)而出现等待。此时，一个额外的线程可以继续使用 CPU 时间片。
    - 混合型

计算线程合理大小的公式：S = N * U * ( 1 + WT / ST)

- N：CPU 的个数
- U：目标 CPU 使用率
- WT：任务执行线程进行等待的时间
- ST：任务执行线程使用 CPU 进行进行计算的时间
- 其中 WT 和 ST 可以通过工具(如 jvisualvm)计算出相应值

#### 线程池的监控

- getPoolSize()：获取当前线程池大小
- getQueue()：返回工作队列实例，通过该实例获取工作队列的当前大小
- getLargestPoolSize()：获取工作者线程曾经达到的最大数，该数值有助于确认线程池的最大大小设置是否合理
- getActiveCount()：获取线程池中当前正在执行任务的工作者线程数(近似值)
- getTaskCount()：获取线程池到目前为止所接收到的任务数(近似值)
- getCompleteTaskCount()：获取线程池到目前为止已经处理完毕的任务数(近似值)

#### 线程泄露

> 指线程池的工作者线程意外中止，是的线程池中实际可用的工作者线程变少。线程泄露通常是由线程对象的 run 方法中异常处理没有捕获
> RuntimeException 和 Error 导致 run 方法以意外返回，使得相应线程提前中止。
>
> 实际开发中需要注意另外一种可以事实上造成线程泄露的场景：如果线程池中的某个工作者线程执行的任务涉及外部资源等待，如等待
> I/O，而该任务有没有对这种等待指定时间限制。那么，外部资源如果一直没有返回该任务所等待的结果，就会导致执行该任务的工作者线程一直处于等待状态而无法执行其他任务，这就形成了事实上的线程泄露。

参考代码：

- [Counter.java](memoryleak%2FCounter.java)
- [MemoryLeakingServlet.java](memoryleak%2FMemoryLeakingServlet.java)
- [MemoryLeakPreventingServlet.java](memoryleak%2FMemoryLeakPreventingServlet.java)
- [MemoryPseudoLeakingServlet.java](memoryleak%2FMemoryPseudoLeakingServlet.java)

#### 可靠性与线程池饱和处理策略

- ThreadPoolExecutor.AbortPolicy
    - 直接抛出异常
    - 默认策略
    - 人工补救
- ThreadPoolExecutor.DiscardPolicy
    - 丢弃当前被拒绝的任务(而不抛出任何异常)
- ThreadPoolExecutor.DiscardOldestPolicy
    - 将缓冲区中最老的任务丢弃，然后重新尝试接纳被拒绝的任务
- ThreadPoolExecutor.CallerRunsPolicy
    - 在任务的提交方线程中运行被拒绝的任务
    - 唯一能够实现补救
    - 可能会引起线程安全问题
        - 假设某种任务其执行过程中使用了非线程安全对象，并且不需要与其他线程共享任何对象，此时我们完全可以考虑使用最大工作者线程数为
          1 的 ThreadPoolExecutor 实例来执行这些任务。此时，这些任务的相关代码可以采用单线程的方式去编写，而无需考虑数据同步和线程安全。这种情况下，如果采用
          ThreadPoolExecutor.CallerRunsPolicy 作为该 ThreadPoolExecutor 实例的线程饱和处理策略则可能引起线程安全问题。这是因为此情形下提交失败的任务会通过
          ThreadPoolExecutor.CallerRunsPolicy 实例被客户端线程重新执行，而客户端线程与该 ThreadPoolExecutor
          实例中的唯一工作者线程可能形成两个并发的线程，从而引发线程安全问题。

参考代码：[ReEnqueueRejectedExecutionHandler.java](ReEnqueueRejectedExecutionHandler.java)

如果采用无界队列作为线程池的工作队列，那么上述的线程饱和处理策略就不适用了。

#### 死锁

如果线程池中执行的任务在其执行过程中又会向同一个线程池提交另外一个任务，而前一个任务的执行结束又依赖于后一个任务的执行结果，那么当线程池中所有的线程都处于这种等待其他任务的处理结果，而这些线程所等待的任务仍然还在工作队列中的时候，由于线程池已经没有可以对工作队列中的任务进行处理的工作者线程，这种等待就会一直持续下去而形成死锁。

因此，适合提交同一个线程池实例执行的任务是相互独立的任务，而不是彼此有依赖关系的任务。

要执行彼此有依赖关系的任务可以考虑将不同类型的任务给不同的线程池实例执行，或者对负责任务执行的线程池实例进行如下配置:

1. 设置线程池的最大大小为一个有限值，而不是默认值 Integer.MAX_VALUE。
2. 使用 SynchronousQueue 作为工作队列。
3. 使用 ThreadPoolExecutor.CallerRunsPolicy 作为线程饱和处理策略。

采用上述配置的线程池之所以能够避免死锁，是因为：当线程池中的一个工作者线程 ThreadA 执行某个任务 TaskA
时，该任务向同一个线程池提交了另外一个任务 TaskB，而 TaskA 的执行结束依赖 TaskB 的处理结果。若此时线程池已满(这个依赖上述
1)，则 TaskB 入工作队列失败(这有依赖上述配置 2)。这时，TaskB 就由于线程池饱和(工作者队列满并且线程池也满)而被拒绝。但巧妙的一点事，此时
TaskB 可以由当前线程池中提交该任务的工作者线程(即 ThreadA)自身去执行(这有依赖上述配置 3)，即存在依赖关系的两个任务 TaskA
和 TaskB 此时有线程池中的同一个工作者线程执行，因此避免了死锁。

参考代码：[ThreadPoolDeadLockAvoidance.java](ThreadPoolDeadLockAvoidance.java)

#### 线程池空闲线程清理

> 对于 ThreadPoolExecutor 而言，在初始化实例时通过指定其构造器的第三、四个参数(long keeAliveTime、TimeUnit unit)，告诉
> ThreadPoolExecutor 对于核心工作者线程以外的线程，若已经空闲了指定时间，则将其清理掉。

