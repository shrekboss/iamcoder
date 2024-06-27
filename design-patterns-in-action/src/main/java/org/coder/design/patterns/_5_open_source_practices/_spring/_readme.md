## Spring

### Spring 提供的观察者模式

- 定义一个继承 ApplicationEvent 的事件 [DemoEvent.java](DemoEvent.java)；
- 定义一个实现了 ApplicationListener 的监听器 [DemoListener.java](DemoListener.java)；
- 定义一个发送者 [DemoPublisher.java](DemoPublisher.java)，发送者调用 ApplicationContext 来发送事件消息。

> ApplicationEvent 和 ApplicationListener 的代码实现都非常简单，内部并不包含太多属性和方法。实际上，它们最大的作用是做类型标识之用（继承自
> ApplicationEvent 的类是事件，实现 ApplicationListener 的类是监听器）。

```java
public abstract class ApplicationEvent extends EventObject {
    private static final long serialVersionUID = 7099057708183571937L;
    private final long timestamp = System.currentTimeMillis();

    public ApplicationEvent(Object source) {
        super(source);
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}

public class EventObject implements java.io.Serializable {
    private static final long serialVersionUID = 5516075349620653480L;
    protected transient Object source;

    public EventObject(Object source) {
        if (source == null)
            throw new IllegalArgumentException("null source");
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}

public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E var1);
}
```

具体到源码来说，ApplicationContext 只是一个接口，具体的代码实现包含在它的实现类 AbstractApplicationContext 中。

```java
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {
    private final Set<ApplicationListener<?>> applicationListeners;

    public AbstractApplicationContext() {
        this.applicationListeners = new LinkedHashSet();
        //...
    }

    public void publishEvent(ApplicationEvent event) {
        this.publishEvent(event, (ResolvableType) null);
    }

    public void publishEvent(Object event) {
        this.publishEvent(event, (ResolvableType) null);
    }

    protected void publishEvent(Object event, ResolvableType eventType) {
        //...
        Object applicationEvent;
        if (event instanceof ApplicationEvent) {
            applicationEvent = (ApplicationEvent) event;
        } else {
            applicationEvent = new PayloadApplicationEvent(this, event);
            if (eventType == null) {
                eventType = ((PayloadApplicationEvent) applicationEvent).getResolvableType();
            }
        }

        if (this.earlyApplicationEvents != null) {
            this.earlyApplicationEvents.add(applicationEvent);
        } else {
            this.getApplicationEventMulticaster().multicastEvent(
                    (ApplicationEvent) applicationEvent, eventType);
        }

        if (this.parent != null) {
            if (this.parent instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
            } else {
                this.parent.publishEvent(event);
            }
        }
    }

    public void addApplicationListener(ApplicationListener<?> listener) {
        Assert.notNull(listener, "ApplicationListener must not be null");
        if (this.applicationEventMulticaster != null) {
            this.applicationEventMulticaster.addApplicationListener(listener);
        } else {
            this.applicationListeners.add(listener);
        }
    }

    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
    }

    protected void registerListeners() {
        Iterator var1 = this.getApplicationListeners().iterator();

        while (var1.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener) var1.next();
            this.getApplicationEventMulticaster().addApplicationListener(listener);
        }

        String[] listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class, true, false);
        String[] var7 = listenerBeanNames;
        int var3 = listenerBeanNames.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String listenerBeanName = var7[var4];
            this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }

        Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
        this.earlyApplicationEvents = null;
        if (earlyEventsToProcess != null) {
            Iterator var9 = earlyEventsToProcess.iterator();

            while (var9.hasNext()) {
                ApplicationEvent earlyEvent = (ApplicationEvent) var9.next();
                this.getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }
    }
}
```

真正的消息发送，实际上是通过 ApplicationEventMulticaster 这个类来完成的。它通过线程池，支持异步非阻塞、同步阻塞这两种类型的观察者模式。

