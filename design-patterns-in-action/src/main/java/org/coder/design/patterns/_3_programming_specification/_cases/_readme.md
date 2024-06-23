## ID 生成器

一份“能用”的代码实现

```java
import java.net.InetAddress;
import java.util.Random;

public class IdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(IdGenerator.class);

    public static String generate() {
        String id = "";
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            String[] tokens = hostName.split("\\.");
            if (tokens.length > 0) {
                hostName = tokens[tokens.length - 1];
            }
            char[] randomChars = new char[8];
            int count = 0;
            Random random = new Random();
            while (count < 8) {
                int randomAscii = random.nextInt(122);
                if (randomAscii >= 48 && randomAscii <= 57) {
                    randomChars[count] = (char) ('0' + (randomAscii - 48));
                    count++;
                } else if (randomAscii >= 65 && randomAscii <= 90) {
                    randomChars[count] = (char) ('A' + (randomAscii - 65));
                    count++;
                } else if (randomAscii >= 97 && randomAscii <= 122) {
                    randomChars[count] = (char) ('a' + (randomAscii - 97));
                    count++;
                }
            }
            id = String.format("%s-%d-%s", hostName,
                    System.currentTimeMillis(), new String(randomChars));
        } catch (UnknownHostException e) {
            logger.warn("Failed to get the host name.", e);
        }

        return id;
    }
}
```

- 如何发现代码质量问题 - 常规 checklist
    - 目录设置是否合理、模块划分是否清晰、代码结构是否满足“高内聚、松耦合”？
    - 是否遵循经典的设计原则和设计思想（SOLID、DRY、KISS、YAGNI、LOD 等）？
    - 设计模式是否应用得当？是否有过度设计？
    - 代码是否容易扩展？如果要添加新功能，是否容易实现？
    - 代码是否可以复用？是否可以复用已有的项目代码或类库？是否有重复造轮子？
    - 代码是否容易测试？单元测试是否全面覆盖了各种正常和异常的情况？
    - 代码是否易读？是否符合编码规范（比如命名和注释是否恰当、代码风格是否一致等）？

- 如何发现代码质量问题 - 业务需求 checklist
    - 代码是否实现了预期的业务需求？
    - 逻辑是否正确？是否处理了各种异常情况？
    - 日志打印是否得当？是否方便 debug 排查问题？
    - 接口是否易用？是否支持幂等、事务等？
    - 代码是否存在并发问题？是否线程安全？
    - 性能是否有优化空间，比如，SQL、算法是否可以优化？
    - 是否有安全漏洞？比如输入输出校验是否全面？

### 第一轮重构：提高代码的可读性

- 具体有下面几点：
    - hostName 变量不应该被重复使用，尤其当这两次使用时的含义还不同的时候；
    - 将获取 hostName 的代码抽离出来，定义为 getLastFieldOfHostName() 函数；
    - 删除代码中的魔法数，比如，57、90、97、122；
    - 将随机数生成的代码抽离出来，定义为 generateRandomAlphameric() 函数；
    - generate() 函数中的三个 if 逻辑重复了，且实现过于复杂，要对其进行简化；
    - 对 IdGenerator 类重命名，并且抽象出对应的接口。

参考代码：

- [IdGenerator.java](IdGenerator.java)
- [LogTraceIdGenerator.java](LogTraceIdGenerator.java)
- [RandomIdGenerator.java](RandomIdGenerator.java)
- [SequenceIdGenerator.java](SequenceIdGenerator.java)

### 第二轮重构：提高代码的可测试性

- 主要包含下面两个方面：
    - generate() 函数定义为静态函数，会影响使用该函数的代码的可测试性；
    - generate() 函数的代码实现依赖运行环境（本机名）、时间函数、随机函数，所以 generate() 函数本身的可测试性也不好。

参考代码：[RandomIdGenerator.java](v2%2FRandomIdGenerator.java)

