场景描述：
> 虽然 Thread 为我们提供了可获取状态，以及判断是否 alive 的方法。但是这些方法是针对线程本身的，而我们提交的任务 Runnable
> 在运行的过程中所处的状态如何是无法直接获取的，比如它什么时候开始，什么时候结束，最不好的一种体验是无法获得 Runnable 任务执行
> 后的结果。一般情况下，想要获得最终结果，不得不为 Thread 或者 Runnable 传人共享变量。但是在多线程的情况下，共享变量将导致资
> 源的竞争从而增加了数据不一致性的安全隐患。

### 通过观察者模式 + 适配器模式解决

> 观察者模式需要有事件源，也就是引发状态改变的源头，很明显 Thread 负责执行任务的逻辑单元，它最清楚这个过程的始末周期，而事件的
> 接收者则是通知接受者一方，严格意义上的观察者模式是需要 Observer 的集合的，这里不需要完全遵守这样的规则，只需要将执行任务的每
> 个阶段都通知给观察者即可。

### 程序结构

- [Observable 被观察者接口定义，主要是暴露调用者使用的](Observable.java)
- [事件回调的响应者](TaskLifecycle.java)
- [被观察者](ObservableThread.java)
- [任务执行接口](Task.java)
- [测试入口](ObservableThreadTest.java)

### 关键点总结

1. Observable 中定义和 Thread 同样的方法用于屏蔽 Thread 的 API。
2. ObservableThread 中的 run 方法修饰为 final，或者将 ObservableThread 类修饰为 final，防止子类继承重写，导致整个生命周期的监控时效。
3. ObservableThread 本身的 run 方法充当了事件源的发起者，而 TaskLifecycle 则扮演了事件回调的响应者。