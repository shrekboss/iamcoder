## Active Object 模式的可复用实现代码

> Active Object 模式通过将方法的调用和执行分离，实现了异步编程。有利于提高并发性，从而提高系统的吞吐率。
>
> Active Object 模式还有个好处是它可以将任务(MethodRequest)的提交(调用异步方法)和任务的执行策略(Execution Policy)
> 分离。任务的执行策略
> 可以反应一下一些问题：
> - 采用什么顺序去执行任务，如 FIFO、LIFO，或者基于任务中包含的信息所定的优先级？
> - 多少个任务可以并发执行？
> - 多少个任务可以被排队等待执行？
> - 如果有任务由于系统过载被拒绝，此时哪个人物该被选中作为牺牲品，应用程序该如何被通知到到？
> - 任务执行前、执行后需要执行哪些操作？
>
> 该模式的参与者有 6 个之多，其实现过程也包含了不少中间的处理：MethodRequest 对象的生成、MethodRequest 对象的移动(
> 进出缓冲区)、MethodRequest 对象的运行调度和线程上下文切换等。这些处理都有其空间和时间的代价。因此，Active Object
> 模式适合于分解一个比较耗时的任务(如涉及 I/O 操作的任务)：将任务的发起和执行进行分离，以减少不必要的等待时间。

| 参与者             | 可以借用的 JDK类                                                                                                          | 备注                                                                                                                                          |
|-----------------|---------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| Scheduler       | Java Execution Framework 中的 java.util.concurrent.ExecutorService 接口的相关实现类，如 Java.util.concurrent.ThreadPoolExecutor | ExecutorService 接口所定义的 submit(Callable<T> task) 方法相当于 enqueue 方法                                                                            |
| ActivationQueue | java.util.concurrent.LinkedBlockingQueue                                                                            | 若 Scheduler 采用 Java.util.concurrent.ThreadPoolExecutor，则 java.util.concurrent.LinkedBlockingQueue Queue 实例作为 ThreadPool Executor 构造器的参数参入即可 |
| MethodRequest   | java.util.concurrent.Callable 接口实现类                                                                                 | Callable 接口比起 Runnable 接口的优势在于它定义的 call 方法有返回值，便于将该返回值传递给 Future 实例。通常使用 Callable 接口的匿名实现类即可。                                               |
| Future          | java.util.concurrent.Future                                                                                         | ExecutorService 接口所定义的 submit(Callable task) 方法的返回值类型就是 java.util.concurrent.Future                                                         |

### 错误隔离

> 错误隔离指一个任务的处理失败不影响其他任务的处理。选用 JDK 中实现的类(如 ThreadPoolExecutor)来实现 Scheduler
> 的一个好处就是这些类可能已经实现了错误隔离。

参考代码：[CustomScheduler.java](CustomScheduler.java)

### 缓冲区监控

> 通过定时任务周期性地调用 ThreadPoolExecutor 的 getQueue 方法对缓冲区的大小进行监控。当然，在监控缓冲区的时候，往往只需要大致的值，因此在监控代码中要注意避免不必要的锁。

### 缓冲区饱和处理策略

- ThreadPoolExecutor.AbortPolicy
    - 直接抛出异常
- ThreadPoolExecutor.DiscardPolicy
    - 丢弃当前被拒绝的任务(而不抛出任何异常)
- ThreadPoolExecutor.DiscardOldestPolicy
    - 将缓冲区中最老的任务丢弃，然后重新尝试接纳被拒绝的任务
- ThreadPoolExecutor.CallerRunsPolicy
    - 在任务的提交方线程中运行被拒绝的任务

对于 ThreadPoolExecutor 而言，工作队列满的情况下，新提交的任务会用所有核心线程之外的新增线程来执行，直到工作者线程数达到最大线程数时，新提交的任务才会被拒绝。

### Scheduler 空闲工作者线程清理

对于 ThreadPoolExecutor 而言，在初始化实例时通过指定其构造器的第三、四个参数(long keeAliveTime、TimeUnit unit)，告诉
ThreadPoolExecutor 对于核心工作者线程以外的线程，若已经空闲了指定时间，则将其清理掉。

### [ActiveObjectProxy.java](ActiveObjectProxy.java)

> 对该代理对象的异步方法(即返回值类型为 java.util.concurrent.Future 的方法)的调用会被 ActiveObjectProxy 实现
> InvocationHandler(DispatchInvocationHandler)所拦截，并转发给 ActiveObjectProxy 的 newInstance 方法中指定的 Servant 处理。
>
> 使用 ActiveObjectProxy 实现 Active Object 模式，应用代码只需要调用 ActiveObjectProxy 的静态方法 newInstance 即可。

应用代码调用 newInstance 方法需要指定以下参数：

- 指定 Active Object 模式对外暴露的接口，该接口作为第 1 个参数传人。
- 创建 Active Object 模式对外暴露的接口的实现类。该类的实例作为第 2 个参数传入。
- 指定一个 java.util.concurrent.ExecutorService 实例。该实例作为第 3 个参数传入。