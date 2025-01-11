## Two-phase Termination 设计模式

> 当一个线程正常结束，或者因被打断而结束，或者因出现异常而结束时，我们需要考虑如何同时释放线程中资源，比如文件句柄、socket
> 套接字句柄、数据库连接等比较稀缺的资源。
>
> 当希望结束这个线程时，发出线程结束请求，接下来线程不会立即结束，而是会执行相应的资源释放动作直到真正的结束，在终止处理状态时，线程虽然还在运行，
> 但是进行的是终止处理工作，因此终止处理又称为线程结束的第二阶段，而受理终止要求则被称为线程结束的第一个阶段。

在进行线程两阶段中介的时候需要考虑如下几个问题：

- 第二阶段的终止保证安全性，比如涉及对共享资源的操作；
- 要百分之一百地确保线程结束，假设在第二阶段出现死循环、阻塞等异常导致无法结束；
- 对资源的释放时间要控制在一个可控的范围之内。

Two Phase Termination 与其说是一个模式，还不如说是线程使用的一个技巧(best practice)。其主要针对的是当线程结束生命周期时，能有机会做一些资源释放的动作。

参考代码：

- [ClientHandler.java](ClientHandler.java)

### 模式简介

> Two-phase Termination 模式通过将停止线程这个动作分解为准备阶段和执行阶段这两个阶段，提供了一种通用的用于优雅地停止线程的方法。所谓“优雅”是指可以等要停止的线程在其处理完待处理的任务后才停止，而不是强行停止。

准备阶段

- 该阶段的主要动作是“通知”目标线程(欲停止的线程)准备进行停止。这一步会设置一个标志变量用于指示目标线程可以准备停止了。
- 还需要通过调用目标线程的 interrupt 方法，以期望目标线程能够通过捕获相关的异常侦测到该方法调用，从而中断其阻塞状态、等待状态。
- 对于能够 interrupt 方法调用做出相应的方法
    - **Object.wait()、Object.wait(long timeout)、Object.wait(long timeout, int nanos)**
        - 响应 interrupt 调用抛出异常：InterruptedException
    - **Thread.sleep(long millis)、Thread.sleep(long millis, int nanos)**
        - 响应 interrupt 调用抛出异常：InterruptedException
    - **Thread.join()、Thread.join(long millis)、Thread.join(long millis, int nanos)**
        - 响应 interrupt 调用抛出异常：InterruptedException
    - **java.util.concurrent.BlockingQueue.take()**
        - 响应 interrupt 调用抛出异常：InterruptedException
    - **java.util.concurrent.locks.Lock.lockInterruptibly()**
        - 响应 interrupt 调用抛出异常：InterruptedException
    - **java.nio.channels.InterruptiblyChannel**
        - 响应 interrupt 调用抛出异常：java.nio.channels.ClosedInterruptedException

执行阶段

- 该阶段的主要动作是检查准备阶段所设置的线程停止标志和信号，再次基础上决定线程停止的时机，并进行适当的“清理”操作。

参考代码：

- [AlarmMgr.java](alarm%2FAlarmMgr.java)
- [Terminatable.java](alarm%2FTerminatable.java)
    - [AbstractTerminatableThread.java](alarm%2FAbstractTerminatableThread.java)
        - [AlarmSendingThread.java](alarm%2FAlarmSendingThread.java)
- [TerminationToken.java](alarm%2FTerminationToken.java)

AbstractTerminatableThread 是一个可复用的 Terminatable 参与者实例， 其 terminate 方法完成了线程停止的准备阶段。该方法首先将
terminationToken 的 toShutdown 属性设置为 true，此时目标线程可以准备停止了。但是目标线程可能处于一些阻塞(Blocking)
方法的调用，如调用 Thread.sleep、InputStream.read 等，无法检测该变量的值。调用目标线程的 interrupt
方法可以使一些阻塞方法抛出异常从而使目标线程停止。但是也有些阻塞方法如 InputStream.read 并不对 interrupt 方法调用做出响应，此时需要有
AbstractTerminatableThread 的子类实现 doTerminate 方法，在该方法中实现一些关闭目标线程所需要的额外操作。

