## Google Guava 的不变集合类

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