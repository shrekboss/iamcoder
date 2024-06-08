## 单例不支持有参数的构造函数

> 比如创建一个连接池的单例对象，没法通过参数来指定连接池的大小。针对这个问题，来看下都有哪些解决方案

### 第一种解决思路

> 创建完实例之后，先调用 init() 函数传递参数，最后才使用。需要注意的是，我们在使用这个单例类的时候，要先调用 init() 方法，然后才能调用
> getInstance() 方法，否则代码会抛出异常。
> 参考代码：[SingletonSolution1.java](SingletonSolution1.java)

### 第二种解决思路

> 将参数放到 getInstance() 方法中。
> 参考代码：[SingletonSolution2.java](SingletonSolution2.java)

- 如下两次执行 getInstance() 方法，那获取到的 singleton1 和 signleton2

```java 
Singleton singleton1 = Singleton.getInstance(10, 50);
Singleton singleton2 = Singleton.getInstance(20, 30);
```

### 第三种解决思路

> 将参数放到另外一个全局变量中。
> Config 是一个存储了 paramA 和 paramB 值的全局变量。里面的值既可以像下面的代码那样通过静态常量来定义，也可以从配置文件中加载得到。实际上，这种方式是最值得推荐的。
> [SingletonSolution3.java](SingletonSolution3.java)

## 有何替代解决方案？

### 静态方法

> 为了保证全局唯一，除了使用单例，我们还可以用静态方法来实现。
> 静态方法这种实现思路，并不能解决之前提到的问题。
> 参考代码：[ReplaceSingletonSolution1.java](ReplaceSingletonSolution1.java)

### 依赖注入

> 基于新的使用方式，我们将单例生成的对象，作为参数传递给函数（也可以通过构造函数传递给类的成员变量）。
> 参考代码：[ReplaceSingletonSolution2.java](ReplaceSingletonSolution2.java)
> - 可以解决单例隐藏类之间依赖关系的问题。
> - 不过，对于单例存在的其他问题，比如对 OOP 特性、扩展性、可测性不友好等问题，还是无法解决。

> 所以，如果要完全解决这些问题，我们可能要从根上，寻找其他方式来实现全局唯一类。

- 实际上，类对象的全局唯一性可以通过多种不同的方式来保证。
    - 既可以通过单例模式来强制保证;
    - 也可以通过工厂模式、IOC 容器（比如 Spring IOC 容器）来保证
    - 还可以通过程序员自己来保证（自己在编写代码的时候自己保证不要创建两个类对象）。这就类似 Java 中内存对象的释放由 JVM
      来负责，而 C++ 中由程序员自己负责，道理是一样的。