执行阶段在 AbstractTerminatableThread 的 run 方法中完成。该方法通过 TerminationToken 的 toShutdown 属性和 reservations
属性的判断或者通过捕获有 interrupt 方法调用而抛出的异常来终止线程，并在线程终止前调用由 AbstractTerminatableThread 子类实现的
doCleanup 方法用于执行一些清理动作。

解决：目标线程停止前可以保证处理完待处理的任务

规避：目标线程发送完队列中现有的告警信息后，doRun 方法不再被调用，从而避免了队列为空时 BlockingQueue.take 调用导致的阻塞。

### 模式的评价与实现考量

#### 线程停止标志

TerminationToken 使用了 toShutdown 这个 boolean 变量作为主要的停止标志，而非使用 Thread.isInterrupted()。这是因为，调用目标线程的
interrupt 方法无法保证目标线程的 isInterrupted() 方法返回值为 true：目标线程可能调用一些代码，它们捕获
InterruptedException 后没有通过设置 Thread.currentThread().interrupt() 保留线程中断状态。另外，toShutdown
这个变量保证了内存可见性而又避免了使用显示锁的开销，采用了 volatile 修饰。

#### 生产者-消费者问题中的线程停止

参考代码：

- [SomeService.java](SomeService.java)

#### 隐藏而非暴露可停止的线程

参考代码：

- [AlarmMgr.java](alarm%2FAlarmMgr.java)
    - `private final AlarmSendingThread alarmSendingThread;`

### Reference(Strong/soft/weak/phantom)

> 无论是 File 还是 Socket 等重量级的资源(严重依赖操作系统资源)，在进行释放时并不能百分之一百的保证成功(
> 可能是操作系统的原因)，在第二阶段的关闭有可能会失败，然后 socket 的实例会被垃圾回收器回收，但是 socket 实例对应的底层系统资源或许并未释放。
>
> JDK提供了对象被垃圾回收时的可追踪机制 - PhantomReference。借助于 PhantomReference
> 就可以很好地获取到哪个对象即将被垃圾回收器清楚，在被清楚之前还可以尝试一次资源回收，尽最大的努力回收重量级的资源是一种非常好的编程体验。

参考代码如下：

- [Reference.java](reference%2FReference.java)
- [LRUCache.java](reference%2FLRUCache.java)
- [SoftLRUCache.java](reference%2FSoftLRUCache.java)
- [SocketCleaningTracker.java](reference%2FSocketCleaningTracker.java)
- [ReferenceExample.java](reference%2FReferenceExample.java)


- 强引用 - Strong Reference
    - 只要引用到 ROOT 根的路径可达，无论怎样的 GC 都不会将其释放，而是宁可出现 JVM 内存溢出。
    - finalize 方法会在垃圾回收的标记阶段被调用(垃圾回收期在回收一个对象之前，首先会进行标记，标记过程则会调用该对象的
      finalize 方法，所以千万不要认为该方法被调用之后，就代表对象已被垃圾回收期回收，对象在 finalize
      方法中是可以“自我救赎”的)。
- 软引用 - Soft Reference
    - 当 JVM Detect(探测)到内存即将溢出，它会尝试 GC soft 类型的 reference。
    - **锯齿状是最理想的 JVM 内存状态**
    - [SoftReferenceTest.java](reference%2FSoftReferenceTest.java)
- 弱引用 - Weak Reference
    - 无论是 young GC 还是 full GC Weak Reference 的引用都会被垃圾回收器回收。
    - 无论是 SoftReference 还是 WeakReference 引用，被垃圾回收器回收后，都会被存放到与之关联的 ReferenceQueue 中。
    - [WeakReferenceTest.java](reference%2FWeakReferenceTest.java)
- 幻影引用 - Phantom Reference
    - Phantom Reference 必须和 ReferenceQueue 配合使用。
    - Phantom Reference 的 get 方法返回的始终是 null。
    - 当垃圾回收器决定回收 Phantom Reference 对象的时候会将其插入关联的 ReferenceQueue 中。
    - 使用 Phantom Reference 进行清理动作要比 Object 的 finalize 方法更加灵活。
    - [PhantomReferenceTest.java](reference%2FPhantomReferenceTest.java)

