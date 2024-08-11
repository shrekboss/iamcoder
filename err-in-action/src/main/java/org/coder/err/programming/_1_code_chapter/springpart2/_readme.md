## Spring框架：框架帮我们做了很多工作也带来了复杂度

Spring 框架内部的复杂度主要表现为三点：

- Spring 框架借助 IoC 和 AOP 的功能，实现了修改、拦截 Bean 的定义和实例的灵活性，因此真正执行的代码流程并不是串行的。
- Spring Boot 根据当前依赖情况实现了自动配置，虽然省去了手动配置的麻烦，但也因此多了一些黑盒、提升了复杂度。
- Spring Cloud 模块多版本也多，Spring Boot 1.x 和 2.x 的区别也很大。如果要对 Spring Cloud 或 Spring Boot
  进行二次开发的话，考虑兼容性的成本会很高

### 1. Feign AOP切不到的诡异案例

参考代码：[aopfeign](aopfeign)

> 使用 Spring Cloud 做微服务调用，为方便统一处理 Feign，想到了用 AOP 实现，即使用 within 指示器匹
> 配 feign.Client 接口的实现进行 AOP 切入。一开始这个项目使用的是客户端的负载均衡，也就是让 Ribbon
> 来做负载均衡，代码没啥问题。后来因为后端服务通过 Nginx 实现服务端负载均衡，所以把 @FeignClient
> 的配置设置了 URL 属性，直接通过一个固定 URL 调用后端服务
>
> 小技巧：如果你希望知道一个类是怎样调用栈初始化的，可以在构造方法中设置一个断点进行调试。

FeignClient 的创建过程，也就是分析 FeignClientFactoryBean 类的 getTarget 方法

- Feign 指定 URL

    - 当 URL 没有内容也就是为空或者不配置时调用 loadBalance 方法，在其内部通过 FeignContext 从容器获取 feign.Client 的实例。
    - client 是 LoadBalanceFeignClient，已经是经过代理增强的，明显是一个 Bean
    - org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget

      ```java
      <T> T getTarget() {
        FeignContext context = this.applicationContext.getBean(FeignContext.class);
        Feign.Builder builder = feign(context);
        if (!StringUtils.hasText(this.url)) {
          ...
          return (T) loadBalance(builder, context,
              new HardCodedTarget<>(this.type, this.name, this.url));
        }
        ...
        String url = this.url + cleanPath();
        Client client = getOptional(context, Client.class);
        if (client != null) {
          if (client instanceof LoadBalancerFeignClient) {
            // not load balancing because we have a url,
            // but ribbon is on the classpath, so unwrap
            client = ((LoadBalancerFeignClient) client).getDelegate();
          }
          builder.client(client);
        }
        ...
      }
      protected <T> T loadBalance(Feign.Builder builder, FeignContext context,
          HardCodedTarget<T> target) {
        Client client = getOptional(context, Client.class);
        if (client != null) {
          builder.client(client);
          Targeter targeter = get(context, Targeter.class);
          return targeter.target(this, builder, context, target);
        }
      ...
      }
      protected <T> T getOptional(FeignContext context, Class<T> type) {
        return context.getInstance(this.contextId, type);
      }
      ```
        - 当 URL 没有内容也就是为空或者不配置时调用 loadBalance 方法，在其内部通过 FeignContext 从容器获取 feign.Client
          的实例
