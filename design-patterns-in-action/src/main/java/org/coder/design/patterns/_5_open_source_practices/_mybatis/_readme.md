## Mybatis

### MyBatis Plugin 的设计与实现

集成了 MyBatis 的应用在启动的时候，MyBatis 框架会读取全局配置文件（前面例子中的 mybatis-config.xml 文件），解析出
Interceptor（也就是例子中的 SqlCostTimeInterceptor），并且将它注入到 Configuration 类的 InterceptorChain 对象中。

这部分逻辑对应到源码如下所示：

```java
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.parsing.XNode;

public class XMLConfigBuilder extends BaseBuilder {
    //解析配置
    private void parseConfiguration(XNode root) {
        try {
            //省略部分代码...
            pluginElement(root.evalNode("plugins")); //解析插件
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    //解析插件
    private void pluginElement(XNode parent) throws Exception {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                String interceptor = child.getStringAttribute("interceptor");
                Properties properties = child.getChildrenAsProperties();
                //创建Interceptor类对象
                Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
                //调用Interceptor上的setProperties()方法设置properties
                interceptorInstance.setProperties(properties);
                //下面这行代码会调用InterceptorChain.addInterceptor()方法
                configuration.addInterceptor(interceptorInstance);
            }
        }
    }
}

// Configuration类的addInterceptor()方法的代码如下所示
public void addInterceptor(Interceptor interceptor) {
    interceptorChain.addInterceptor(interceptor);
}
```

Interceptor 和 InterceptorChain 这两个类的代码，如下所示。Interceptor 的 setProperties() 方法就是一个单纯的 setter
方法，主要是为了方便通过配置文件配置 Interceptor 的一些属性值，没有其他作用。Interceptor 类中 intecept() 和 plugin() 函数，以及
InterceptorChain 类中的 pluginAll() 函数，是最核心的三个函数。

```java
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

public class Invocation {
    private final Object target;
    private final Method method;
    private final Object[] args;

    // 省略构造函数和getter方法...
    public Object proceed() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, args);
    }
}

public interface Interceptor {
    Object intercept(Invocation invocation) throws Throwable;

    Object plugin(Object target);

    void setProperties(Properties properties);
}

public class InterceptorChain {
    private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }
}
```

解析完配置文件之后，所有的 Interceptor 都加载到了 InterceptorChain 中。

在执行 SQL 的过程中，MyBatis 会创建 Executor、StatementHandler、ParameterHandler、ResultSetHandler 这几个类的对象，对应的创建代码在
Configuration 类中，如下所示：

```java
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.ReuseExecutor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    executorType = executorType == null ? defaultExecutorType : executorType;
    executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
    Executor executor;
    if (ExecutorType.BATCH == executorType) {
        executor = new BatchExecutor(this, transaction);
    } else if (ExecutorType.REUSE == executorType) {
        executor = new ReuseExecutor(this, transaction);
    } else {
        executor = new SimpleExecutor(this, transaction);
    }
    if (cacheEnabled) {
        executor = new CachingExecutor(executor);
    }
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
}

public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
    parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
    return parameterHandler;
}

public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler,
                                            ResultHandler resultHandler, BoundSql boundSql) {
    ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
    return resultSetHandler;
}

public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
    StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
    return statementHandler;
}
```

这几个类对象的创建过程都调用了 InterceptorChain 的 pluginAll() 方法。它的代码实现很简单，嵌套调用 InterceptorChain 上每个
Interceptor 的 plugin() 方法。plugin() 是一个接口方法（不包含实现代码），需要由用户给出具体的实现代码。

在之前的例子中，SQLTimeCostInterceptor 的 plugin() 方法通过直接调用 Plugin 的 wrap() 方法来实现。wrap() 方法的代码实现如下所示：

