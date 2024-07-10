## Worker-Thread(Pipeline) 设计模式

> Worker-Thread 设计模式有时也称为流水线设计模式。需要有如下几个角色：
> - 流水线工人：主要用来对传送带上的产品进行加工。
> - 流水线传送带：用来传送来自上游的产品。
> - 产品组装说明书：用来说明该产品如何组装。

### Worker-Thread VS. Producer-Consumer

Producer、Consumer 对 Queue 都是依赖关系，其次 Producer 要做的就是不断地往 Queue 生产数据，而 Consumer 则是不断地从 Queue
中获取数据，Queue 既不知道 Producer 的存在也不知道 Consumer 的存在，最后 Consumer 对Queue
中数据的消费不依赖数据本身的方法(使用说明书)。

传送带上游的线程，同样的不断地往传送带(Queue)中生产数据，而当 Channel 被启动的时候，就会同时创建并启动若干数量的 Worker
线程，因此可以看出 Worker 与 Channel 来说不是单纯的依赖关系，而是聚合关系，Channel 必须知道 Worker 的存在。

### Pipeline 模式的简介

> 核心思想：将一个任务处理分解为若干个处理阶段(stage)
>
，其中每个处理阶段的输出作为下一个处理阶段的输入，并且各个处理阶段都有相应的工作者线程去执行相应的计算。因此，处理一批任务时，各个任务的各个处理阶段是并行(
> Parallel)的。
>
> - 处理阶段 1 的工作者线程执行 T1 相应的计算后，其处理结果会被提交给处理阶段 2 作为输入；
> - 当处理阶段 2 的工作者线程正在执行任务 T1 的相应计算式，处理阶段 1 正在执行任务 T2 相应的计算，此时这两个处理阶段是并行的。

### Pipeline 模式的评价与实现考量

> Pipeline 模式可以对有依赖关系的任务实现并行处理。从个体任务上看 Pipeline 模式对各个任务的处理是顺序的。由于每个 Pipe
> 实例都有其工作者线程负责任务处理，当一个 Pipe 实例处理其上游 Pipe 实例提交的某个任务时，其上游 Pipe 实例已经在处理其接收到的其他任务。

Pipeline 模式非常便于采用单线程模型实现对子任务的处理。比如，子任务的处理涉及非线程安全或者涉及阻塞 I/O
操作时，不希望引入锁从而避免其增加上下文切换。Pipeline 模式适合处理规模较大的任务。

#### Pipeline 的深度

> 一个 Pipeline 实例包含的 Pipe 实例的个数被称为 Pipeline 的深度。
> - 如果当前 Pipeline 实例所处理的任务多属 CPU 密集型，那么 Pipeline 的深度最好不超过 N(CPU 个数)。
> - 如果当前 Pipeline 实例所处理的任务多属 I/O 密集型，那么 Pipeline 的深度最好不超过 2N(CPU 个数)。

#### 基于线程池的 Pipe

> 需要注意各个 Pipe 实例给线程池提交的任务之间是否存在依赖关系。存在依赖关系，则可能导致线程池死锁。

参考代码：[ThreadPoolBasedPipeExample.java](ThreadPoolBasedPipeExample.java)

#### 错误处理

- 各个 Pipe 实例捕获到异常后调用 PipeContext 实例的 handleError 进行错误处理。
- 创建一个专门负责错误处理的 Pipe 实例，其他 Pipe 实例捕获异常后提交相关数据给该 Pipe 实例处理。

#### 可配置的 Pipeline