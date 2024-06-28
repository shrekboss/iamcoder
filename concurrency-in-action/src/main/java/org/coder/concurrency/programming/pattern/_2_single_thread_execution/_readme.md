## Single Thread Execution 设计模式

> 所谓线程安全的类是指多个线程对某个类的实力同时进行操作时，不会引起数据不一致的问题，反之则是非线程安全的类，在线程安全的类中经常会看到
> Synchronized 关键字的身影。

### [机场安检](flight_security)

参考代码：

- [FlightSecurity.java](flight_security%2FFlightSecurity.java)
- [FlightSecurityTest.java](flight_security%2FFlightSecurityTest.java)

### [吃面条问题](eat_noodle_problem)

吃面引起的死锁问题

参考代码如下：

- 会出现死锁：
    - [Tableware.java](eat_noodle_problem%2FTableware.java)
    - [EatNoodleThread.java](eat_noodle_problem%2FEatNoodleThread.java)

- 解决死锁：
    - [EatNoodleThread1.java](eat_noodle_problem%2FEatNoodleThread1.java)
    - [TablewarePair.java](eat_noodle_problem%2FTablewarePair.java)

将某个类设计成线程安全的类，用 Single Thread Execution 控制是其中的方法之一，但是子类如果继承了线程安全的类并且打破了
Single Thread Execution 的方法，就会破坏方法的安全性，这种情况一般称为继承异常(inheritance anomaly)。