### 第三轮重构：编写完善的单元测试

参考代码：[RandomIdGeneratorTest.java](..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2F..%2Ftest%2Fjava%2Forg%2Fcoder%2Fdesign%2Fpatterns%2F_3_programming_specification%2F_cases%2Fidgenerator%2Fv2%2FRandomIdGeneratorTest.java)

### 第四轮重构：所有重构完成之后添加注释

参考代码：[RandomIdGenerator.java](v4%2FRandomIdGenerator.java)

## 程序出错该返回啥？NULL、异常、错误码、空对象？

> - 可以把函数的运行结果分为两类。一类是预期的结果，也就是函数在正常情况下输出的结果。
> - 一类是非预期的结果，也就是函数在异常（或叫出错）情况下输出的结果。

### 函数出错应该返回啥？

#### ~~返回错误码~~

C 语言没有异常这样的语法机制，返回错误码便是最常用的出错处理方式。而 Java、Python
等比较新的编程语言中，大部分情况下，我们都用异常来处理函数出错的情况，极少会用到错误码。

#### 返回 NULL 值

```java
public class UserService {
    // 依赖注入
    private UserRepo userRepo;

    public User getUser(String telephone) {
        // 如果用户不存在，则返回null
        return null;
    }

    public static void main(String[] args) {
        // 使用函数getUser()
        User user = userService.getUser("18917718965");
        if (user != null) { // 做NULL值判断，否则有可能会报NPE
            String email = user.getEmail();
            if (email != null) { // 做NULL值判断，否则有可能会报NPE
                String escapedEmail = email.replaceAll("@", "#");
            }
        }
    }
}
```

返回 NULL 值有诸多弊端，但对于以 get、find、select、search、query 等单词开头的查找函数来说，数据不存在，并非一种异常情况，这是一种正常行为。所以，返回代表不存在语义的
NULL 值比返回异常更加合理。

对于查找函数来说，除了返回数据对象之外，有的还会返回下标位置，比如 Java 中的 indexOf()
函数，用来实现在某个字符串中查找另一个子串第一次出现的位置。函数的返回值类型为基本类型 int。这个时候，我们就无法用 NULL
值来表示不存在的情况了。对于这种情况，我们有两种处理思路，一种是返回 NotFoundException，一种是返回一个特殊值，比如 -1。不过，显然
-1 更加合理，理由也是同样的，也就是说“没有查找到”是一种正常而非异常的行为。

#### 返回空对象

> 两种比较简单、比较特殊的空对象，那就是空字符串和空集合。
> 当函数返回的数据是字符串类型或者集合类型的时候，我们可以用空字符串或空集合替代 NULL 值，来表示不存在的情况。

```java
import java.util.Collections;

// 使用空集合替代NULL
public class UserService {
    private UserRepo userRepo; // 依赖注入

    public List<User> getUsers(String telephonePrefix) {
        // 没有查找到数据
        return Collections.emptyList();
    }

    // 使用空字符串替代NULL
    public String retrieveUppercaseLetters(String text) {
        // 如果text中没有大写字母，返回空字符串，而非NULL值
        return "";
    }

    public static void main(String[] args) {
        // getUsers使用示例
        List<User> users = userService.getUsers("189");
        for (User user : users) { //这里不需要做NULL值判断
            // ...
        }

        // retrieveUppercaseLetters()使用举例
        String uppercaseLetters = retrieveUppercaseLetters("wangzheng");
        int length = uppercaseLetters.length();// 不需要做NULL值判断 
        System.out.println("Contains " + length + " upper case letters.");
    }
}
```

#### 抛出异常对象

异常有两种类型：受检异常和非受检异常。

对于应该用受检异常还是非受检异常，网上的争论有很多，但也并没有一个非常强有力的理由，说明一个就一定比另一个更好。所以，我们只需要根据团队的开发习惯，在同一个项目中，制定统一的异常处理规范即可。

