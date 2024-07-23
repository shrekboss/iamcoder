## 判等问题：程序里如何确定你就是你？

### 1. 注意equals和==的区别

参考代码：[intandstringequal](intandstringequal)

- 对基本类型，比如 int、long，进行判等，只能使用 ==，比较的是直接值。因为基本类型的值就是其数值。
- 对引用类型，比如 Integer、Long 和 String，进行判等，需要使用 equals 进行内容判等。因为引用类型的直接值是指针，使用 ==
  的话，比较的是指针，也就是两个对象在内存中的地址，即比较它们是不是同一个对象，而不是比较对象的内容。
- 比较值的内容，除了基本类型只能使用 == 外，其他类型都需要使用 equals。
- 虽然使用 new 声明的字符串调用 intern 方法，也可以让字符串进行驻留，但在业务代码中滥用 intern，可能会产生性能问题。没事别轻易用
  intern，如果要用一定要注意控制驻留的字符串的数量，并留意常量表的各项指标。
- 设置 JVM 参数 -XX:+PrintStringTableStatistic，程序退出时可以打印出字符串常量表的统计信息

### 2. 实现一个equals没有这么简单

参考代码：[equalitymethod](equalitymethod)

实现一个 equals 应该注意的点：

- 考虑到性能，可以先进行指针判等，如果对象是同一个那么直接返回 true；
- 需要对另一方进行判空，空对象和自身进行比较，结果一定是 fasle；
- 需要判断两个对象的类型，如果类型都不同，那么直接返回 false；
- 确保类型相同的情况下再进行类型强制转换，然后逐一判断所有字段。

### 3. hashCode和equals要配对实现

参考代码：[equalitymethod](equalitymethod)

使用 getClass 和 instanceof 这两种方案都是可以判断对象类型的。它们的区别就是，getClass 限制了这两个对象只能属于同一个类，而
instanceof 却允许两个对象是同一个类或其子类。正是因为这种区别，不同的人对这两种方案有不同的喜好，争论也很多。在我看来，你只需要根据自己的要求去选择。补充说明一下，Lombok
使用的是 instanceof 的方案。

- HashSet 基于 HashMap，数据结构是哈希表。所以，HashSet 的 contains 方法，其实就是根据 hashcode 和 equals 去判断相等的。
- TreeSet 基于 TreeMap，数据结构是红黑树。所以，TreeSet 的 contains 方法，其实就是根据 compareTo 去判断相等的。

### 4. 注意compareTo和equals的逻辑一致性

参考代码：[compareto](compareto)

对于自定义的类型，如果要实现 Comparable，请记得 equals、hashCode、compareTo 三者逻辑一致。

### 5. 小心Lombok生成代码的“坑”

参考代码：[lombokequals](lombokequals)

- Lombok 的 @Data 注解会帮我们实现 equals 和 hashcode 方法，但是有继承关系时，Lombok 自动生成的方法可能就不是我们期望的了。

- @EqualsAndHashCode 注解实现 equals 和 hashCode 的时候，默认使用类型所有非 static、非 transient 的字段，且不考虑父类
    - @EqualsAndHashCode.Exclude 排除一些字段
    - 手动设置：@EqualsAndHashCode(callSuper = true) 来让子类的 equals 和 hashCode 调用父类的相应方法。

### 6. 不同类加载器加载相同类的坑

参考代码：[differentclassloaderequals](differentclassloaderequals)

> equals比较的对象除了所谓的相等外，还有一个非常重要的因素，就是该对象的类加载器也必须是同一个，
> 不然equals返回的肯定是false；之前遇到过一个坑：重启后，两个对象相等，结果是true，但是修改了某些
> 东西后，热加载（不用重启即可生效）后，再次执行equals，返回就是false，因为热加载使用的类加载器和
> 程序正常启动的类加载器不同。
>
> 关于类加载器部分，JDK 9 之前的 Java 应用都是由「启动类加载器」、「扩展类加载器」、
> 「应用程序类加载器」这三种类加载器互相配合来完成加载的，如果有需要还可以加入自定义的类加载器来
> 进行拓展；JDK 9 为了模块化的支持，对双亲委派模式做了一些改动：扩展类加载器被平台类加载
> 器（Platform ClassLoader）取代。平台类加载器和应用程序类加载器都不再继承自
> java.net.URLClassLoader，而是继承于 jdk.internal.loader.BuiltinClassLoader。具体细节可以自行搜索。