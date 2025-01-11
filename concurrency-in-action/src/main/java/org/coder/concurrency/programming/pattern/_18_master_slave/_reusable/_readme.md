## Master-Slave(主仆) 模式的可复用实现代码

参考代码：

- [TaskDivideStrategy.java](TaskDivideStrategy.java)
- [SubTaskDispatchStrategy.java](SubTaskDispatchStrategy.java)
    - [RoundRobinSubTaskDispatchStrategy.java](RoundRobinSubTaskDispatchStrategy.java)
- [SlaveSpec.java](SlaveSpec.java)
    - [WorkerThreadSlave.java](WorkerThreadSlave.java)
- [AbstractMaster.java](AbstractMaster.java)
- [SubTaskFailureException.java](SubTaskFailureException.java)
- [RetryInfo.java](RetryInfo.java)
- [ParallelPrimeGenerator.java](ParallelPrimeGenerator.java)

1. 【必需】创建 TaskDivideStrategy 接口实现类，在该类中实现原始任务分解算法。
2. 【必需】创建 AbstractMaster 的子类。该子类除了实现父类定义的几个抽象方法外，还要定义服务方法，该服务方法的名字比其父类的
   service 方法含义更为具体。
3. 【必需】创建 WorkerThreadSlave 的子类。在该子类中实现其父类的 doProcess 方法。当然，也可以自己编写 SlaveSpec 接口的实现类。
4. 【可选】创建 SubTaskDispatchStrategy 的实现类。在该类中实现了子任务派发算法。AbstractMaster 默认使用
   RoundRobinSubTaskDispatchStrategy。

