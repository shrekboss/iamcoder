## 性能计数器框架

### 项目背景

> 开发一个小的框架，能够获取接口调用的各种统计信
> 息，比如，响应时间的最大值（max）、最小值（min）、平均值（avg）、百分位值（percentile）、接口调用次数（count）、频率（tps）
> 等，并且支持将统计结果以各种显示格式（比如：JSON 格式、网页格式、自定义显示格式等）输出到各种终端（Console 命令行、HTTP
> 网页、Email、日志文件、自定义输出终端等），以方便查看。

### 需求分析

#### 1. 功能性需求分析

- 接口统计信息：包括接口响应时间的统计信息，以及接口调用次数的统计信息等。
- 统计信息的类型：max、min、avg、percentile、count、tps 等。
- 统计信息显示格式：Json、Html、自定义显示格式。
- 统计信息显示终端：Console、Email、HTTP 网页、日志、自定义显示终端。

下面几个隐藏的需求

- 统计触发方式：包括主动和被动两种。主动表示以一定的频率定时统计数据，并主动推送到显示终端，比如邮件推送。被动表示用户触发统计，比如用户在网页中选择要统计的时间区间，触发统计，并将结果显示给用户。
- 统计时间区间：框架需要支持自定义统计时间区间，比如统计最近 10 分钟的某接口的 tps、访问次数，或者统计 12 月 11 日 00 点到
  12 月 12 日 00 点之间某接口响应时间的最大值、最小值、平均值等。
- 统计时间间隔：对于主动触发统计，我们还要支持指定统计时间间隔，也就是多久触发一次统计显示。比如，每间隔 10s
  统计一次接口信息并显示到命令行中，每间隔 24 小时发送一封统计信息邮件。

#### 2. 非功能性需求分析

- 易用性
- 性能
- 扩展性
    - 之前讲到的扩展是从框架代码开发者的角度来说的。这里所说的扩展是从框架使用者的角度来说的，特指使用者可以在不修改框架源码，甚至不拿到框架源码的情况下，为框架扩展新的功能。这就有点类似给框架开发插件。

feign 是一个 HTTP 客户端框架，我们可以在不修改框架源码的情况下，用如下方式来扩展我们自己的编解码方式、日志、拦截器等。

```java
Feign feign = Feign.builder()
        .logger(new CustomizedLogger())
        .encoder(new FormEncoder(new JacksonEncoder()))
        .decoder(new JacksonDecoder())
        .errorDecoder(new ResponseErrorDecoder())
        .requestInterceptor(new RequestHeadersInterceptor()).build();

public class RequestHeadersInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("appId", "...");
        template.header("version", "...");
        template.header("timestamp", "...");
        template.header("token", "...");
        template.header("idempotent-token", "...");
        template.header("sequence-id", "...");
    }

    public class CustomizedLogger extends feign.Logger {
        //...
    }

    public class ResponseErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {
            //...
        }
    }
}
```

- 容错性
- 通用性

### 框架设计

基于最小原型实现的参考代码：

- [Metrics.java](prototype%2FMetrics.java)
- [UserController.java](prototype%2FUserController.java)

整个框架分为四个模块：数据采集、存储、聚合统计、显示。每个模块负责的工作简单罗列如下:

- 数据采集：负责打点采集原始数据，包括记录每次接口请求的响应时间和请求时间。数据采集过程要高度容错，不能影响到接口本身的可用性。除此之外，因为这部分功能是暴露给框架的使用者的，所以在设计数据采集
  API 的时候，也要尽量考虑其易用性。
- 存储：负责将采集的原始数据保存下来，以便后面做聚合统计。数据的存储方式有多种，比如：Redis、MySQL、HBase、日志、文件、内存等。数据存储比较耗时，为了尽量地减少对接口性能（比如响应时间）的影响，采集和存储的过程异步完成。
- 聚合统计：负责将原始数据聚合为统计数据，比如：max、min、avg、percentile、count、tps 等。为了支持更多的聚合统计规则，代码希望尽可能灵活、可扩展。
- 显示：负责将统计数据以某种格式显示到终端，比如：输出到命令行、邮件、网页、自定义显示终端等。

### 小步快跑、逐步迭代

在 v1.0 版本中，暂时只实现下面这些功能

