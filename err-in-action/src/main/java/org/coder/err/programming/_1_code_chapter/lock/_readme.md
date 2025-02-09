## 代码加锁：不要让“锁”事成为烦心事

### 加锁前要清楚锁和被保护的对象是不是一个层面的

> 锁解决问题之前一定要理清楚，我们要保护的是什么逻辑，多线程执行的情况又是怎样的。除了没有分析清线程、业务逻辑和锁三者之间的关系随意添加无效的方法锁外，还有一种比较常见的错误是，没有理清楚锁和要保护的对象是否是一个层面的。
>
> 使用 synchronized 加锁虽然简单，但我们首先要弄清楚共享资源是类还是实例级别的、会被哪些线程操作，synchronized
> 关联的锁对象或方法又是什么范围的。

参考代码：[lockscope](lockscope)

### 加锁要考虑锁的粒度和场景问题

> 加锁尽可能要考虑粒度和场景，锁保护的代码意味着无法进行多线程操作。对于 Web 类型的天然多线程项目，对方法进行大范围加锁会显著降级并发能力，要考虑尽可能地
> 只为必要的代码块加锁，降低锁的粒度；而对于要求超高性能的业务，还要细化考虑锁的读写场景，以及悲观优先还是乐观优先，尽可能针对明确场景精细化加锁方案，可以
> 在适当的场景下考虑使用 ReentrantReadWriteLock、StampedLock 等高级的锁工具类。

在方法上加 synchronized 关键字实现加锁确实简单，也因此我曾看到一些业务代码中几乎所有方法都加了 synchronized，但这种滥用
synchronized 的做法：

- 一是，没必要。通常情况下 60% 的业务代码是三层架构，数据经过无状态的 Controller、Service、Repository 流转到数据库，没必要使用
  synchronized 来保护什么数据。
- 二是，可能会极大地降低性能。使用 Spring 框架时，默认情况下 Controller、Service、Repository 是单例的，加上 synchronized
  会导致整个程序几乎就只能支持单线程，造成极大的性能问题。

即使我们确实有一些共享资源需要保护，也要尽可能降低锁的粒度，仅对必要的代码块甚至是需要保护的资源本身加锁。

如果精细化考虑了锁应用范围后，性能还无法满足需求的话，我们就要考虑另一个维度的粒度问题了，即：区分读写场景以及资源的访问冲突，考虑使用悲观方式的锁还是乐观方式的锁。

- 对于读写比例差异明显的场景，考虑使用 ReentrantReadWriteLock 细化区分读写锁，来提高性能。
- 如果你的 JDK 版本高于 1.8、共享资源的冲突概率也没那么大的话，考虑使用 StampedLock 的乐观读的特性，进一步提高性能。
- JDK 里 ReentrantLock 和 ReentrantReadWriteLock 都提供了公平锁的版本，在没有明确需求的情况下不要轻易开启公平锁特性，在任务很轻的情况下开启公平锁可能会让性能下降上百倍。

参考代码：[lockgranularity](lockgranularity)

### 多把锁要小心死锁问题

核心的业务场景：下单操作需要锁定订单中多个商品的库存，拿到所有商品的锁之后进行下单扣减库存操作，全部操作完成之后释放所有的锁。代码上线后发现，下单失败概率很高，失败后需要用户重新下单，极大影响了用户体验，还影响到了销量。那为什么会有死锁问题呢？

> 购物车添加商品的逻辑，随机添加了三种商品，假设一个购物车中的商品是 item1 和 item2，另一个购物车中的商品是 item2 和
> item1，一个线程先获取到了 item1 的锁，同时另一个线程获取到了 item2 的锁，然后两个线程接下来要分别获取 item2 和 item1
> 的锁，这个时候锁已经被对方获取了，只能相互等待一直到 10 秒超时。
>
> ==> 免死锁的方案很简单，为购物车中的商品排一下序，让所有的线程一定是先获取 item1 的锁然后获取 item2 的锁，就不会有问题了。

- 业务逻辑中有多把锁时要考虑死锁问题，通常的规避方案是，避免无限等待和循环等待；
- 如果业务逻辑中锁的实现比较复杂的话，要仔细看看加锁和释放是否配对，是否有遗漏释放或重复释放的可能性；
    - 强烈建议 Mock 相关外部接口或数据库操作后对应用代码进行压测，通过压测排除锁误用带来的性能问题和死锁问题
- 对于分布式锁要考虑锁自动超时释放了，而业务逻辑却还在进行的情况下，如果别的线线程或进程拿到了相同的锁，可能会导致重复执行。

参考代码：[deadlock](deadlock)

### 加锁和释放没有配对的问题

用一些代码质量工具或代码扫描工具（比如 Sonar）来帮助排查。这个问题在编码阶段就能发现。

### 分布式锁自动释放导致的重复逻辑执行的问题

- 参考 Redisson 的 RedissonLock
  的[锁续期机制](https://github.com/redisson/redisson/blob/e11c1e14ba50bc5938184fb96d9b72782e591df7/redisson/src/main/java/org/redisson/RedissonLock.java#L265)
  。锁续期是每次续一段时间，比如 30 秒，然后 10
  秒执行一次续期。虽然是无限次续期，但即使客户端崩溃了也没关系，不会无限期占用锁，因为崩溃后无法自动续(Watch Dog
  机制线程也就没有了)自然最终会超时。
- 看源码发现，leaseTime 必须是 -1 才会开启 Watch Dog 机制，也就是如果你想开启 Watch Dog 机制必须使用默认的加锁时间为
  30s。如果你自己自定义时间，超过这个时间，锁就会自定释放，并不会延长。
