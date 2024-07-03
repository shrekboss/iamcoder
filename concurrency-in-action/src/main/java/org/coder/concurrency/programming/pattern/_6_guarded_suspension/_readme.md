## Guarded Suspension 设计模式

> Suspension 是“挂起”、“暂停”的意思，而 Guarded 则是“担保”的意思，连在一起就是确保挂起。当线程在访问某个对象时，发现条件不满足，就暂时挂起等待条件满足时再次访问，这一点和
> Balking 设计模式刚好相反(Balking 在遇到条件不满足时会放弃)。
>
> Guarded Suspension 设计模式是很多设计模式的基础，比如生产者消费者模式，Worker Thread 设计模式，等等，同样 Java 并发包中的
> BlockingQueue 中也大量使用到了 Guarded Suspension 设计模式。Guarded Suspension 的关注点在于临界值的条件是否满足，当达到设置的临界值时相线程会被挂起。

参考代码：

- [GuardedSuspensionQueue.java](GuardedSuspensionQueue.java)

### 模式简介

> 多线程编程中，为了提高并发性，往往将一个任务分解为不同的部分，将其交由不同的线程来执行。这些线程间相互写作时，仍然可能出现一个线程去等待另外
> 有几个线程完成一定的操作，其自身才能继续运行的情形。该模式可以帮助我们解决上述等待问题。该模式的核心思想是如果某个线程执行特定的操作之前需要满
> 足一定的条件，则在该条件为满足时将该线程暂停运行(即暂挂线程，使其处于等待状态，直到条件满足时才继续改线程的运行)。
>
> 的确，wait/notify 可以用来实现 Guarded Suspension 模式。但是，Guarded Suspension 模式还要解决 wait/notify 所解决的问题之外的问题。
>
> Guarded Suspension 模式的核心是一个受保护方法(Guarded Method)。

参考代码：

- [AlarmAgent.java](alarm%2FAlarmAgent.java)
- [Predicate.java](alarm%2FPredicate.java)
- [GuardedAction.java](alarm%2FGuardedAction.java)
- [Blocker.java](alarm%2FBlocker.java)
    - [ConditionVarBlocker.java](alarm%2FConditionVarBlocker.java)

### 模式的评价与实现考量

> 关注点分离(Separation of Concern)。 Guarded Suspension
> 模式中的各个参与者各自仅关注本模式所要解决的问题中的一个方面，各个参与者的职责是高度内聚(Cohesive)的。应用开发人员只需要根据应用的需要实现
> GuardedObject、ConcretePredicate 和 ConcreteGuardedAction 这几个必须由应用实现的参与者即可，而其他的参与者的实现都是可以复用的。

可能增加 JVM 垃圾回收的负担。

为了是 GuardedAction 实例的 call 方法能够访问保护方法 guardedMethod 的参数，需要用闭包(
Closure)。因此，GuardedAction 实例可能是在保护方法中创建的。这意味着，每次保护方法被调用的时候都会有个新的 GuardedAction
实例被创建。而这会增加 JVM 内存池 Eden 区域内存的占用，从而可能增加 JVM 垃圾回收的负担。如果应用程序所在 JVM 的内存池 Eden
区域空间比较小，则需要特别注意 GuardedAction 实例创建可能导致的垃圾回收负担。

可能增加上下文切换(Context Switch)。

严格来说，这点与 Guarded Suspension 模式本身无关。只不过，不管如何实现 Guarded Suspension
模式，只要这里面涉及线程的暂挂和唤醒就会引起上下文切换。过于频繁的上下文切换会过多消耗系统的 CPU 时间，从而降低系统处理能力。

Blocker 实现类中封装的几个易错的重要技术细节。

- 内存可见性和锁泄露(Lock Leak)
    - 保护条件中涉及的变量牵涉读线程和写线程进行共享访问。保护方法的执行是读线程，它读取这些变量以判断保护条件是否成立。而写线程是受保护对象实现的
      stateChanged 方法的执行线程，它会去改变这些变量的值。因此，对保护条件设计的变量的访问应该使用锁进行保护，以保证写线程对这些变量所做的更改，读线程能够“看到”相应的值。
    - ConditionVarBlocker 类 为了保证保护条件中涉及的变量的内存可见性而引入 ReentrantLock
      锁。使用该锁时需要注意临界区中的代码无论是执行正常还是出现异常，进入临界区前获取的锁实例都应该被释放。否则，就会出现锁泄露现象：锁对象被某个线程获得，但是永远不会被该线程释放，导致其他线程无法获得该所。为了避免锁泄露，使用
      ReentrantLock 的临界区代码总是需要按照如下格式来编写：
        ```
            lock.lockInterruptibly();
            try {
                // 临界区代码
            } finally{
                // 在 finally 块中释放锁，保证锁总是会被释放的
                lcok.unlock();
            }     
        ```
- 现成过早被唤醒
    - ConditionVarBlocker 类的 callWithGuard 方法对 Condition 实例的 await 方法是放在一个 while 循环中的，而不是 if 语句中。
      ```
          lock.lockInterruptibly();
          try {
              while (保护条件不成立) {
                  condtion.await();
              }
              执行目标动作
          } finally{
              // 在 finally 块中释放锁，保证锁总是会被释放的
              lcok.unlock();
          }     
      ```
- 嵌套监视器锁死
  - 
  - 参考代码：[GuardedSuspensionQueue.java](GuardedSuspensionQueue.java)