- Feign 不指定 URL
    - 当 URL 不为空的时候，client 设置为了 LoadBalanceFeignClient 的 delegate 属性。其原因注释中有
      提到，因为有了 URL 就不需要客户端负载均衡了，但因为 Ribbon 在 classpath 中，所以需要从
      LoadBalanceFeignClient 提取出真正的 Client。
    - client 是一个 ApacheHttpClient
    - HttpClientFeignLoadBalancedConfiguration 类实例化的 ApacheHttpClient，LoadBalancerFeignClient
      这个 Bean 在实例化的时候，new 出来一个 ApacheHttpClient 作为 delegate 放到了
      LoadBalancerFeignClient 中，说明 ApacheHttpClient 不是一个 Bean。

  为什么 within(feign.Client+) 无法切入设置过 URL

    - 表达式声明的是切入 feign.Client 的实现类。
    - Spring 只能切入由自己管理的 Bean。虽然 LoadBalancerFeignClient 和 ApacheHttpClient 都是 feign.Client 接口的实现，但是
      HttpClientFeignLoadBalancedConfiguration 的自动配置只是把前者定义为 Bean，后者是 new 出来的、作为了
      LoadBalancerFeignClient 的 delegate，不是 Bean。
    - 在定义了 FeignClient 的 URL 属性后，我们获取的是 LoadBalancerFeignClient 的 delegate，它不是 Bean。

    ```java
    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client feignClient(CachingSpringLoadBalancerFactory cachingFactory,
                              SpringClientFactory clientFactory, HttpClient httpClient) {
      ApacheHttpClient delegate = new ApacheHttpClient(httpClient);
      return new LoadBalancerFeignClient(delegate, cachingFactory, clientFactory);
    }
    
    public LoadBalancerFeignClient(Client delegate,
                                   CachingSpringLoadBalancerFactory lbClientFactory,
                                   SpringClientFactory clientFactory) {
      this.delegate = delegate;
      this.lbClientFactory = lbClientFactory;
      this.clientFactory = clientFactory;
    }
    ```

    - ApacheHttpClient 其实有机会独立成为 Bean。查看 HttpClientFeignConfiguration 的源码可以发现，
      当没有 ILoadBalancer 类型的时候，自动装配会把 ApacheHttpClient 设置为 Bean。

    ```java
    import feign.httpclient.ApacheHttpClient;
    import org.apache.http.impl.client.CloseableHttpClient;
    import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
    import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
    import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
    import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
  
    @Configuration
    @ConditionalOnClass(ApacheHttpClient.class)
    // 如果我们不希望做客户端负载均衡的话，应该不会引用 Ribbon 组件的依赖，自然没有 
    // LoadBalancerFeignClient，只有 ApacheHttpClient
    @ConditionalOnMissingClass("com.netflix.loadbalancer.ILoadBalancer")
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    @ConditionalOnProperty(value = "feign.httpclient.enabled", matchIfMissing = true)
    protected static class HttpClientFeignConfiguration {
        @Bean
        @ConditionalOnMissingBean(Client.class)
        public Client feignClient(HttpClient httpClient) {
            return new ApacheHttpClient(httpClient);
        }
   }
  ```

    - pom.xml 中的 ribbon 模块注释之后
  ```xml
  <dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
  </dependency>
  ```

```txt
// 启动报错
Caused by: java.lang.IllegalArgumentException: Cannot subclass final class feign.httpclient.ApacheHttpClient
  at org.springframework.cglib.proxy.Enhancer.generateClass(Enhancer.java:657)
  at org.springframework.cglib.core.DefaultGeneratorStrategy.generate(DefaultGeneratorStrategy.java:25)
```

Spring 实现动态代理的两种方式：

- JDK 动态代理，通过反射实现，只支持对实现接口的类进行代理；
- CGLIB 动态字节码注入方式，通过继承实现代理，没有这个限制。

Spring Boot 2.x 默认使用 CGLIB 的方式，但通过继承实现代理有个问题是，无法继承 final 的类。因为，ApacheHttpClient 类就是定义为了
final。切换到使用 JDK 动态代理的方式：
`spring.aop.proxy-target-class=false`

```java
public final class ApacheHttpClient implements Client {...
}
```

### 2. Spring程序配置的优先级问题

参考代码：[propertysource](propertysource)

#### Spring 通过环境 Environment 抽象出的 Property 和 Profile

- Property，又抽象出各种 PropertySource 类代表配置源。一个环境下可能有多个配置源，每个配置源
  中有诸多配置项。在查询配置信息时，需要按照配置源优先级进行查询。
