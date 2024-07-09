## 线程上下文(Context / Thread Specific Storage)设计模式

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

### Thread Specific Storage 模式简介

> 多线程相关的问题如线程安全、死锁等归根究底是多线程共享变量导致的。
>
> 使用线程对象的私有变量这种方法可以看成是 Thread Specific Storage 模式的最小实现。但是，这种方法的通用性不够。毕竟代码之间调用关系并不总是这种
> run 方法去访问线程的私有变量。

参考代码：[ThreadPrivateMember.java](ThreadPrivateMember.java)

### Thread Specific Storage 模式实战案例解析

> 某系统需要支持验证码短信功能。该系统的用户进行一些重要操作的时候，该系统会生成一个验证码，并将其通过短信发送给用。

验证码的生成需要使用 java.security.SecureRandom 这种强随机生成器，而非 java.math.Random 这种伪随机生成器。但是
SecureRandom 可能涉及以下几个问题。

- SecureRandom 实例的初始化(主要是初始化种子)可能比较耗时。
- SecureRandom 用于生成随机整数的 nextInt 方法最终会调用一个由 SecureRandom 自身定义的 synchronized 方法。这意味着，nextInt
  方法的调用实际上会涉及锁。
- SecureRandom 是线程安全的，借用 Thread Specific Storage 模式：让每个需要生成验证码的线程生成一个且仅一个 SecureRandom
  实例。

参考代码：

- [ThreadSpecificSecureRandom.java](mmsc%2FThreadSpecificSecureRandom.java)
- [SmsVerficationCodeSender.java](mmsc%2FSmsVerficationCodeSender.java)

### Thread Specific Storage 模式的评价与实现

> Thread Specific Storage 模式提升了计算效率。Thread Specific Storage 使得可以在不使用锁的情况下实现线程安全，从而避免了锁的开销以及锁带来的相关问题，如上下文切换、死锁等。

Thread Specific Storage 模式的常见使用场景包括以下几个：

- 需要使用非线程安全对象，但又不希望使用锁。
    - 参考代码：[ThreadSpecificDateFormat.java](ThreadSpecificDateFormat.java)
- 使用线程安全对象，但希望避免其使用的锁的开销和相关问题。
- 隐式参数传递
    - 即一个类的方法调用另一个类的方法时，前者向后者传递数据可以借助 ThreadLocal 而不必通过方法参数传递。
    - 参考代码：[ImplicitParameterPassing.java](ImplicitParameterPassing.java)
- 特定于线程的单例
    - 广为使用的单例模式所实现的是，对于一个 JVM 中的一个类加载器而言，某个类有且仅有一个实例。如果对于某个类，希望每个线程有且仅有该类的一个实例，那么就可以使用
      Thread Specific Storage 模式。

在线程池环境下使用线程特有对象需要考虑在适当的时间和地方清理线程特有对象，以便同一个工作者线程在处理其他任务的时候不会产生数据错乱。清理线程特有对象只需要调用获取相应线程特有对象实例时所用的
ThreadLocal 实例的 remove 方法即可。

另一方面，线程池环境下使用 Thread Specific Storage 模式也不一定就会产生数据错乱。只不过，该案例涉及的线程特有对象
ThreadSpecificSecureRandom 是用于生成随机数字的，客户端代码只关心其生成的数字是否是随机的，而并不关心具体是哪个
ThreadSpecificSecureRandom 实例负责产生随机数字。因此，这种情形下，即便是在线程池环境中，使用 Thread Specific Storage
模式也不会产生数据错乱问题。

### 内存泄露与伪内存泄露

> 内存泄露：Memory Leak。指由于对象永远无法被垃圾回收导致其占用的 JVM 内存无法被释放。持续的内存泄露会导致 JVM
> 可用内存逐渐减少，并最终可能导致 JVM 内存溢出(Out of Memory)直到 JVM 宕机。
>
> 伪内存泄露：Memory Pseudo-leak。类似内存泄露。所不同的是，伪内存泄露中对象所占用的内存在其不再被使用后的相当长的时间仍然无法被回收，甚至可能永远无法被回收。

对于某个 ThreadLocal 实例而言，如果在某一个时间段内该实例除了 ThreadLocalMap 条目对其有可达的引用(Reachable Reference)
外，没有其他可达的引用，那么垃圾回收器就可以将该 ThreadLocal 实例回收。此时，先前引用该 ThreadLocal 实例的 ThreadLocalMap
条目由于 Key 的值为 null，就变成一条无效的条目(Stale Entry)。这种无效的条目(即 Key 值为 null，Value 值指向一个线程特有对象)
在其所属的 ThreadLocalMap 有新增条目时可能被删除掉。如果某个线程引用的 ThreadLocalMap 实例产生无效条目后，某段时间内该线程处于非运行状态，则该线程引用的
ThreadLocalMap 实例就没有新增的条目，因此其所有的无效条目在该时间段内也无法被删除。如果该线程一直处于非运行状态，则该线程引用的
ThreadLocalMap 实例的无效条目永远无法被删除。所以，这种情形会导致伪内存泄露。

**某个类的实例会持有对该类(类本身也是一个对象)的引用。而类对象又会持有对其进行加载的类加载器对象的引用。Java
中的类加载器会持有对其所有加载的所有类的引用。**

参考代码：

- [MemoryLeakingServlet.java](memoryleak%2FMemoryLeakingServlet.java)
- [MemoryPseudoLeakingServlet.java](memoryleak%2FMemoryPseudoLeakingServlet.java)
- [MemoryLeakingServlet.java](memoryleak%2FMemoryLeakingServlet.java)
- [ManagedThreadLocal.java](ManagedThreadLocal.java)