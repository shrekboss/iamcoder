## 线程上下文(Context)设计模式

参考代码：

- [ActionContext.java](ActionContext.java)
- [ApplicationContext.java](ApplicationContext.java)
- [ActionContextExample.java](ActionContextExample.java)
- [ThreadLocalExample.java](ThreadLocalExample.java)

### ThreadLocal 的使用场景及注意事项

> ThreadLocal 又被称之为“线程保险箱”，ThreadLocal 能够将指定的变量和当前线程进行绑定，线程之间彼此隔离，持有不同的对象实例，从而避免了数据资源的竞争。

- 在进行对象跨层传递的时候，可以考虑使用 ThreadLocal，避免方法多次传递，打破层次的约束；
- 线程间数据隔离
- 进行事务操作，用于存储线程事务信息。

### ThreadLocal 详解

参考：《Java 高并发编程详解-多线程与架构设计》P264

### ThreadLocal 的内存泄露问题分析

> 当 ThreadLocal 被显示地指定为 null 之后，执行 GC操作，此时堆内存中的 ThreadLocal 被回收，同时 ThreadLocalMap 中的Entry.key
> 也称为 null，但是 value 将不会被释放，除非当前线程已经结束了生命周期的 Thread
> 引用被垃圾回收器回收。
>
> 内存泄漏和内存溢出是有区别的，内存泄露时导致内存溢出的原因之一，但两者并不是完全等价的，内存泄露更多的是程序中不再持有某个对象的引用，但是该对象仍然无法被垃圾回收器回收，究其原因是因为该对象到引用根
> Root 的链路是可达的，比如 ThreadRef 到 Entry.Value 的引用链路。

参考：《Java 高并发编程详解-多线程与架构设计》P270