- 数据采集：负责打点采集原始数据，包括记录每次接口请求的响应时间和请求时间。
- 存储：负责将采集的原始数据保存下来，以便之后做聚合统计。数据的存储方式有很多种，暂时只支持 Redis 这一种存储方式，并且，采集与存储两个过程同步执行。
- 聚合统计：负责将原始数据聚合为统计数据，包括响应时间的最大值、最小值、平均值、99.9 百分位值、99 百分位值，以及接口请求的次数和
  tps。
- 显示：负责将统计数据以某种格式显示到终端，暂时只支持主动推送给命令行和邮件。命令行间隔 n 秒统计显示上 m 秒的数据（比如，间隔
  60s 统计上 60s 的数据）。邮件每日统计上日的数据。

#### 划分职责进而识别出有哪些类

根据需求描述，大致识别出下面几个接口或类：

- MetricsCollector 类负责提供 API，来采集接口请求的原始数据。可以为 MetricsCollector 抽象出一个接口，但这并不是必须的，因为暂时只能想到一个
  MetricsCollector 的实现方式。
- MetricsStorage 接口负责原始数据存储，RedisMetricsStorage 类实现 MetricsStorage 接口。这样做是为了今后灵活地扩展新的存储方法，比如用
  HBase 来存储。
    - 注意，一次性取太长时间区间的数据，可能会导致拉取太多的数据到内存中，有可能会撑爆内存。对于 Java 来说，就有可能会触发
      OOM（Out Of Memory）。而且，即便不出现 OOM，内存还够用，但也会因为内存吃紧，导致频繁的 Full GC，进而导致系统接口请求处理变慢，甚至超时。
- Aggregator 类负责根据原始数据计算统计数据。
- ConsoleReporter 类、EmailReporter 类分别负责以一定频率统计并发送统计数据到命令行和邮件。至于 ConsoleReporter 和
  EmailReporter 是否可以抽象出可复用的抽象类，或者抽象出一个公共的接口，暂时还不能确定。

统计显示所要完成的功能逻辑细分一下的话，主要包含下面 4 点：

1. 根据给定的时间区间，从数据库中拉取数据；
2. 根据原始数据，计算得到统计数据；
3. 将统计数据显示到终端（命令行或邮件）；
4. 定时触发以上 3 个过程的执行。#### 定义类及属性和方法，定义类与类之间的关系

#### 将类组装起来并提供执行入口

参考代码：[Bootstrap.java](v1%2FBootstrap.java)

### Review 设计与实现

[MetricsCollector.java](v1%2FMetricsCollector.java)

负责采集和存储数据，职责相对来说还算比较单一。它基于接口而非实现编程，通过依赖注入的方式来传递 MetricsStorage 对象，可以在不需要修改代码的情况下，灵活地替换不同的存储方式，满足开闭原则。

[MetricsStorage.java](v1%2FMetricsStorage.java) 
[RedisMetricsStorage.java](v1%2FRedisMetricsStorage.java)

MetricsStorage 和 RedisMetricsStorage 的设计比较简单。当需要实现新的存储方式的时候，只需要实现 MetricsStorage 接口即可。因为所有用到 MetricsStorage 和 RedisMetricsStorage 的地方，都是基于相同的接口函数来编程的，所以，除了在组装类的地方有所改动（从 RedisMetricsStorage 改为新的存储实现类），其他接口函数调用的地方都不需要改动，满足开闭原则。

[Aggregator.java](v1%2FAggregator.java)

Aggregator 类是一个工具类，里面只有一个静态函数，有 50 行左右的代码量，负责各种统计数据的计算。当需要扩展新的统计功能的时候，需要修改 aggregate() 函数代码，并且一旦越来越多的统计功能添加进来之后，这个函数的代码量会持续增加，可读性、可维护性就变差了。所以，从刚刚的分析来看，这个类的设计可能存在职责不够单一、不易扩展等问题，需要在之后的版本中，对其结构做优化。

[ConsoleReporter.java](v1%2FConsoleReporter.java)
[EmailReporter.java](v1%2FEmailReporter.java)

ConsoleReporter 和 EmailReporter 中存在代码重复问题。在这两个类中，从数据库中取数据、做统计的逻辑都是相同的，可以抽取出来复用，否则就违反了 DRY 原则。而且整个类负责的事情比较多，职责不是太单一。特别是显示部分的代码，可能会比较复杂（比如 Email 的展示方式），最好是将显示部分的代码逻辑拆分成独立的类。除此之外，因为代码中涉及线程操作，并且调用了 Aggregator 的静态函数，所以代码的可测试性不好。
