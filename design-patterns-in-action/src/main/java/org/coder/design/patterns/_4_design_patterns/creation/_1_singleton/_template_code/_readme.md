### 单例模式的实现方式

#### 饿汉式

1. [饿汉式单例模式](HungrySingleton.java)

#### 懒汉式：懒加载

1. [懒汉式的单例模式](LazySingleton.java)
2. [懒汉式 + 同步方法的单例模式](LazySyncSingleton.java)
3. [Double-Check的单例模式](DoubleCheckSingleton.java)
4. [volatile + Double-Check的单例模式](DoubleCheckSingletonWithVolatile.java)
5. [Holder 方式的单例模式](HolderSingleton.java)
6. 枚举方式的单例
    - [枚举方式的单例模式](EnumSingleton.java)
    - [枚举 + Holder 方式的单例模式](EnumLazySingleton.java)

#### 登记式单例模式：可以被继承

1. 登记式模式可以被继承 (饿单例模式和懒单例模式构造方法都是私有的，因而是不能被继承的);
2. 登记式单例实际上维护的是一组单例类的实例，将这些实例存储到一个Map(登记簿)中，对于已经登记过的单例，则从工厂直接返回，对于没有登记的，则先登记，而后返回。
