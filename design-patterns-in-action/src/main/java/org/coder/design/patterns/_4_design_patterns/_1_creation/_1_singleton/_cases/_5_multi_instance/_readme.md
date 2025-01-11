## 如何实现一个多例模式？

“单例”指的是，一个类只能创建一个对象。对应地，“多例”指的就是，一个类可以创建多个对象，但是个数是有限制的。
> 参考代码: [BackendServer.java](BackendServer.java)

对于多例模式，还有一种理解方式：同一类型的只能创建一个对象，不同类型的可以创建多个对象。

```java
import java.util.concurrent.ConcurrentHashMap;

public class Logger {
    private static final ConcurrentHashMap<String, Logger> instances = new ConcurrentHashMap<>();

    private Logger() {
    }

    public static Logger getInstance(String loggerName) {
        instances.putIfAbsent(loggerName, new Logger());
        return instances.get(loggerName);
    }

    public void log() {
        //...
    }
}

public class TestLogger {
    public static void main(String[] args) {
        //l1==l2, l1!=l3
        Logger l1 = Logger.getInstance("User.class");
        Logger l2 = Logger.getInstance("User.class");
        Logger l3 = Logger.getInstance("Order.class");
    }
}

```

- 这种多例模式的理解方式有点类似工厂模式。
- 它跟工厂模式的不同之处是：
    - 多例模式创建的对象都是同一个类的对象。
    - 而工厂模式创建的是**不同子类的对象**，关于这一点，到工厂模式再分析。
- 它还有点类似享元模式，两者的区别到享元模式再分析。
- 除此之外，实际上，枚举类型也相当于多例模式，一个类型只能对应一个对象，一个类可以创建多个对象。