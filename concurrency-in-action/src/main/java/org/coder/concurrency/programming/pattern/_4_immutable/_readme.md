## Immutable Object 设计模式

> 所谓共享的资源，是指多个线程同时对其进行访问的情况下，各线程都会使其发生变化，而线程安全性的主要目的就在于受控的并发访问中防止数据发生变化。
>
> 无论是 Synchronized 关键字还是显示锁 Lock，都会牺牲系统的性能。
>
> 不可变对象最核心的地方在于不给外部修改共享资源的机会，这样就会避免多线程情况下的数据冲突而导致的数据不一致的情况，又能避免因为对锁的依赖而带来的性能降低。

**Immutable Object 模式的意图是通过使用对外可见的状态不可变的对象(即 Immutable Object)，使得共享对象“天生”具有线程安全性，而无需额外地同步访问控制。**

所谓状态不可变对象，即对象一经创建，其对外可见的状态就保持不变。

- **一个严格意义上不可变对象要满足一下所有条件**
    - 类本身使用 final 修饰：防止其子类改变其定义的行为。
    - 所有字段都是用 final 修饰：使用 final 修饰不仅仅是从语义上说明被修饰字段的引用不可改变。更重要的语义在多线程环境下由
      JMM 保证了被修饰字段所引用对象的初始化安全，即 final 修饰的字段在其他线程可见时，它必须是初始化完成的。相反，非 final
      修饰的字段由于缺少这种保证，肯能导致一个线程“看到”一个字段的时候，它还未被初始化完成，从而可能导致一些不可预料的结果。
    - 在对象的创建过程中，this 关键字没有泄露给其他类：防止其他类(如该类的内部匿名类)在对象创建过程中修改器状态。
    - 任何字段，若其引用了其他状态可变的对象(如集合、数组等)，则这些字段必须是 private
      修饰的，并且这些字段不能对外泄露。若有相关方法要返回这些字段值，应该进行防御性复制(Defensive copy)。

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