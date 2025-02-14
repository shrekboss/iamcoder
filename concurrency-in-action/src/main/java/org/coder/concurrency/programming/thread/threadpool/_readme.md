### 实现步骤

1. [定义线程池基本操作和方法](ThreadPool.java)
2. [用任务队列，主要用户缓存提交到线程池中的任务](RunnableQueue.java)
3. [提供创建线程的接口](ThreadFactory.java)
4. [拒绝策略](DenyPolicy.java)：该接口定义了三种默认的实现
5. [RunnableDenyException.java](RunnableDenyException.java)：RunnableDenyException 是 RuntimeException 的子类，主要用于通知
   任务提交者，任务队列已无法再接受新的任务
6. [任务提交者](InternalTask.java)：是 Runnable 的一个实现，主要用户线程池内部，该类会使用到 RunnableQueue，然后不断地从
   queue 中取出
   某个 runnable，并运行 runnable 的 run 方法
7. [RunnableQueue 队列的实现](LinkedRunnableQueue.java)
8. [ThreadPool 的基础实现](BasicThreadPool.java)
9. [自定义线程池测试测试类](ThreadPoolTest.java)

### 存在的问题：

1. BasicThreadPool 和 Thread 不应该是继承关系，采用组合关系更为妥当，这样就可以避免调用者直接使用 BasicThreadPool 中的
   Thread 的方法
2. 线程池的销毁功能并未返回未处理的任务，这样会导致未处理的任务被丢弃
3. BasicThreadPool 的构造函数太多，创建不太方便，建议采用 Builder 和设计模式对其进行封装或者提供工厂方法进行构造
4. 线程池中的数量控制没有进行合法性校验，比如 initSize 数量不应该大于 maxSize 数量