```java
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.PluginException;
import org.apache.ibatis.reflection.ExceptionUtil;

import java.util.HashSet;

// 借助Java InvocationHandler实现的动态代理模式
public class Plugin implements InvocationHandler {
    private final Object target;
    private final Interceptor interceptor;
    private final Map<Class<?>, Set<Method>> signatureMap;

    private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

    // wrap()静态方法，用来生成target的动态代理，
    // 动态代理对象=target对象+interceptor对象。
    public static Object wrap(Object target, Interceptor interceptor) {
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
        Class<?> type = target.getClass();
        Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
        if (interfaces.length > 0) {
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    interfaces,
                    new Plugin(target, interceptor, signatureMap));
        }
        return target;
    }

    // 调用target上的f()方法，会触发执行下面这个方法。
    // 这个方法包含：执行interceptor的intecept()方法 + 执行target上f()方法。
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Set<Method> methods = signatureMap.get(method.getDeclaringClass());
            if (methods != null && methods.contains(method)) {
                return interceptor.intercept(new Invocation(target, method, args));
            }
            return method.invoke(target, args);
        } catch (Exception e) {
            throw ExceptionUtil.unwrapThrowable(e);
        }
    }

    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
        Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
        // issue #251
        if (interceptsAnnotation == null) {
            throw new PluginException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());
        }
        Signature[] sigs = interceptsAnnotation.value();
        Map<Class<?>, Set<Method>> signatureMap = new HashMap<Class<?>, Set<Method>>();
        for (Signature sig : sigs) {
            Set<Method> methods = signatureMap.get(sig.type());
            if (methods == null) {
                methods = new HashSet<Method>();
                signatureMap.put(sig.type(), methods);
            }
            try {
                Method method = sig.type().getMethod(sig.method(), sig.args());
                methods.add(method);
            } catch (NoSuchMethodException e) {
                throw new PluginException("Could not find method on " + sig.type() + " named " + sig.method() + ". Cause: " + e, e);
            }
        }
        return signatureMap;
    }

    private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        while (type != null) {
            for (Class<?> c : type.getInterfaces()) {
                if (signatureMap.containsKey(c)) {
                    interfaces.add(c);
                }
            }
            type = type.getSuperclass();
        }
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }
}
```

实际上，Plugin 是借助 Java InvocationHandler 实现的动态代理类。用来代理给 target 对象添加 Interceptor 功能。其中，要代理的
target 对象就是 Executor、StatementHandler、ParameterHandler、ResultSetHandler 这四个类的对象。wrap() 静态方法是一个工具函数，用来生成
target 对象的动态代理对象。

当然，只有 interceptor 与 target 互相匹配的时候，wrap() 方法才会返回代理对象，否则就返回 target 对象本身。怎么才算是匹配呢？那就是
interceptor 通过 @Signature 注解要拦截的类包含 target 对象，具体可以参看 wrap() 函数的代码实现（上面一段代码中的第 16~19
行）。

MyBatis 中的职责链模式的实现方式比较特殊。它对同一个目标对象嵌套多次代理（也就是 InteceptorChain 中的 pluginAll()
函数要执行的任务）。每个代理对象（Plugin 对象）代理一个拦截器（Interceptor 对象）功能。

pluginAll() 函数的代码如下：

```java
public Object pluginAll(Object target) {
    // 嵌套代理
    for (Interceptor interceptor : interceptors) {
        target = interceptor.plugin(target);
        // 上面这行代码等于下面这行代码，target(代理对象)=target(目标对象)+interceptor(拦截器功能)
        // target = Plugin.wrap(target, interceptor);
    }
    return target;
}

// MyBatis像下面这样创建target(Executor、StatementHandler、ParameterHandler、ResultSetHandler），相当于多次嵌套代理
Object target = interceptorChain.pluginAll(target);
```

