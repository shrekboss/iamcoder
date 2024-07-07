### Producer-Consumer 设计模式

参考代码：

- [AttachmentProcessor.java](..%2FAttachmentProcessor.java)
- [Channel.java](..%2FChannel.java)
    - [BlockingQueueChannel.java](..%2FBlockingQueueChannel.java)

### 模式的评价与实现考量

#### 通道积压

- 使用有界队列。
- 使用带流量控制的无界阻塞队列。
    - 即对同一时间内可以有多少个生产者线程往通道中存储“产品”进行限制。
    - 参开代码：[SemaphoreBasedChannel.java](..%2FSemaphoreBasedChannel.java)

#### 工作窃取算法

一个通道实例对应多个队列实例的时候，当一个消费者线程处理完该线程对应的队列中的“产品”时，它可以继续从其他消费者线程对应的队列中取出“产品”进行处理，这样不会导致该消费者闲置处理，并减轻其他消费者线程的负担。这就是工作窃取(
Work Stealing)。

参开代码：

- [WorkStealingEnabledChannel.java](..%2FWorkStealingEnabledChannel.java)
    - [WorkStealingChannel.java](..%2FWorkStealingChannel.java)
- [WorkStealingExample.java](..%2FWorkStealingExample.java)

#### 线程的停止

借助 Two-phase termination 模式来先停止 producer 参与者的工作者线程。但某个服务的所有 Producer 参与者的工作线程都停止之后，在停止该服务涉及的
Consumer 参与者的工作者线程。

#### 高性能高可靠性的 Producer-Consumer 模式实现

如果应用程序对准备采用 Producer-Consumer 模式实现的服务有较高的性能和可靠性的要求，那么不妨考虑使用开源的
Producer-Consumer 模式实现库 LMAX Disruptor。 