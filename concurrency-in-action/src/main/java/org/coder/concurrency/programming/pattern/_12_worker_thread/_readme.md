## Worker-Thread 设计模式

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
