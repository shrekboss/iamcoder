## 业务代码写完，就意味着生产就绪了？

生产就绪需准备工作

- 第一，提供健康检测接口。传统采用 ping 的方式对应用进行探活检测并不准确。有的时候，应用的关键内部或外部依赖已经离线，导致其根本无法正常工作，但其对外的 Web 端口或管理端口是可以 ping 通的。我们应该提供一个专有的监控检测接口，并尽可能触达一些内部组件。
- 第二，暴露应用内部信息。应用内部诸如线程池、内存队列等组件，往往在应用内部扮演了重要的角色，如果应用或应用框架可以对外暴露这些重要信息，并加以监控，那么就有可能在诸如 OOM 等重大问题暴露之前发现蛛丝马迹，避免出现更大的问题。
- 第三，建立应用指标 Metrics 监控。Metrics 可以翻译为度量或者指标，指的是对于一些关键信息以可聚合的、数值的形式做定期统计，并绘制出各种趋势图表。这里的指标监控，包括两个方面：
    - 一是，应用内部重要组件的指标监控，比如 JVM 的一些指标、接口的 QPS 等；
    - 二是，应用的业务数据的监控，比如电商订单量、游戏在线人数等。

### 1. 准备工作：配置 Spring Boot Actuator

> Actuator 模块，封装了诸如健康检测、应用内部信息、Metrics 指标等生产就绪的功能

[Spring Boot 管理工具 Spring Boot Admin](https://github.com/codecentric/spring-boot-admin)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
management.server.port=45679
management.endpoints.web.exposure.include=*
# http://localhost:45679/admin ，来查看 Actuator 的所有功能 URL
management.endpoints.web.base-path=/admin
management.endpoint.health.show-details=always
```



### 2. 健康检测需要触达关键组件：[health](health)

> 额外补充一下，[Spring Boot 2.3.0](https://spring.io/blog/2020/03/25/liveness-and-readiness-probes-with-spring-boot) 增强了健康检测的功能，细化了 Liveness 和 Readiness 两个端点，便于 Spring Boot 应用程序和 Kubernetes 整合

让所有用户都可以直接查看各个组件的健康情况

```properties
management.endpoint.health.show-details=always
```

配置授权的角色(可选)

```pro
management.endpoint.health.roles
```

可以通过 org.springframework.boot.actuate.health.HealthIndicator 健康指标接口实现是否正确响应和程序整体的健康状态挂钩

- 参考
    - org.crayzer.err.design.productionready.health.ThreadPoolHealthIndicator
    - org.crayzer.err.design.productionready.health.UserServiceHealthIndicator



通过 org.springframework.boot.actuate.health.CompositeHealthContributor 接口来聚合多个 HealthContributor，实现一组线程池的监控

- 参考：org.crayzer.err.design.productionready.health.ThreadPoolsHealthContributor



对于查看和操作 MBean，除了使用 jconsole 之外，你可以使用 jolokia 把 JMX 转换为 HTTP 协议，引入依赖：

```xml
<dependency>
    <groupId>org.jolokia</groupId>
    <artifactId>jolokia-core</artifactId>
</dependency>
```

http://localhost:45679/admin/jolokia/exec/org.springframework.boot:type=Endpoint,name=Info/info



### 3. 对外暴露应用内部重要组件的状态：[info](info)

通过 Actuator 的 org.springframework.boot.actuate.info.InfoContributor 功能，对外暴露程序内部重要组件的状态数据

- 参考：org.crayzer.err.design.productionready.info.ThreadPoolInfoContributor



### 4. 指标 Metrics 是快速定位问题的“金钥匙”：[metrics](metrics)

###### 案例：外卖订单的下单和配送流程
![外卖订单的下单和配送流程.png](http://ww1.sinaimg.cn/large/002eBIeDgy1guemgo9y6dj61h610itg502.jpg)


OrderController 进行下单操作，下单操作前先判断参数，如果参数正确调用另一个服务查询商户状态，如果商户在营业的话继续下单，下单成功后发一条消息到 RabbitMQ 进行异步配送流程；然后另一个 DeliverOrderHandler 监听这条消息进行配送操作。(涉及同步调用和异步调用的业务流程)



分别为下单和配送这两个重要操作，建立一些指标进行监控。(对于配送操作，也是建立类似的 4 个指标。)

- 下单总数量指标，监控整个系统当前累计的下单量；
- 下单请求指标，对于每次收到下单请求，在处理之前 +1；
- 下单成功指标，每次下单成功完成 +1；
- 下单失败指标，下单操作处理出现异常 +1，并且把异常原因附加到指标上。

```
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-influx</artifactId>
</dependency>
```
使用 Micrometer 框架实现指标的收集，它也是 Spring Boot Actuator 选用的指标框架。它实现了各种指标的抽象，常用的有三种：
- gauge（红色），它反映的是指标当前的值，是多少就是多少，不能累计，比如本例中的下单总数量指标，
  又比如游戏的在线人数、JVM 当前线程数都可以认为是 gauge。
- counter（绿色），每次调用一次方法值增加 1，是可以累计的，比如本例中的下单请求指标。举一个例子，如果 5 秒内调用了 10 次方法，Micrometer 也是每隔 5 秒把指标发送给后端存储系统一次，那么它可以只发送一次值，其值为 10。
- timer（蓝色），类似 counter，只不过除了记录次数，还记录耗时，比如本例中的下单成功和下单失败两个
  指标。

附加一些 tags 标签，作为补充数据。比如，当操作执行失败的时候，我们就会附加一个 reason 标签到指标上。



> 模拟数据说明：当用户 ID<10 的时候，我们模拟用户数据无效的情况，当商户 ID 不为 2 的时候我们模拟商户不营业的情况。

- 第一种情况是，使用合法的用户 ID 和营业的商户 ID 运行一段时间：
  `wrk -t 1 -c 1 -d 3600s http://localhost:45678/order/createOrder\?userId\=20\&merchantId\=2`

- 第二种情况是，模拟无效用户 ID 运行一段时间：
  `wrk -t 1 -c 1 -d 3600s http://localhost:45678/order/createOrder\?userId\=2\&merchantId\=2`

- 第三种情况是，尝试一下因为商户不营业导致的下单失败：
  `wrk -t 1 -c 1 -d 3600s http://localhost:45678/order/createOrder\?userId\=20\&merchantId\=1`

- 第四种情况是，配送停止。我们通过 curl 调用接口，来设置配送停止开关：
  `第四种情况是，配送停止。我们通过 curl 调用接口，来设置配送停止开关：`