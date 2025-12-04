## 把“接口”理解为一组 API 接口集合

> 接口隔离原则的英文翻译是“ Interface Segregation Principle”，缩写为 ISP。Robert Martin 在 SOLID 原则中是这样定义它的：“Clients 
> should not be forced to depend upon interfaces that they do not use。”直译成中文的话就是：客户端不应该被强迫依赖它不需要的接口。
> 其中的“客户端”，可以理解为接口的调用者或者使用者。

> 微服务用户系统提供了一组跟用户相关的 API 给其他系统使用，比如：注册、登录、获取用户信息等。

具体代码如下所示：

```java
public interface UserService {
    boolean register(String cellphone, String password);

    boolean login(String cellphone, String password);

    UserInfo getUserInfoById(long id);

    UserInfo getUserInfoByCellphone(String cellphone);
}

public class UserServiceImpl implements UserService {
    //...
}
```

后台管理系统要实现删除用户的功能，希望用户系统提供一个删除用户的接口。

在 UserService 中新添加一个 deleteUserByCellphone() 或 deleteUserById() 接口就可以了。这个方法可以解决问题，但是也隐藏了一些安全隐患。

删除用户是一个非常慎重的操作，我们只希望通过后台管理系统来执行，所以这个接口只限于给后台管理系统使用。不加限制地被其他业务系统调用，就有可能导致误删用户。

参照接口隔离原则，调用者不应该强迫依赖它不需要的接口，将删除接口单独放到另外一个接口 RestrictedUserService 中，然后将
RestrictedUserService 只打包提供给后台管理系统来使用。具体的代码实现如下所示：

```java
public interface UserService {
    boolean register(String cellphone, String password);

    boolean login(String cellphone, String password);

    UserInfo getUserInfoById(long id);

    UserInfo getUserInfoByCellphone(String cellphone);
}

public interface RestrictedUserService {
    boolean deleteUserByCellphone(String cellphone);

    boolean deleteUserById(long id);
}

public class UserServiceImpl implements UserService, RestrictedUserService {
    // ...省略实现代码...
}
```

## 把“接口”理解为单个 API 接口或函数

> 那接口隔离原则就可以理解为：函数的设计要功能单一，不要将多个不同的功能逻辑在一个函数中实现。

示例代码如下所示：

```java
import java.util.Collection;public class Statistics {
    private Long max;
    private Long min;
    private Long average;
    private Long sum;
    private Long percentile99;
    private Long percentile999;
    //...省略constructor/getter/setter等方法...
}

/**
 * count() 函数的功能不够单一 
 */
//public Statistics count(Collection<Long> dataSet) {
//    Statistics statistics = new Statistics();
//    //...省略计算逻辑...
//    return statistics;
//}

// 把 count() 函数拆成几个更小粒度的函数，每个函数负责一个独立的统计功能
public Long max(Collection<Long> dataSet) { /*...*/ }
public Long min(Collection<Long> dataSet) { /*...*/ }
public Long average(Colletion<Long> dataSet) { /*...*/ }
// ...省略其他统计函数...
```

接口隔离原则跟单一职责原则有点类似，不过稍微还是有点区别。

- 单一职责原则针对的是模块、类、接口的设计。
- 而接口隔离原则相对于单一职责原则:
    - 一方面它更侧重于接口的设计;
    - 另一方面它的思考的角度不同。
- 接口隔离原则提供了一种判断接口是否职责单一的标准：
    - 通过调用者如何使用接口来间接地判定。如果调用者只使用部分接口或接口的部分功能，那接口的设计就不够职责单一。

## 把“接口”理解为 OOP 中的接口概念

> 项目中用到了三个外部系统：Redis、MySQL、Kafka。每个系统都对应一系列配置信息，比如地址、端口、访问超时时间等。为了在内存中存储这些配置信息，供项目中的其他模块来使用

分别设计实现了三个 Configuration 类：RedisConfig、MysqlConfig、KafkaConfig。具体的代码实现如下所示:

参考代码：

- [KafkaConfig.java](KafkaConfig.java)
- [MySqlConfig.java](MySqlConfig.java)
- [RedisConfig.java](RedisConfig.java)

新的功能需求，希望支持 Redis 和 Kafka 配置信息的热更新。

参考代码：

- [Updater.java](Updater.java)
- [ScheduledUpdater.java](ScheduledUpdater.java)
- [RedisConfig.java](RedisConfig.java)
- [KafkaConfig.java](KafkaConfig.java)
- [Application.java](Application.java)

通过命令行来查看 Zookeeper 中的配置信息是比较麻烦的。所以，我们希望能有一种更加方便的配置信息查看方式。

可以在项目中开发一个内嵌的 SimpleHttpServer，输出项目的配置信息到一个固定的 HTTP 地址，比如：http://127.0.0.1:2389/config
。我们只需要在浏览器中输入这个地址，就可以显示出系统的配置信息。不过，出于某些原因，我们只想暴露 MySQL 和 Redis 的配置信息，不想暴露
Kafka 的配置信息。

又有一个新的需求，开发一个 Metrics 性能统计模块，并且希望将 Metrics 也通过 SimpleHttpServer 显示在网页上，以方便查看。

参考代码：

- [Viewer.java](Viewer.java)
- [SimpleHttpServer.java](SimpleHttpServer.java)
- [MySqlConfig.java](MySqlConfig.java)
- [RedisConfig.java](RedisConfig.java)
- [DbMetrics.java](DbMetrics.java)
- [ApiMetrics.java](ApiMetrics.java)
- [Application.java](Application.java)

设计了两个功能非常单一的接口：Updater 和 Viewer。ScheduledUpdater 只依赖 Updater 这个跟热更新相关的接口，不需要被强迫去依赖不需要的
Viewer 接口，满足接口隔离原则。同理，SimpleHttpServer 只依赖跟查看信息相关的 Viewer 接口，不依赖不需要的 Updater
接口，也满足接口隔离原则。