## 桥接模式

> JDBC 驱动是桥接模式的经典应用。

如何利用 JDBC 驱动来查询数据库。具体的代码如下所示：

```java
import java.sql.DriverManager;import java.sql.ResultSet;

Class.forName("com.mysql.jdbc.Driver");//加载及注册JDBC驱动程序
String url = "jdbc:mysql://localhost:3306/sample_db?user=root&password=your_password";
Connection con = DriverManager.getConnection(url);
Statement stmt = con.createStatement()；
String query = "select * from test";
ResultSet rs=stmt.executeQuery(query);
while(rs.next()) {
  rs.getString(1);
  rs.getInt(2);
}
```

从 com.mysql.jdbc.Driver 这个类的代码看起：

```java
package com.mysql.jdbc;
import java.sql.SQLException;

public class Driver extends NonRegisteringDriver implements java.sql.Driver {
  static {
    try {
      java.sql.DriverManager.registerDriver(new Driver());
    } catch (SQLException E) {
      throw new RuntimeException("Can't register driver!");
    }
  }

  /**
   * Construct a new driver and register it with DriverManager
   * @throws SQLException if a database error occurs.
   */
  public Driver() throws SQLException {
    // Required for Class.forName().newInstance()
  }
}
```

- 当执行 Class.forName(“com.mysql.jdbc.Driver”) 这条语句的时候，实际上是做了两件事情：
    - 要求 JVM 查找并加载指定的 Driver 类；
    - 执行该类的静态代码，也就是将 MySQL Driver 注册到 DriverManager 类中。

DriverManager 类是干什么用的。具体的代码如下所示：

```java
import sun.reflect.Reflection;

import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;

public class DriverManager {
    private final static CopyOnWriteArrayList<DriverInfo> registeredDrivers = new CopyOnWriteArrayList<DriverInfo>();

    //...
    static {
        loadInitialDrivers();
        println("JDBC DriverManager initialized");
    }
    //...

    public static synchronized void registerDriver(java.sql.Driver driver) throws SQLException {
        if (driver != null) {
            registeredDrivers.addIfAbsent(new DriverInfo(driver));
        } else {
            throw new NullPointerException();
        }
    }

    public static Connection getConnection(String url, String user, String password) throws SQLException {
        java.util.Properties info = new java.util.Properties();
        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }
        return (getConnection(url, info, Reflection.getCallerClass()));
    }
    //...
}
```

桥接模式的定义是“将抽象和实现解耦，让它们可以独立变化”。那弄懂定义中“抽象”和“实现”两个概念。

- “抽象”：JDBC 本身就相当于“抽象”。注意，这里所说的“抽象”，指的并非“抽象类”或“接口”，而是跟具体的数据库无关的、被抽象出来的一套“类库”。
- “实现”：具体的 Driver（比如，com.mysql.jdbc.Driver）就相当于“实现”。注意，这里所说的“实现”，也并非指“接口的实现类”，而是跟具体数据库相关的一套“类库”。

参考代码：

- [Notification.java](..%2F..%2F..%2Fcommon%2FNotification.java)
- [SevereNotification.java](SevereNotification.java)
- [NormalNotification.java](NormalNotification.java)
- [UrgencyNotification.java](UrgencyNotification.java)
- [TrivialNotification.java](TrivialNotification.java)
- [MsgSender.java](MsgSender.java)
- [EmailMsgSender.java](EmailMsgSender.java)
- [TelephoneMsgSender.java](TelephoneMsgSender.java)
- [WechatMsgSender.java](WechatMsgSender.java)



