当执行 Executor、StatementHandler、ParameterHandler、ResultSetHandler 这四个类上的某个方法的时候，MyBatis 会嵌套执行每层代理对象（Plugin
对象）上的 invoke() 方法。而 invoke() 方法会先执行代理对象中的 interceptor 的 intecept()
函数，然后再执行被代理对象上的方法。就这样，一层一层地把代理对象上的 intercept() 函数执行完之后，MyBatis 才最终执行那 4
个原始类对象上的方法。

### SqlSessionFactoryBuilder：为什么要用建造者模式来创建 SqlSessionFactory？

使用 Builder 类来创建对象，一般都是先级联一组 setXXX() 方法来设置属性，然后再调用 build() 方法最终创建对象。但是，在上面这段代码中，通过
SqlSessionFactoryBuilder 来创建 SqlSessionFactory 并不符合这个套路。它既没有 setter 方法，而且 build()
方法也并非无参，需要传递参数。除此之外，从上面的代码来看，SqlSessionFactory 对象的创建过程也并不复杂。那直接通过构造函数来创建
SqlSessionFactory 不就行了吗？为什么还要借助建造者模式创建 SqlSessionFactory 呢？

SqlSessionFactoryBuilder 类的源码如下：

```java
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import java.io.IOException;

public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(Reader reader) {
        return build(reader, null, null);
    }

    public SqlSessionFactory build(Reader reader, String environment) {
        return build(reader, environment, null);
    }

    public SqlSessionFactory build(Reader reader, Properties properties) {
        return build(reader, null, properties);
    }

    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                reader.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    public SqlSessionFactory build(InputStream inputStream) {
        return build(inputStream, null, null);
    }

    public SqlSessionFactory build(InputStream inputStream, String environment) {
        return build(inputStream, environment, null);
    }

    public SqlSessionFactory build(InputStream inputStream, Properties properties) {
        return build(inputStream, null, properties);
    }

    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                inputStream.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
```

SqlSessionFactoryBuilder 类中有大量的 build() 重载函数。把重载函数定义抽象出来，代码如下：

```java
public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(Reader reader);

    public SqlSessionFactory build(Reader reader, String environment);

    public SqlSessionFactory build(Reader reader, Properties properties);

    public SqlSessionFactory build(Reader reader, String environment, Properties properties);

    public SqlSessionFactory build(InputStream inputStream);

    public SqlSessionFactory build(InputStream inputStream, String environment);

    public SqlSessionFactory build(InputStream inputStream, Properties properties);

    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties);

    // 上面所有的方法最终都调用这个方法    
    public SqlSessionFactory build(Configuration config);
}
```

SqlSessionFactoryBuilder 虽然带有 Builder 后缀，但不要被它的名字所迷惑，它并不是标准的建造者模式。

- 一方面，原始类 SqlSessionFactory 的构建只需要一个参数，并不复杂。
- 另一方面，Builder 类 SqlSessionFactoryBuilder 仍然定义了 n 多包含不同参数列表的构造函数。

实际上，SqlSessionFactoryBuilder 设计的初衷只不过是为了简化开发。因为构建 SqlSessionFactory 需要先构建 Configuration，而构建
Configuration 是非常复杂的，需要做很多工作，比如配置的读取、解析、创建 n 多对象等。为了将构建 SqlSessionFactory
的过程隐藏起来，对程序员透明，MyBatis 就设计了 SqlSessionFactoryBuilder 类封装这些构建细节。

### SqlSessionFactory：到底属于工厂模式还是建造器模式？

```java
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.TransactionIsolationLevel;

public interface SqlSessionFactory {
    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

    SqlSession openSession(Connection connection);

    SqlSession openSession(TransactionIsolationLevel level);

    SqlSession openSession(ExecutorType execType);

    SqlSession openSession(ExecutorType execType, boolean autoCommit);

    SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);

    SqlSession openSession(ExecutorType execType, Connection connection);

    Configuration getConfiguration();
}
```

SqlSessionFactory 是一个接口，DefaultSqlSessionFactory 是它唯一的实现类。DefaultSqlSessionFactory 源码如下所示：

