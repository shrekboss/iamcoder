## 不可变对象设计模式

> 所谓共享的资源，是指多个线程同时对其进行访问的情况下，各线程都会使其发生变化，而线程安全性的主要目的就在于受控的并发访问中防止数据发生变化。
>
> 无论是 Synchronized 关键字还是显示锁 Lock，都会牺牲系统的性能。
>
> 不可变对象最核心的地方在于不给外部修改共享资源的机会，这样就会避免多线程情况下的数据冲突而导致的数据不一致的情况，又能避免因为对锁的依赖而带来的性能降低。

### 累加器

参考代码：

- [IntegerAccumulator.java](accumulator%2FIntegerAccumulator.java)
- [IntegerAccumulator2.java](accumulator%2FIntegerAccumulator2.java)
- [IntegerAccumulator3.java](accumulator%2FIntegerAccumulator3.java)
    - final 修饰其的目的是为了防止由于继承重写而导致失去线程安全性
    - 另外 init 属性被 final 修饰不允许线程对其进行改变，在构造函数中赋值后将不会再改变
    - add 方法并未在原有 init 的基础上进行累加，而是创建了一个全新的 IntegerAccumulator，并未提供任何修改原始
      IntegerAccumulator 的机会，运行程序不会出现 ERROR 的情况。

### Google Guava 的不变集合类

> Google Guava 针对集合类（Collection、List、Set、Map…）提供了对应的不变集合类（ImmutableCollection、ImmutableList、ImmutableSet、ImmutableMap…）和
> Java JDK 提供了不变集合类（UnmodifiableCollection、UnmodifiableList、UnmodifiableSet、UnmodifiableMap…）有什么区别？

参考代码：

- [ImmutableDemo.java](ImmutableDemo.java)

- 不变模式可以分为两类
    - 一类是普通不变模式
        - [User.java](general_immutable_pattern%2FUser.java)
        - [Address.java](general_immutable_pattern%2FAddress.java)
    - 另一类是深度不变模式（Deeply Immutable Pattern）
        - [User.java](deeply_immutable_pattern%2FUser.java)
        - [Address.java](deeply_immutable_pattern%2FAddress.java)

### 总结

> 设计一个不可变的类共享资源需要具备不可破坏性，比如使用 final 修饰，另外针对共享资源操作的方法是不允许被重写的，以防止由于继承而带来的安全性问题。

但是单凭这两点也补足以保证一个类是不可变的。参考代码：

- [Immutable.java](Immutable.java)

以为 getList 方法返回的 list 是可被其他线程修改的，如果想要使其真正的不可变，则需要再返回 list 的时候增加不可修改的约束
Collections.unmodifiableList(this.list) 或者克隆一个全新的 list 返回。