### 模拟实现 EventBus
> [Google Guava EventBus 的源码](https://github.com/google/guava)
- [Subscribe：标明观察者中的哪个函数可以接收消息](Subscribe.java)
- [ObserverAction：类用来表示 @Subscribe 注解的方法](AsyncEventBus.java)
  - target 表示观察者类
  - method 表示方法
- [ObserverRegistry：Observer 注册表](ObserverRegistry.java)
  - 一个比较有技巧的地方是 CopyOnWriteArraySet 的使用
  - CopyOnWriteArraySet，顾名思义，在写入数据的时候，会创建一个新的 set，并且将原始数据 clone 到新的 set 中，在新的 set 中写入数据完成之后，再用新的 set 替换老的 set。这样就能保证在写入数据的时候，不影响数据的读取操作，以此来解决读写并发问题。
  - 除此之外，CopyOnWriteSet 还通过加锁的方式，避免了并发写冲突。具体的作用你可以去查看一下 CopyOnWriteSet 类的源码，一目了然。
- [EventBus：阻塞同步的观察者模式](EventBus.java)
  - 实际上，MoreExecutors.directExecutor() 是 Google Guava 提供的工具类，看似是多线程，实际上是单线程。
  - 之所以要这么实现，主要还是为了跟 AsyncEventBus 统一代码逻辑，做到代码复用
- [ObserverAction：为了实现异步非阻塞的观察者模式](ObserverAction.java)