```java
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, autoCommit);
    }

    @Override
    public SqlSession openSession(ExecutorType execType) {
        return openSessionFromDataSource(execType, null, false);
    }

    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return openSessionFromDataSource(configuration.getDefaultExecutorType(), level, false);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
        return openSessionFromDataSource(execType, level, false);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
        return openSessionFromDataSource(execType, null, autoCommit);
    }

    @Override
    public SqlSession openSession(Connection connection) {
        return openSessionFromConnection(configuration.getDefaultExecutorType(), connection);
    }

    @Override
    public SqlSession openSession(ExecutorType execType, Connection connection) {
        return openSessionFromConnection(execType, connection);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
            final Executor executor = configuration.newExecutor(tx, execType);
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } catch (Exception e) {
            closeTransaction(tx); // may have fetched a connection so lets call close()
            throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    private SqlSession openSessionFromConnection(ExecutorType execType, Connection connection) {
        try {
            boolean autoCommit;
            try {
                autoCommit = connection.getAutoCommit();
            } catch (SQLException e) {
                // Failover to true, as most poor drivers
                // or databases won't support transactions
                autoCommit = true;
            }
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            final Transaction tx = transactionFactory.newTransaction(connection);
            final Executor executor = configuration.newExecutor(tx, execType);
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }
    }
    //...省略部分代码...
}
```

从 SqlSessionFactory 和 DefaultSqlSessionFactory 的源码来看，它的设计非常类似 SqlSessionFactoryBuilder，通过重载多个
openSession() 函数，支持通过组合 autoCommit、Executor、Transaction 等不同参数，来创建 SqlSession 对象。标准的工厂模式通过
type 来创建继承同一个父类的不同子类对象，而这里只不过是通过传递进不同的参数，来创建同一个类的对象。所以，它更像建造者模式。

虽然设计思路基本一致，但一个叫 xxxBuilder（SqlSessionFactoryBuilder），一个叫 xxxFactory（SqlSessionFactory）。而且，叫
xxxBuilder 的也并非标准的建造者模式，叫 xxxFactory 的也并非标准的工厂模式。所以，我个人觉得，MyBatis
对这部分代码的设计还是值得优化的。

实际上，这两个类的作用只不过是为了创建 SqlSession 对象，没有其他作用。所以，王争更建议参照
Spring 的设计思路，把 SqlSessionFactoryBuilder 和 SqlSessionFactory 的逻辑，放到一个叫“ApplicationContext”的类中。让这个类来全权负责读入配置文件，创建
Configuration，生成 SqlSession。

### BaseExecutor：模板模式跟普通的继承有什么区别？

如果去查阅 SqlSession 与 DefaultSqlSession 的源码，你会发现，SqlSession 执行 SQL 的业务逻辑，都是委托给了 Executor
来实现。Executor 相关的类主要是用来执行 SQL。其中，Executor 本身是一个接口；BaseExecutor 是一个抽象类，实现了 Executor 接口；而
BatchExecutor、SimpleExecutor、ReuseExecutor 三个类继承 BaseExecutor 抽象类。

```java
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

public abstract class BaseExecutor implements Executor {
    //...省略其他无关代码...

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        ErrorContext.instance().resource(ms.getResource()).activity("executing an update").object(ms.getId());
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        clearLocalCache();
        return doUpdate(ms, parameter);
    }

    public List<BatchResult> flushStatements(boolean isRollBack) throws SQLException {
        if (closed) {
            throw new ExecutorException("Executor was closed.");
        }
        return doFlushStatements(isRollBack);
    }

    private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        List<E> list;
        localCache.putObject(key, EXECUTION_PLACEHOLDER);
        try {
            list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
        } finally {
            localCache.removeObject(key);
        }
        localCache.putObject(key, list);
        if (ms.getStatementType() == StatementType.CALLABLE) {
            localOutputParameterCache.putObject(key, parameter);
        }
        return list;
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameter);
        return doQueryCursor(ms, parameter, rowBounds, boundSql);
    }

    protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;

    protected abstract List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException;

    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;

    protected abstract <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException;
}
```

