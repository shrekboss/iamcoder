### Serial Thread Confinement 模式的可复用实现代码

参考代码：

- [AbstractSerializer.java](AbstractSerializer.java)
- [TerminatableWorkerThread.java](TerminatableWorkerThread.java)
- [TaskProcessor.java](TaskProcessor.java)

利用可复用代码实现 Serial Thread Confinement 模式，应用程序只需要要完成以下几件事情：

- 【必需】定义 Serializer 提交给 WorkerThread 的任务对应的类型。
- 【必需】定义 AbstractSerializer 的子类，并实现其父类定义的 makeTsk 抽象方法。另外该子类需要定义一个名字含义比 service
  更为具体的服务方法。该方法发可直接调用其父类的 service 方法。
- 【必需】定义 TaskProcessor 接口的实现类
- 
