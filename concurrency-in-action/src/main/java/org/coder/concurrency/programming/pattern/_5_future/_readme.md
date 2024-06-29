## Future 设计模式

> 当某个任务运行需要较长的时间时，调佣线程在提交任务之后的徒劳等待对 CPU 资源来说是一种浪费，在等待的这段时间里，完全可以进行其他任务的执行，这种场景完全符合
> Future 设计模式的应用。

- 可以增强点：
    - 将提交的任务交给线程池运行。
    - get 方法没有超时功能，如果获取一个计算结果在规定的时间内没有返回，则可以抛出异常通知调用线程。
    - Future 未提供 Cancel 功能，当任务提交之后还可以对其进行取消。
    - 任务运行时出错未提供回调方式。

参考代码：

- [Future.java](Future.java)
    - [FutureTask.java](FutureTask.java)
- [FutureService.java](FutureService.java)
    - [FutureServiceImpl.java](FutureServiceImpl.java)
- [Callback.java](Callback.java)
- [Task.java](Task.java)
- [FutureTest.java](FutureTest.java)
