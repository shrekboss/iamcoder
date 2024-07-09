## Thread-Per-Message(Serial Thread Confinement 串行线程封闭) 设计模式

> Thread-Per-Message 的意思是为每一个消息的处理开辟一个线程使得消息能够并发的方式进行处理，从而提高系统整体的吞吐能力。但是在开发中不建议采用这种方式。
> - 假如每个线程执行的时间比较长，那么在某个时刻 JVM 会由于无法再创建新的线程而导致栈内存溢出；
> - 假如每一个任务的执行时间都比较短，频繁地创建销毁线程对系统性能的开销也是一个不小的影响。
>
> 这种处理方式虽然有很多问题，但不代表一无是处，其实它也有自己的使用场景，比如在基于 Event 的编程模型(Event Bus 设计模式)
> 中，当系统初始化事件发生时，需要进行若干资源的后台加载，由于系统初始化时的任务数量并不多，可以考虑使用该模式响应初始化
> Event，或者系统在关闭时，进行资源回收也可以考虑将销毁事件触发的动作交给该模式。
>
> 另外补充：Thread-Per-Message 模式在网络通信中的使用也是非常广泛的。

每个任务一个线程，参考代码如下：

- [Request.java](Request.java)
- [TaskHandler.java](TaskHandler.java)
- [Operator.java](Operator.java)
- [Client.java](Client.java)

### 多用户的网络聊天

参考代码如下：

- [ChatServer.java](chat%2FChatServer.java)
- [ClientHandler.java](chat%2FClientHandler.java)
- [ChatServerTest.java](chat%2FChatServerTest.java)

### Serial Thread Confinement 模式简介

> 核心思想：通过将多个并发的任务存入队列实现任务的串行化，并为这些串行化的任务创建唯一的一个工作者线程进行处理。
>
> 本质：使用一个开销更小的锁(串行化并发任务时所用队列涉及的锁) 去替代另一个可能得开销更大的锁(
> 为保障并发任务所访问的非线程安全对象可能引入的锁)。

### Serial Thread Confinement 模式实战案例分析

参考代码：

- [MessageFileDownloader.java](ftp%2FMessageFileDownloader.java)
- [WorkerThread.java](ftp%2FWorkerThread.java)
    - [FakeWorkerThread.java](ftp%2FFakeWorkerThread.java)
- [SampleClient.java](ftp%2FSampleClient.java)

### Serial Thread Confinement 模式的评价与实现

Serial Thread Confinement 模式可以帮助我们在不使用锁的情况下实现线程安全。但是，是实际使用时需要注意 Serial Thread
Confinement 模式自身的开销：将任务串行化所涉及的进出队列以及 Serializer 创建向 WorkerThread 提交的任务对象这些动作都有时间和空间的开销。

需要注意锁的开销：如果采用锁去保障对某个非线程安全对象的访问的线程安全，那么这个锁的开销比起 Serial Thread Confinement
模式中使用的队列涉及的锁哪个开销更大些？

Serial Thread Confinement 模式的典型应用场景包括以下两个：

- 需要使用非线程安全对象，但又不希望引入锁。
- 任务的执行涉及 I/O 操作，但不希望过多的 I/O 线程增加上下文切换。