模板模式基于继承来实现代码复用。如果抽象类中包含模板方法，模板方法调用有待子类实现的抽象方法，那这一般就是模板模式的代码实现。而且，在命名上，模板方法与抽象方法一般是一一对应的，抽象方法在模板方法前面多一个“do”，比如，在
BaseExecutor 类中，其中一个模板方法叫 update()，那对应的抽象方法就叫 doUpdate()。

### SqlNode：如何利用解释器模式来解析动态 SQL？

支持配置文件中编写动态 SQL，是 MyBatis 一个非常强大的功能。所谓动态 SQL，就是在 SQL 中可以包含在
trim、if、#{}等语法标签，在运行时根据条件来生成不同的 SQL。

```text
<update id="update" parameterType="XXX.XXX.User"
        UPDATE user
        <trim prefix="SET" prefixOverrides=",">
            <if test="name != null and name != ''">
                name = #{name}
            </if>
            <if test="age != null and age != ''">
                , age = #{age}
            </if>
            <if test="birthday != null and birthday != ''">
                , birthday = #{birthday}
            </if>
        </trim>
        where id = ${id}
</update>
```

解释器模式在解释语法规则的时候，一般会把规则分割成小的单元，特别是可以嵌套的小单元，针对每个小单元来解析，最终再把解析结果合并在一起。MyBatis
把每个语法小单元叫 SqlNode。SqlNode 的定义如下所示：

- org.apache.ibatis.scripting.xmltags.SqlNode

对于不同的语法小单元，MyBatis 定义不同的 SqlNode 实现类。

整个解释器的调用入口在 DynamicSqlSource.getBoundSql 方法中，它调用了 rootSqlNode.apply(context) 方法。

### ErrorContext：如何实现一个线程唯一的单例模式？

在 MyBatis 中，ErrorContext 这个类就是标准单例的变形：线程唯一的单例。它基于 Java 中的 ThreadLocal 来实现。实际上，这里的
ThreadLocal 就相当于那里的 ConcurrentHashMap。

- org.apache.ibatis.executor.ErrorContext

### Cache：为什么要用装饰器模式而不设计成继承子类？

在 MyBatis 中，缓存功能由接口 Cache 定义。PerpetualCache 类是最基础的缓存类，是一个大小无限的缓存。除此之外，MyBatis 还设计了
9 个包裹 PerpetualCache
类的装饰器类，用来实现功能增强。它们分别是：FifoCache、LoggingCache、LruCache、ScheduledCache、SerializedCache、SoftCache、SynchronizedCache、WeakCache、TransactionalCache。

```java
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;

public interface Cache {
    String getId();

    void putObject(Object key, Object value);

    Object getObject(Object key);

    Object removeObject(Object key);

    void clear();

    int getSize();

    ReadWriteLock getReadWriteLock();
}

public class PerpetualCache implements Cache {
    private final String id;
    private Map<Object, Object> cache = new HashMap<Object, Object>();

    public PerpetualCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return cache.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }
    //省略部分代码...
}
```

从代码中我们可以看出，它是标准的装饰器模式的代码实现。

```java
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public class LruCache implements Cache {
    private final Cache delegate;
    private Map<Object, Object> keyMap;
    private Object eldestKey;

    public LruCache(Cache delegate) {
        this.delegate = delegate;
        setSize(1024);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    public void setSize(final int size) {
        keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
            private static final long serialVersionUID = 4267176411845948333L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                boolean tooBig = size() > size;
                if (tooBig) {
                    eldestKey = eldest.getKey();
                }
                return tooBig;
            }
        };
    }

    @Override
    public void putObject(Object key, Object value) {
        delegate.putObject(key, value);
        cycleKeyList(key);
    }

    @Override
    public Object getObject(Object key) {
        keyMap.get(key); //touch
        return delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
        keyMap.clear();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    private void cycleKeyList(Object key) {
        keyMap.put(key, key);
        if (eldestKey != null) {
            delegate.removeObject(eldestKey);
            eldestKey = null;
        }
    }
}
```

