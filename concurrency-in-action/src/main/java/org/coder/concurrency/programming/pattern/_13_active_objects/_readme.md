## Active Objects 设计模式

> Active 是“主动”的意思，Active Objects 是“主动对象”的意思，所谓主动对象是指其拥有自己的独立线程，比如 java.lang.Thread
>
> 实例就是一个主动对象，不过 Active Objects Pattern 不仅仅是拥有独立的线程，它还可以接受异步消息，并且能够返回处理的结果。
>
> System.gc() 方法就是一个“接受异步消息的主动对象”，调用 gc 方法的线程和 gc 自身执行线程并不是同一个线程。

分别设计了两种不同的 Active Objects 模式实现，第二种方式更加通用一些，因为它摒弃了第一种方式需要手动定义方法的 Message 以及
Proxy 等缺陷，通过动态代理的方式动态生成代理类。

Active Objects 模式既能够完整地保留接口方法的调用形式，又能让方法的执行异步化，这也是其他接口异步调用模式(Future
模式：只提供了任务的异步执行方案，但是无法保留接口原有的调用形式)无法同时做到的。

Active Objects 模式中使用了很多其他设计模式，代理类的生成(代理设计模式)、ActiveMessageQueue(Guarded Suspension Pattern 以及
Worker-Thread Pattern)、findOrderDetails 方法(Future 设计模式)。

参考代码如下

- [OrderService.java](OrderService.java)
    - [OrderServiceImpl.java](OrderServiceImpl.java)
- [ActiveDaemonThread.java](ActiveDaemonThread.java)
- [ActiveFuture.java](ActiveFuture.java)
- [ActiveMessage.java](ActiveMessage.java)
- [ActiveMessageQueue.java](ActiveMessageQueue.java)
- [ActiveMethod.java](ActiveMethod.java)
- [ActiveServiceFactory.java](ActiveServiceFactory.java)
- [IllegalActiveMethod.java](IllegalActiveMethod.java)
- [ActiveOrderServiceTest.java](ActiveOrderServiceTest.java)

### 模式简介

> Active Object 模式是一种异步编程模型。它通过对方法的调用(Method Invocation) 与方法的执行(Method Execution) 进行解耦(
> Decoupling) 来提高并发性。

参考代码如下：

- [MMSDeliveryServlet.java](mmsc%2FMMSDeliveryServlet.java)
- [RequestPersistence.java](mmsc%2FRequestPersistence.java)
    - [AsyncRequestPersistence.java](mmsc%2FAsyncRequestPersistence.java)
    - [DiskbasedRequestPersistence.java](mmsc%2FDiskbasedRequestPersistence.java)

Active Object 模式的 Proxy 参与者相当于 Promise 模式中的 Promisor 参与者，其 asyncService 异步方法的返回值类型 Future 相当于
Promise 模式中的 Promise 参与者。
