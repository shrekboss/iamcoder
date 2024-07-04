## Two-phase Termination 设计模式的可复用实现代码

- [AbstractTerminatableThread.java](..%2Falarm%2FAbstractTerminatableThread.java)
- [TerminationToken.java](..%2Falarm%2FTerminationToken.java)

在此基础上，应用代码只需要在定义 AbstractTerminatableThread 的子类(或匿名类)时实现 doRun
方法，在该方法中实现线程的处理逻辑。另外，应用代码如果需要在目标线程处理完待处理的任务后再停止，则需要注意 TerminationToken
实例的 reservations 属性值的增加和减少。