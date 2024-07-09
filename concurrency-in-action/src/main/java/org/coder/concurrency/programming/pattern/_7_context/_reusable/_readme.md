## 线程上下文(Context / Thread Specific Storage)设计模式的可复用的实现代码

ThreadLocal 类相当于 Thread Specific Storage 模式中的 TSObjectProxy 参与者。其类型参数 T 相当于 TSObject 参与者。

1. 【必需】创建 ThreadLocal 的子类(或者匿名类)。
2. 【可选，但通常是必需要的】在 ThreadLocal 的子类中覆盖其父类的 initialValue 方法，用于定义初始的线程特有对象实例。

需要注意的是，类型为 ThreadLocal 的变量，其声明通常采用 static final 修饰。不同的线程采用同一个 ThreadLocal
实例即可获取所需的线程特有对象实例，因此类型为 ThreadLocal 的变量定义为类变量(用 static 修饰)即可，而无须定义为实例变量(
不使用 static 修饰)。