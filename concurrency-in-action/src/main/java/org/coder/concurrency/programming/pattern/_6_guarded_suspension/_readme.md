## Guarded Suspension 设计模式

> Suspension 是“挂起”、“暂停”的意思，而 Guarded 则是“担保”的意思，连在一起就是确保挂起。当线程在访问某个对象时，发现条件不满足，就暂时挂起等待条件满足时再次访问，这一点和
> Balking 设计模式刚好相反(Balking 在遇到条件不满足时会放弃)。
>
> Guarded Suspension 设计模式是很多设计模式的基础，比如生产者消费者模式，Worker Thread 设计模式，等等，同样 Java 并发包中的
> BlockingQueue 中也大量使用到了 Guarded Suspension 设计模式。Guarded Suspension 的关注点在于临界值的条件是否满足，当达到设置的临界值时相线程会被挂起。

参考代码：

- [GuardedSuspensionQueue.java](GuardedSuspensionQueue.java)