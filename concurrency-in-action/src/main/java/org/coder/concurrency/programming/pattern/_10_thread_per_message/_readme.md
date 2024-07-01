## Thread-Per-Message 设计模式

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
- 
### 多用户的网络聊天

参考代码如下：

- [ChatServer.java](chat%2FChatServer.java)
- [ClientHandler.java](chat%2FClientHandler.java)
- [ChatServerTest.java](chat%2FChatServerTest.java)
