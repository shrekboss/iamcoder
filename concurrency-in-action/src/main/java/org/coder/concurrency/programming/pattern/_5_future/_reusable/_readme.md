## Future 设计模式的可复用实现代码

JDK 1.5 开始已提供的 java.util.concurrent.Future 可以看成是 Promise 模式中 Promise 参与者的抽象，其声明如下：
`public interface Future<V`

该接口的类型参数 V 相当于 Promise 模式中的 Result 参与者。该接口定义的方法及其与 promise 参与者相关方法之间的对应关系如下所示：

| 接口 java.util.concurrent.Future 的方法 | Promise 参与者方法 | 功能           |
|------------------------------------|---------------|--------------|
| get()                              | getResult()   | 获取异步任务的执行结果  |
| isDone()                           | isDone()      | 检查异步任务是否执行完毕 |

接口 java.util.concurrent.Future 的实现类 java.util.concurrent.FutureTask 可以看成 Promise 模式的 Promise 参与者实例。

