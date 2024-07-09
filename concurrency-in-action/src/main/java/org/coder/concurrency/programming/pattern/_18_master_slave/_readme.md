## Master-Slave(主仆) 设计模式

### Master-Slave 模式简介

> Master-Slave 模式是一个基于分而治之(Divide and conquer)思想的设计模式。其核心思想是将一个任务(原始任务)
> 分解为若干个语义等同(Semantically-identical)
> 的子任务，并由专门的工作者线程并行执行这些子任务，原始任务的处理结果是通过整合各个子任务的处理结果而形成的。而这些与分而治之相关的处理细节对于原始任务的提交方来说是不可见的。因此，Master-Slave
> 模式既是提高计算效率，有实现了信息隐藏。

### Master-Slave 模式的评价与实现考量

应用场景：

- 并行计算 - Parallel Computation
- 容错处理 - Fault Tolerance
    - 原始任务的处理结果是任意一个 Slave 实例的成功处理结果(那些处理失败的 Slave 实例无法为 Master 返回结果)。
- 计算精度 - Computational Accuracy

上述 3 种场景，只有第 1 种场景中所有 Slave 参与者实例都是用了一个实现类，而后面两种场景下，不同 Slave
参与者实例对应着不同的实现类。此时，为了保证 Slave 参与者实例在数量、类型上如果有变动对 Master 可能产生的影响最小，需要在
Slave 参与者中使用 Strategy 模式，使得所有的 Slave 实现类都有一个共同的接口。

Master-Slave 模式能够带来以下几个好处：

- 可交换性(Exchangeability)和可扩展性(Extensibility)。
- 提升计算性能。

#### 子任务的处理结果的收集

- 使用存储仓库(Repository)。
    - 所谓的存储仓库是一个 Master 参与者和 Slave 参与者都能够访问的数据结构。
- Promise 模式。
    - 使 Slave 参与者的 subService 方法的返回值为 Promise 模式的 Promise 参与者实例。通常可以使 subService 方法的返回值为一个
      java.util.concurrent.Future 实例。这样，Master 参与者可以通过调用各个 Slave 参与者实例的 subService 方法的返回值的
      get() 方法来获取子任务的处理结果。

#### Slave 参与者实例的负载均衡与工作窃取

Master-Slave 模式可以看成 Producer-Consumer 模式的一个实例。Master-Slave 模式的 Master 参与者和 Slave 参与者分别相当于
Producer-Consumer 模式中的 Producer 参与者和 Consumer 参与者。因此，从 Slave 实例运行的角度来看，可以使用工作窃取算法来动态调整各个
Slave 实例的计算负载。

#### 可靠性与异常处理

基于 Promise 模式的方法来获取子任务的处理结果，那么获取 Slave 参与者实例的处理异常就变得十分简单：Master 参与者实例调用
subService 返回值的 get() 方法获取子任务处理结果的时候，如果 get() 方法抛出异常则说明相应的子任务处理失败。

为了提高计算的可靠性，Master 参与者在侦测到上述异常时可以考虑由其自身重新执行处理失败的子任务，即让处理失败的子任务运行在客户端线程中，而不是在
Slave 参与者的工作线程中。

参考代码：[ExceptionHandlingExample.java](ExceptionHandlingExample.java)

#### Slave 线程停止

Two-phase Termination 模式