```java
public void multicastEvent(ApplicationEvent event) {
    this.multicastEvent(event, this.resolveDefaultEventType(event));
}

public void multicastEvent(final ApplicationEvent event, ResolvableType eventType) {
    ResolvableType type = eventType != null ? eventType : this.resolveDefaultEventType(event);
    Iterator var4 = this.getApplicationListeners(event, type).iterator();

    while (var4.hasNext()) {
        final ApplicationListener<?> listener = (ApplicationListener) var4.next();
        Executor executor = this.getTaskExecutor();
        if (executor != null) {
            executor.execute(new Runnable() {
                public void run() {
                    SimpleApplicationEventMulticaster.this.invokeListener(listener, event);
                }
            });
        } else {
            this.invokeListener(listener, event);
        }
    }

}
```

借助 Spring 提供的观察者模式的骨架代码，如果要在 Spring 下实现某个事件的发送和监听，只需要做很少的工作，定义事件、定义监听器、往
ApplicationContext 中发送事件就可以了，剩下的工作都由 Spring 框架来完成。实际上，这也体现了 Spring
框架的扩展性，也就是在不需要修改任何代码的情况下，扩展新的事件和监听。

### 模板模式在 Spring 中的应用

参考代码如下：

- org.springframework.beans.factory.InitializingBean
- org.springframework.beans.factory.DisposableBean
- org.springframework.beans.factory.config.BeanPostProcessor
    - postProcessBeforeInitialization(...)
    - postProcessAfterInitialization(...)

这里的模板模式的实现，并不是标准的抽象类的实现方式，而是有点类似 Callback 回调的实现方式，也就是将要执行的函数封装成对象（比如，初始化方法封装成
InitializingBean 对象），传递给模板（BeanFactory）来执行。

### 适配器模式在 Spring 中的应用

> 在 Spring MVC 中，定义一个 Controller 最常用的方式是，通过 @Controller 注解来标记某个类是 Controller 类，通过
> @RequestMapping 注解来标记函数对应的 URL。

定义一个 Controller 有三种方式：

```java
// 方法一：通过 @Controller、@RequestMapping 来定义
@Controller
public class DemoController {
    @RequestMapping("/employname")
    public ModelAndView getEmployeeName() {
        ModelAndView model = new ModelAndView("Greeting");
        model.addObject("message", "Dinesh");
        return model;
    }
}

// 方法二：实现 Controller接口 + xml 配置文件:配置 DemoController 与 URL 的对应关系
public class DemoController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ModelAndView model = new ModelAndView("Greeting");
        model.addObject("message", "Dinesh Madhwal");
        return model;
    }
}

// 方法三：实现 Servlet 接口 + xml 配置文件:配置 DemoController 类与 URL 的对应关系
public class DemoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("Hello World.");
    }
}
```

不同方式定义的 Controller，其函数的定义（函数名、入参、返回值等）是不统一的。如上示例代码所示：

- 方法一中的函数的定义很随意、不固定；
- 方法二中的函数定义是 handleRequest()；
- 方法三中的函数定义是 service()（看似是定义了 doGet()、doPost()，实际上，这里用到了模板模式，Servlet 中的 service() 调用了
  doGet() 或 doPost() 方法，DispatcherServlet 调用的是 service() 方法）。
    - DispatcherServlet 需要根据不同类型的 Controller，调用不同的函数。

Spring 的代码实现：

Spring 定义了统一的接口 HandlerAdapter，并且对每种 Controller
定义了对应的适配器类。这些适配器类包括：AnnotationMethodHandlerAdapter、SimpleControllerHandlerAdapter、SimpleServletHandlerAdapter
等

