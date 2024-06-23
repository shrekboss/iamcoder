## 回调函数

代码如下所示：

- [AClass.java](AClass.java)
- [BClass.java](BClass.java)
- [ICallback.java](ICallback.java)

### 应用举例一：JdbcTemplate

下面这段是使用 JDBC 来查询用户信息的代码：

```java
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcDemo {
    public User queryUser(long id) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //1.加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "xzg", "xzg");

            //2.创建statement类对象，用来执行SQL语句
            stmt = conn.createStatement();

            //3.ResultSet类，用来存放获取的结果集
            String sql = "select * from user where id=" + id;
            ResultSet resultSet = stmt.executeQuery(sql);

            String eid = null, ename = null, price = null;

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setTelephone(resultSet.getString("telephone"));
                return user;
            }
        } catch (ClassNotFoundException e) {
            // TODO: log...
        } catch (SQLException e) {
            // TODO: log...
        } finally {
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO: log...
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    // TODO: log...
                }
        }
        return null;
    }

}
```

> 针对不同的 SQL 执行请求，这些流程性质的代码是相同的、可以复用的，不需要每次都重新敲一遍。Spring 提供了 JdbcTemplate，对
> JDBC 进一步封装，来简化数据库编程。

```java
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTemplateDemo {
    private JdbcTemplate jdbcTemplate;

    public User queryUser(long id) {
        String sql = "select * from user where id=" + id;
        return jdbcTemplate.query(sql, new UserRowMapper()).get(0);
    }

    class UserRowMapper implements RowMapper<User> {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setTelephone(rs.getString("telephone"));
            return user;
        }
    }
}
```

JdbcTemplate 底层具体是如何实现的呢？

> JdbcTemplate 通过回调的机制，将不变的执行流程抽离出来，放到模板方法 execute() 中，将可变的部分设计成回调
> StatementCallback，由用户来定制。query() 函数是对 execute() 函数的二次封装，让接口用起来更加方便。

```java
import java.sql.ResultSet;
import java.sql.SQLException;

@Override
public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
    return query(sql, new RowMapperResultSetExtractor<T>(rowMapper));
}

@Override
public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
    Assert.notNull(sql, "SQL must not be null");
    Assert.notNull(rse, "ResultSetExtractor must not be null");
    if (logger.isDebugEnabled()) {
        logger.debug("Executing SQL query [" + sql + "]");
    }

    class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
        @Override
        public T doInStatement(Statement stmt) throws SQLException {
            ResultSet rs = null;
            try {
                rs = stmt.executeQuery(sql);
                ResultSet rsToUse = rs;
                if (nativeJdbcExtractor != null) {
                    rsToUse = nativeJdbcExtractor.getNativeResultSet(rs);
                }
                return rse.extractData(rsToUse);
            } finally {
                JdbcUtils.closeResultSet(rs);
            }
        }

        @Override
        public String getSql() {
            return sql;
        }
    }

    return execute(new QueryStatementCallback());
}

@Override
public <T> T execute(StatementCallback<T> action) throws DataAccessException {
    Assert.notNull(action, "Callback object must not be null");

    Connection con = DataSourceUtils.getConnection(getDataSource());
    Statement stmt = null;
    try {
        Connection conToUse = con;
        if (this.nativeJdbcExtractor != null &&
                this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativeStatements()) {
            conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
        }
        stmt = conToUse.createStatement();
        applyStatementSettings(stmt);
        Statement stmtToUse = stmt;
        if (this.nativeJdbcExtractor != null) {
            stmtToUse = this.nativeJdbcExtractor.getNativeStatement(stmt);
        }
        T result = action.doInStatement(stmtToUse);
        handleWarnings(stmt);
        return result;
    } catch (SQLException ex) {
        // Release Connection early, to avoid potential connection pool deadlock
        // in the case when the exception translator hasn't been initialized yet.
        JdbcUtils.closeStatement(stmt);
        stmt = null;
        DataSourceUtils.releaseConnection(con, getDataSource());
        con = null;
        throw getExceptionTranslator().translate("StatementCallback", getSql(action), ex);
    } finally {
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, getDataSource());
    }
}
```

### 应用举例二：setClickListener(）

> 从代码结构上来看，事件监听器很像回调，即传递一个包含回调函数（onClick()
> ）的对象给另一个函数。从应用场景上来看，它又很像观察者模式，即事先注册观察者（OnClickListener），当用户点击按钮的时候，发送点击事件给观察者，并且执行相应的
> onClick() 函数。这里的回调算是异步回调，往 setOnClickListener() 函数中注册好回调函数之后，并不需要等待回调函数执行。这也印证了，异步回调比较像观察者模式。

```java
import java.awt.*;

Button button = (Button) findViewById(R.id.button);
button.

setOnClickListener(new OnClickListener() {
    @Override
    public void onClick (View v){
        System.out.println("I am clicked.");
    }
});
```

### 应用举例三：addShutdownHook()

> Callback 更侧重语法机制的描述，Hook 更加侧重应用场景的描述。Hook 比较经典的应用场景是 Tomcat 和 JVM 的 shutdown
> hook。接下来，我们拿 JVM 来举例说明一下。JVM 提供了 Runtime.addShutdownHook(Thread hook) 方法，可以注册一个 JVM 关闭的
> Hook。当应用程序关闭的时候，JVM 会自动调用 Hook 代码。

代码示例如下所示：

```java
public class ShutdownHookDemo {

    private static class ShutdownHook extends Thread {
        public void run() {
            System.out.println("I am called during shutting down.");
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

}
```

再来看 addShutdownHook() 的代码实现。

```java
import java.util.Collection;
import java.util.IdentityHashMap;

public class Runtime {
    public void addShutdownHook(Thread hook) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("shutdownHooks"));
        }
        ApplicationShutdownHooks.add(hook);
    }
}

class ApplicationShutdownHooks {
    /* The set of registered hooks */
    private static IdentityHashMap<Thread, Thread> hooks;

    static {
        hooks = new IdentityHashMap<>();
    } catch(
    IllegalStateException e)

    {
        hooks = null;
    }
}

static synchronized void add(Thread hook) {
    if (hooks == null)
        throw new IllegalStateException("Shutdown in progress");

    if (hook.isAlive())
        throw new IllegalArgumentException("Hook already running");

    if (hooks.containsKey(hook))
        throw new IllegalArgumentException("Hook previously registered");

    hooks.put(hook, hook);
}

static void runHooks() {
    Collection<Thread> threads;
    synchronized (ApplicationShutdownHooks.class) {
        threads = hooks.keySet();
        hooks = null;
    }

    for (Thread hook : threads) {
        hook.start();
    }
    for (Thread hook : threads) {
        while (true) {
            try {
                hook.join();
                break;
            } catch (InterruptedException ignored) {
            }
        }
    }
}
}
```

> 从代码中可以发现，有关 Hook 的逻辑都被封装到 ApplicationShutdownHooks 类中了。当应用程序关闭的时候，JVM 会调用这个类的
> runHooks() 方法，创建多个线程，并发地执行多个 Hook。在注册完 Hook 之后，并不需要等待 Hook 执行完成，所以，这也算是一种异步回调。