之所以 MyBatis 采用装饰器模式来实现缓存功能，是因为装饰器模式采用了组合，而非继承，更加灵活，能够有效地避免继承关系的组合爆炸。

### PropertyTokenizer：如何利用迭代器模式实现一个属性解析器？

迭代器模式常用来替代 for 循环遍历集合元素。Mybatis 的 PropertyTokenizer 类实现了 Java Iterator
接口，是一个迭代器，用来对配置属性进行解析。具体的代码如下所示：

```java
// person[0].birthdate.year 会被分解为3个PropertyTokenizer对象。其中，第一个PropertyTokenizer对象的各个属性值如注释所示。
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {
    private String name; // person
    private final String indexedName; // person[0]
    private String index; // 0
    private final String children; // birthdate.year

    public PropertyTokenizer(String fullname) {
        int delim = fullname.indexOf('.');
        if (delim > -1) {
            name = fullname.substring(0, delim);
            children = fullname.substring(delim + 1);
        } else {
            name = fullname;
            children = null;
        }
        indexedName = name;
        delim = name.indexOf('[');
        if (delim > -1) {
            index = name.substring(delim + 1, name.length() - 1);
            name = name.substring(0, delim);
        }
    }

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public String getChildren() {
        return children;
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
    }
}
```

实际上，PropertyTokenizer
类也并非标准的迭代器类。它将配置的解析、解析之后的元素、迭代器，这三部分本该放到三个类中的代码，都耦合在一个类中，所以看起来稍微有点难懂。不过，这样做的好处是能够做到惰性解析。我们不需要事先将整个配置，解析成多个
PropertyTokenizer 对象。只有当我们在调用 next() 函数的时候，才会解析其中部分配置。

### Log：如何使用适配器模式来适配不同的日志框架？

Slf4j 框架为了统一各个不同的日志框架（Log4j、JCL、Logback 等），提供了一套统一的日志接口。不过，MyBatis 并没有直接使用 Slf4j
提供的统一日志规范，而是自己又重复造轮子，定义了一套自己的日志访问接口。

org.apache.ibatis.logging.Log

```java
public interface Log {
    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);
}
```

针对 Log 接口，MyBatis 还提供了各种不同的实现类，分别使用不同的日志框架来实现 Log 接口。

在适配器模式中，传递给适配器构造函数的是被适配的类对象，而这里是 clazz（相当于日志名称
name），所以，从代码实现上来讲，它并非标准的适配器模式。但是，从应用场景上来看，这里确实又起到了适配的作用，是典型的适配器模式的应用场景。

Log4jImpl 的源码如下：

```java
import org.apache.ibatis.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jImpl implements Log {
    private static final String FQCN = Log4jImpl.class.getName();
    private final Logger log;

    public Log4jImpl(String clazz) {
        log = Logger.getLogger(clazz);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        log.log(FQCN, Level.ERROR, s, e);
    }

    @Override
    public void error(String s) {
        log.log(FQCN, Level.ERROR, s, null);
    }

    @Override
    public void debug(String s) {
        log.log(FQCN, Level.DEBUG, s, null);
    }

    @Override
    public void trace(String s) {
        log.log(FQCN, Level.TRACE, s, null);
    }

    @Override
    public void warn(String s) {
        log.log(FQCN, Level.WARN, s, null);
    }
}
```

MyBatis 对很多设计模式的实现，都并非标准的代码实现，都做了比较多的自我改进。实际上，这就是所谓的灵活应用，只借鉴不照搬，根据具体问题针对性地去解决。