```java
import org.springframework.stereotype.Controller;

public interface HandlerAdapter {
    boolean supports(Object var1);

    ModelAndView handle(HttpServletRequest var1, HttpServletResponse var2, Object var3) throws Exception;

    long getLastModified(HttpServletRequest var1, Object var2);
}

// 对应实现 Controller 接口的 Controller
public class SimpleControllerHandlerAdapter implements HandlerAdapter {
    public SimpleControllerHandlerAdapter() {
    }

    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return ((Controller) handler).handleRequest(request, response);
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        return handler instanceof LastModified ? ((LastModified) handler).getLastModified(request) : -1L;
    }
}

// 对应实现 Servlet 接口的 Controller
public class SimpleServletHandlerAdapter implements HandlerAdapter {
    public SimpleServletHandlerAdapter() {
    }

    public boolean supports(Object handler) {
        return handler instanceof Servlet;
    }

    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ((Servlet) handler).service(request, response);
        return null;
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1L;
    }
}

// AnnotationMethodHandlerAdapter 对应通过注解实现的 Controller
```

在 DispatcherServlet 类中，就不需要区分对待不同的 Controller 对象了，统一调用 HandlerAdapter 的 handle() 函数就可以了。

### 策略模式在 Spring 中的应用

> Spring AOP 是通过动态代理来实现的。Spring 支持两种动态代理实现方式，一种是 JDK 提供的动态代理实现方式，另一种是 Cglib
> 提供的动态代理实现方式。前者需要被代理的类有抽象的接口定义，后者不需要。

策略模式包含三部分，策略的定义、创建和使用。

- 策略的定义
    - org.springframework.aop.framework.AopProxy
- 策略的创建：一般通过工厂方法来实现
    - org.springframework.aop.framework.AopProxyFactory
    - org.springframework.aop.framework.DefaultAopProxyFactory

策略模式的典型应用场景，一般是通过环境变量、状态值、计算结果等动态地决定使用哪个策略。对应到 Spring 源码中，可以参看刚刚给出的
DefaultAopProxyFactory 类中的 createAopProxy() 函数的代码实现。

```java
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.AdvisedSupport;

//用来判断用哪个动态代理实现方式 
private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
    Class[] ifcs = config.getProxiedInterfaces();
    return ifcs.length == 0 || ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0]);
}
```

### 组合模式在 Spring 中的应用

Spring Cache。Spring Cache 提供了一套抽象的 Cache 接口。使用它能够统一不同缓存实现（Redis、Google Guava…）的不同的访问方式。Spring
中针对不同缓存实现的不同缓存访问类，都依赖这个接口，比如：EhCacheCache、GuavaCache、NoOpCache、RedisCache、JCacheCache、ConcurrentMapCache、CaffeineCache。

参考代码如下：

- org.springframework.cache.Cache

为了管理多个缓存，Spring 还提供了缓存管理功能。不过，它包含的功能很简单，主要有这样两部分：一个是根据缓存名字（创建 Cache
对象的时候要设置 name 属性）获取 Cache 对象；另一个是获取管理器管理的所有缓存的名字列表。

对应的 Spring 源码如下所示：

- org.springframework.cache.CacheManager
- org.springframework.cache.support.CompositeCacheManager

### 装饰器模式在 Spring 中的应用

> 事务功能，Spring 使用到了装饰器模式。TransactionAwareCacheDecorator 增加了对事务的支持，在事务提交、回滚的时候分别对 Cache
> 的数据进行处理。

TransactionAwareCacheDecorator 实现 Cache 接口，并且将所有的操作都委托给 targetCache
来实现，对其中的写操作添加了事务功能。这是典型的装饰器模式的应用场景和代码实现。

参考代码如下：

- org.springframework.cache.transaction.TransactionAwareCacheDecorator

### 工厂模式在 Spring 中的应用

在 Spring 中，工厂模式最经典的应用莫过于实现 IOC 容器，对应的 Spring 源码主要是 BeanFactory 类和 ApplicationContext
相关类(AbstractApplicationContext、ClassPathXmlApplicationContext、FileSystemXmlApplicationContext…)。

参考代码如下：

- org.springframework.beans.factory.BeanFactory
- org.springframework.context.ApplicationContext
    - org.springframework.context.support.AbstractApplicationContext
    - org.springframework.context.support.ClassPathXmlApplicationContext
    - org.springframework.context.support.FileSystemXmlApplicationContext


