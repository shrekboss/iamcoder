## 实战案例一：处理资源访问冲突

### 需求背景

> 需求背景：自定义实现了一个往文件中打印日志的 Logger 类

### 代码演变

- 初始代码

```java
import java.io.FileWriter;

public class Logger {
    private FileWriter writer;

    public Logger() {
        File file = new File("/Users/crayzer/workspaces/iamcoder/log.txt");
        // true表示追加写入
        writer = new FileWriter(file, true);
    }

    public void log(String message) {
        writer.write(message);
    }
}

// Logger类的应用示例：
public class UserController {
    private Logger logger = new Logger();

    public void login(String username, String password) {
        // ...省略业务逻辑代码...
        logger.log(username + " logined!");
    }
}

public class OrderController {
    private Logger logger = new Logger();

    public void create(OrderVo order) {
        // ...省略业务逻辑代码...
        logger.log("Created an order: " + order.toString());
    }
}
```

> 所有的日志都写入到同一个文件 /Users/crayzer/workspaces/iamcoder/log.txt 中。在 UserController 和 OrderController
> 中，分别创建两个 Logger 对象。在 Web 容器的 Servlet 多线程环境下，如果两个 Servlet 线程同时分别执行 login() 和 create()
> 两个函数，并且同时写日志到 log.txt 文件中，那就有可能存在日志信息互相覆盖的情况

- 给 log() 函数加互斥锁 synchronized (this)

```java
import java.io.FileWriter;

public class Logger {
    private FileWriter writer;

    public Logger() {
        File file = new File("/Users/crayzer/workspaces/iamcoder/log.txt");
        // true表示追加写入
        writer = new FileWriter(file, true);
    }

    public void log(String message) {
        synchronized (this) {
            writer.write(mesasge);
        }
    }
}
```

> 这真的能解决多线程写入日志时互相覆盖的问题吗？答案是否定的。
> - 这种锁是一个对象级别的锁，一个对象在不同的线程下同时调用 log() 函数，会被强制要求顺序执行。
> - 但是，不同的对象之间并不共享同一把锁。在不同的线程下，通过不同的对象调用执行 log() 函数，锁并不会起作用，仍然有可能存在写入日志互相覆盖的问题。
> - 给 log() 函数加不加对象级别的锁，其实都没有关系。因为 FileWriter 本身就是线程安全的，它的内部实现中本身就加了对象级别的锁，因此，在外层调用
    write() 函数的时候，再加对象级别的锁实际上是多此一举。因为不同的 Logger 对象不共享 FileWriter 对象，所以，FileWriter
    对象级别的锁也解决不了数据写入互相覆盖的问题。

- 给 log() 函数加互斥锁 synchronized (Logger.class)

```java
import java.io.FileWriter;

public class Logger {
    private FileWriter writer;

    public Logger() {
        File file = new File("/Users/crayzer/workspaces/iamcoder/log.txt");
        // true表示追加写入
        writer = new FileWriter(file, true);
    }

    public void log(String message) {
        // 类级别的锁
        synchronized (Logger.class) {
            writer.write(mesasge);
        }
    }
}
```

除了使用类级别锁之外，实际上，解决资源竞争问题的办法还有很多:

- 分布式锁
    - 实现一个安全可靠、无 bug、高性能的分布式锁，并不是件容易的事情。
- 并发队列（比如 Java 中的 BlockingQueue）也可以解决这个问题。
    - 多个线程同时往并发队列里写日志，一个单独的线程负责将并发队列中的数据，写入到日志文件。
    - 这种方式实现起来也稍微有点复杂
- 单例模式: 参考代码：[UserController.java](UserController.java)
    - 一方面节省内存空间，不用创建那么多 Logger 对象。
    - 另一方面节省系统文件句柄（对于操作系统来说，文件句柄也是一种资源，不能随便浪费）。
    - 所有的线程共享使用的这一个 Logger 对象，共享一个 FileWriter 对象。
    - FileWriter 本身是对象级别线程安全的，也就避免了多线程情况下写日志会互相覆盖的问题。