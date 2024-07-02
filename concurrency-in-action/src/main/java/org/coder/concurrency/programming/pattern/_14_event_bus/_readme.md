## Event Bus 设计模式

- Bus 接口对外提供了几种主要的使用方式，比如 post 方法用来发送 Event，register 方法用来注册 Event 接受者(Subscriber)
  接受响应事件，EventBus 采用同步的方式推送 Event，AsyncEventBus 采用异步的方式(Thread-Per-Message) 推送 Event。
- Registry 注册表，主要用来记录对应的 Subscriber 以及受理消息的回调方法，回调方法我们用注解 @Subscribe 来标识。
- Dispatch 主要用来将 Event 广播给注册表中监听了 topic 的 Subscriber。

参考代码：

- [Bus.java](Bus.java)
    - [EventBus.java](EventBus.java)
        - [AsyncEventBus.java](AsyncEventBus.java)
- [Subscribe.java](Subscribe.java)
- [Registry.java](Registry.java)
- [Dispatcher.java](Dispatcher.java)
- [Subscriber.java](Subscriber.java)
- [EventExceptionHandler.java](EventExceptionHandler.java)
- [EventContext.java](EventContext.java)
- [SimpleSubscriber1.java](SimpleSubscriber1.java)
- [SimpleSubscriber2.java](SimpleSubscriber2.java)
- [Test.java](Test.java)

### 监控目录变化

- [DirectoryTargetMonitor.java](DirectoryTargetMonitor.java)
- [FileChangeEvent.java](FileChangeEvent.java)
- [FileChangeListener.java](FileChangeListener.java)

EventBus 有点类似于 GOF 设计模式中的监听者模式，但是 EventBus 提供的功能更加强大，使用起来也更加灵活，EventBus 中的
Subscriber 不需要继承任何类或者实现任何接口，在使用 EventBus 时只需要持有 Bus 的引用即可。