- Profile 定义了场景的概念。通常，会定义类似 dev、test、stage 和 prod 等环境作为不同的 Profile，用于
  按照场景对 Bean 进行逻辑归属。同时，Profile 和配置文件也有关系，每个环境都有独立的配置文件，但只会激活某一个环境来生效特定环境的配置文件。
  ![undefined](http://ww1.sinaimg.cn/large/002eBIeDgy1gu8pb89q7cj61060uo0vt02.jpg)

#### StandardEnvironment

对于非 Web 应用，Spring 对于 Environment 接口的实现是 StandardEnvironment 类。通过 Spring 注入 StandardEnvironment 后循环
getPropertySources 获得的 PropertySource，来查询所有的 PropertySource 中 key 的属性值；然后遍历 getPropertySources
方法，获得所有配置源并打印出来。

```text
ConfigurationPropertySourcesPropertySource {name='configurationProperties'} -> yizhe.chen 实际取值：yizhe.chen
PropertiesPropertySource {name='systemProperties'} -> yizhe.chen 实际取值：yizhe.chen
OriginTrackedMapPropertySource {name='applicationConfig: [classpath:/application.properties]'} -> defaultadminname 实际取值：yizhe.chen
ConfigurationPropertySourcesPropertySource {name='configurationProperties'} -> 12345 实际取值：12345
OriginAwareSystemEnvironmentPropertySource {name='systemEnvironment'} -> 12345 实际取值：12345
OriginTrackedMapPropertySource {name='applicationConfig: [classpath:/application.properties]'} -> 45679 实际取值：12345

配置优先级：

ConfigurationPropertySourcesPropertySource {name='configurationProperties'} √
StubPropertySource {name='servletConfigInitParams'}
ServletContextPropertySource {name='servletContextInitParams'}
PropertiesPropertySource {name='systemProperties'} √ JVM 系统配置
OriginAwareSystemEnvironmentPropertySource {name='systemEnvironment'}
RandomValuePropertySource {name='random'}
OriginTrackedMapPropertySource {name='applicationConfig: [classpath:/application.properties]'} √ 配置文件配置
MapPropertySource {name='springCloudClientHostInfo'}
MapPropertySource {name='defaultProperties'}
```

![undefined](http://ww1.sinaimg.cn/large/002eBIeDgy1gu910i10buj61lc14aaia02.jpg)

##### 配置优先级

- ConfigurationPropertySourcesPropertySource {name='configurationProperties'} √
- StubPropertySource {name='servletConfigInitParams'}
- ServletContextPropertySource {name='servletContextInitParams'}
- PropertiesPropertySource {name='systemProperties'} √ JVM 系统配置
- OriginAwareSystemEnvironmentPropertySource {name='systemEnvironment'}
- RandomValuePropertySource {name='random'}
- OriginTrackedMapPropertySource {name='applicationConfig: [classpath:/application.properties]'} √ 配置文件配置
- MapPropertySource {name='springCloudClientHostInfo'}
- MapPropertySource {name='defaultProperties'}

##### AbstractEnvironment

> StandardEnvironment，继承的是 AbstractEnvironment（图中紫色类）

- MutablePropertySources 类型的字段 propertySources，代表了所有配置源；
- getProperty 方法，通过 PropertySourcesPropertyResolver 类进行查询配置；
- 实例化 PropertySourcesPropertyResolver 的时候，传入了当前的 MutablePropertySources。

  ```java
  public abstract class AbstractEnvironment implements ConfigurableEnvironment {
    private final MutablePropertySources propertySources = new MutablePropertySources();
    private final ConfigurablePropertyResolver propertyResolver =
        new PropertySourcesPropertyResolver(this.propertySources);
  
    public String getProperty(String key) {
      return this.propertyResolver.getProperty(key);
    }
  }
  ```

##### MutablePropertySources

> MutablePropertySources 的源码（图中蓝色类）

- propertySourceList 字段用来真正保存 PropertySource 的 List，且这个 List 是一个 CopyOnWriteArrayList。
- 类中定义了 addFirst、addLast、addBefore、addAfter 等方法，来精确控制 PropertySource 加入
  propertySourceList 的顺序。这也说明了顺序的重要性。

  ```java
  public class MutablePropertySources implements PropertySources {
  
    private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();
  
    public void addFirst(PropertySource<?> propertySource) {
      removeIfPresent(propertySource);
      this.propertySourceList.add(0, propertySource);
    }
    public void addLast(PropertySource<?> propertySource) {
      removeIfPresent(propertySource);
      this.propertySourceList.add(propertySource);
    }
    public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
      ...
      int index = assertPresentAndGetIndex(relativePropertySourceName);
      addAtIndex(index, propertySource);
    }
      public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
         ...
         int index = assertPresentAndGetIndex(relativePropertySourceName);
         addAtIndex(index + 1, propertySource);
      }
      private void addAtIndex(int index, PropertySource<?> propertySource) {
         removeIfPresent(propertySource);
         this.propertySourceList.add(index, propertySource);
      }
  }
  ```

##### PropertySourcesPropertyResolver

> PropertySourcesPropertyResolver（图中绿色类）的源码，真正查询配置的方法 getProperty：遍历的 propertySources 是
> PropertySourcesPropertyResolver 构造方法传入的，再结合 AbstractEnvironment 的源码可以发现，这个 propertySources 正是
> AbstractEnvironment 中的 MutablePropertySources 对象。遍历时，如果发现配置源中有对应的 Key
> 值，则使用这个值。因此，MutablePropertySources 中配置源的次序尤为重要

```java
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {
    private final PropertySources propertySources;

    public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Searching for key '" + key + "' in PropertySource '" +
                            propertySource.getName() + "'");
                }
                Object value = propertySource.getProperty(key);
                if (value != null) {
                    if (resolveNestedPlaceholders && value instanceof String) {
                        value = resolveNestedPlaceholders((String) value);
                    }
                    logKeyFound(key, propertySource, value);
                    return convertValueIfNecessary(value, targetValueType);
                }
            }
        }
    ...
    }
}
```

处在第一位的是 ConfigurationPropertySourcesPropertySource，这是什么呢？

- 其实，它不是一个实际存在的配置源，扮演的是一个代理的角色。但通过调试会发现，获取的值竟然是由它提供并且返回的，且没有循环遍历后面的
  PropertySource。
- ConfigurationPropertySourcesPropertySource 是所有配置源中的第一个，实现了对
  PropertySourcesPropertyResolver 中遍历逻辑的“劫持”，并且知道了其遍历逻辑。
- getProperty 方法其实是通过 findConfigurationProperty 方法查询配置的。遍历所有的配置源

  ```java
  class ConfigurationPropertySourcesPropertySource extends PropertySource<Iterable<ConfigurationPropertySource>>
      implements OriginLookup<String> {
  
    ConfigurationPropertySourcesPropertySource(String name, Iterable<ConfigurationPropertySource> source) {
      super(name, source);
    }
  
    @Override
    public Object getProperty(String name) {
      ConfigurationProperty configurationProperty = findConfigurationProperty(name);
      return (configurationProperty != null) ? configurationProperty.getValue() : null;
    }
    
    private ConfigurationProperty findConfigurationProperty(String name) {
      try {
        return findConfigurationProperty(ConfigurationPropertyName.of(name, true));
      }
      catch (Exception ex) {
        return null;
      }
    }
    
    private ConfigurationProperty findConfigurationProperty(ConfigurationPropertyName name) {
      if (name == null) {
        return null;
      }
      for (ConfigurationPropertySource configurationPropertySource : getSource()) {
        ConfigurationProperty configurationProperty = configurationPropertySource.getConfigurationProperty(name);
        if (configurationProperty != null) {
          return configurationProperty;
        }
      }
      return null;
    }
  }
  ```

- 循环遍历（getSource() 的结果）的配置源，其实是 SpringConfigurationPropertySources（图中黄色类），其中包含的配置源列表就是之前看到的
  9 个配置源，而第一个就是 ConfigurationPropertySourcesPropertySource
- SpringConfigurationPropertySources 可以发现，它返回的迭代器是内部类 SourcesIterator，在 fetchNext 方法获取下一个项时，通过
  isIgnored 方法排除了 ConfigurationPropertySourcesPropertySource

  ```java
  
  class SpringConfigurationPropertySources implements Iterable<ConfigurationPropertySource> {
  
    private final Iterable<PropertySource<?>> sources;
    private final Map<PropertySource<?>, ConfigurationPropertySource> cache = new ConcurrentReferenceHashMap<>(16,
        ReferenceType.SOFT);
  
    SpringConfigurationPropertySources(Iterable<PropertySource<?>> sources) {
      Assert.notNull(sources, "Sources must not be null");
      this.sources = sources;
    }
  
    @Override
    public Iterator<ConfigurationPropertySource> iterator() {
      return new SourcesIterator(this.sources.iterator(), this::adapt);
    }
  
    private static class SourcesIterator implements Iterator<ConfigurationPropertySource> {
  
      @Override
      public boolean hasNext() {
        return fetchNext() != null;
      }
  
      private ConfigurationPropertySource fetchNext() {
        if (this.next == null) {
          if (this.iterators.isEmpty()) {
            return null;
          }
          if (!this.iterators.peek().hasNext()) {
            this.iterators.pop();
            return fetchNext();
          }
          PropertySource<?> candidate = this.iterators.peek().next();
          if (candidate.getSource() instanceof ConfigurableEnvironment) {
            push((ConfigurableEnvironment) candidate.getSource());
            return fetchNext();
          }
          if (isIgnored(candidate)) {
            return fetchNext();
          }
          this.next = this.adapter.apply(candidate);
        }
        return this.next;
      }
  
  
      private void push(ConfigurableEnvironment environment) {
        this.iterators.push(environment.getPropertySources().iterator());
      }
  
  
      private boolean isIgnored(PropertySource<?> candidate) {
        return (candidate instanceof StubPropertySource
            || candidate instanceof ConfigurationPropertySourcesPropertySource);
      }
    }
  }
  ```

##### ConfigurationPropertySourcesPropertySource

ConfigurationPropertySourcesPropertySource 的源码

- getProperty 方法其实是通过 findConfigurationProperty 方法查询配置的，其实还是在遍历所有的配置源。
- 循环遍历（getSource() 的结果）的配置源，其实是 SpringConfigurationPropertySources（图中黄色类），其中包含的配置源列表就是之前看到的
  9 个配置源，而第一个就是
  ConfigurationPropertySourcesPropertySource。
- 同时观察 configurationProperty 可以看到，这个 ConfigurationProperty 其实类似代理的角色，实际配置
  是从系统属性中获得的
- 查看 SpringConfigurationPropertySources 可以发现，它返回的迭代器是内部类 SourcesIterator，在
  fetchNext 方法获取下一个项时，通过 isIgnored 方法排除了 ConfigurationPropertySourcesPropertySource

ConfigurationPropertySourcesPropertySource 如何让自己成为第一个配置源呢？

- ConfigurationPropertySourcesPropertySource 类是由 ConfigurationPropertySources 的 attach 方法实例
  化的。查阅源码可以发现，这个方法的确从环境中获得了原始的 MutablePropertySources，把自己加入成为一个元素。

  ```java
  public final class ConfigurationPropertySources {
    public static void attach(Environment environment) {
      MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
      PropertySource<?> attached = sources.get(ATTACHED_PROPERTY_SOURCE_NAME);
      if (attached == null) {
        sources.addFirst(new ConfigurationPropertySourcesPropertySource(ATTACHED_PROPERTY_SOURCE_NAME,
            new SpringConfigurationPropertySources(sources)));
      }
    }
  }
  ```

- 而这个 attach 方法，是 Spring 应用程序启动时准备环境的时候调用的。在 SpringApplication 的 run 方
  法中调用了 prepareEnvironment 方法，然后又调用了 ConfigurationPropertySources.attach 方法。

  ```java
  public class SpringApplication {
  
  public ConfigurableApplicationContext run(String... args) {
      ...
      try {
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
        ...
    }
    private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners,
        ApplicationArguments applicationArguments) {
      ...
      ConfigurationPropertySources.attach(environment);
      ...
      }
  }
  ```

### 3. 替换配置属性中占位符的例子

参考代码：[custompropertysource](custompropertysource)

- 利用 PropertySource 具有优先级的特点，实现配置文件中属性值的自动赋值。主要逻辑是，遍历现在的属性值，找出能匹配到占位符的属性，并把这些属性的值替换为实际的数据库信息，然后再把这些替换后的属性值构成新的
  PropertiesPropertySource，加入 PropertySources 的第一个
- 目的就是不希望让开发人员手动配置数据库信息，希望程序启动的时候自动替换占位符实现自动配置（从 CMDB 直接拿着应用程序 ID
  来换取对应的数据库信息。你可能会问了，一个应用程序 ID
  对应多个数据库怎么办？其实，一般对于微服务系统来说，一个应用就应该对应一个数据库）。这样一来，除了程序其他人都不会接触到生产的数据库信息，会更安全

### 4. 关于这些指示器的作用

按照使用场景，建议使用下面这些指示器：

- 针对方法签名，使用 execution；
- 针对类型匹配，使用 within（匹配类型）、this（匹配代理类实例）、target（匹配代理背后的目标类实例）、args（匹配参数）；
- 针对注解匹配，使用 @annotation（使用指定注解标注的方法）、@target（使用指定注解标注的类）、@args（使用指定注解标注的类作为某个方法的参数）。

@within 怎么没有呢？

- 对于 Spring 默认的基于动态代理或 CGLIB 的 AOP，因为切点只能是方法，使用 @within 和 @target 指示器并无区别；但需要注意如果切换到
  AspectJ，那么使用 @within 和 @target 这两个指示器的行为就会有所区别了，@within 会切入更多的成员的访问（比如静态构造方法、字段访问），一般而言使用
  @target 指示器即可