```java
import org.apache.commons.lang3.StringUtils;

// address格式："192.131.2.33:7896"
public void parseRedisAddress(String address) {
    this.host = RedisConfig.DEFAULT_HOST;
    this.port = RedisConfig.DEFAULT_PORT;

    if (StringUtils.isBlank(address)) {
        return;
    }

    String[] ipAndPort = address.split(":");
    if (ipAndPort.length != 2) {
        throw new RuntimeException("...");
    }

    this.host = ipAndPort[0];
    // parseInt()解析失败会抛出NumberFormatException运行时异常
    this.port = Integer.parseInt(ipAndPort[1]);
}
```

对于函数抛出的异常，我们有三种处理方法：

- 直接吞掉。具体的代码示例如下所示：

```java
public void func1() throws Exception1 {
    // ...
}

public void func2() {
    //...
    try {
        func1();
    } catch (Exception1 e) {
        log.warn("...", e); //吐掉：try-catch打印日志
    }
    //...
}
```

- 原封不动地 re-throw。具体的代码示例如下所示：

```java
public void func1() throws Exception1 {
    // ...
}


public void func2() throws Exception1 {//原封不动的re-throw Exception1
    //...
    func1();
    //...
}
```

- 包装成新的异常 re-throw。具体的代码示例如下所示：

```java
public void func1() throws Exception1 {
    // ...
}


public void func2() throws Exception2 {
    //...
    try {
        func1();
    } catch (Exception1 e) {
        throw new Exception2("...", e); // wrap成新的Exception2然后re-throw
    }
    //...
}
```

- 总结了下面三个参考原则：
    - 如果 func1() 抛出的异常是可以恢复，且 func2() 的调用方并不关心此异常，完全可以在 func2() 内将 func1() 抛出的异常吞掉；
    - 如果 func1() 抛出的异常对 func2() 的调用方来说，也是可以理解的、关心的 ，并且在业务概念上有一定的相关性，可以选择直接将
      func1 抛出的异常 re-throw；
    - 如果 func1() 抛出的异常太底层，对 func2() 的调用方来说，缺乏背景去理解、且业务概念上无关，可以将它重新包装成调用方可以理解的新异常，然后
      re-throw。

## 重构ID生成器项目中各函数的异常处理代码

参考代码：[RandomIdGenerator.java](v5%2FRandomIdGenerator.java)

### 重构 generate() 函数

ID 由三部分构成：本机名、时间戳和随机数。时间戳和随机数的生成函数不会出错，唯独主机名有可能获取失败。在目前的代码实现中，如果主机名获取失败，substrOfHostName 为 NULL，那 generate() 函数会返回类似“null-16723733647-83Ab3uK6”这样的数据。如果主机名获取失败，substrOfHostName 为空字符串，那 generate() 函数会返回类似“-16723733647-83Ab3uK6”这样的数据。

不过，我更倾向于明确地将异常告知调用者。所以，这里最好是抛出受检异常，而非特殊值。

### 重构 getLastFieldOfHostName() 函数

getLastFieldOfHostName() 函数用来获取主机名的最后一个字段，UnknownHostException 异常表示主机名获取失败，两者算是业务相关，所以可以直接将 UnknownHostException 抛出，不需要重新包裹成新的异常。

### 重构 getLastSubstrSplitByDot() 函数

如果函数是 private 类私有的，只在类内部被调用，完全在你自己的掌控之下，自己保证在调用这个 private 函数的时候，不要传递 NULL 值或空字符串就可以了。所以，可以不在 private 函数中做 NULL 值或空字符串的判断。如果函数是 public / protected 的，你无法掌控会被谁调用以及如何调用（有可能某个同事一时疏忽，传递进了 NULL 值，这种情况也是存在的），为了尽可能提高代码的健壮性，我们最好是在 public 函数中做 NULL 值或空字符串的判断。

#### 重构 generateRandomAlphameric() 函数

















































