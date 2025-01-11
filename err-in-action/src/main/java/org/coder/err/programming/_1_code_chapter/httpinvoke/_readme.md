## HTTP调用：你考虑到超时、重试、并发了吗？

### 1. 配置连接超时和读取超时参数的学问

参考代码：[clientreadtimeout](clientreadtimeout)

几乎所有的网络框架都会提供这么两个超时参数：

- 连接超时参数 ConnectTimeout，让用户配置建连阶段的最长等待时间
- 读取超时参数 ReadTimeout，用来控制从 Socket 上读取数据的最长等待时间

#### 连接超时参数和连接超时的误区

- 连接超时配置得特别长，比如 60 秒。一般来说，TCP
  三次握手建立连接需要的时间非常短，通常在毫秒级最多到秒级，不可能需要十几秒甚至几十秒。如果很久都无法建连，很可能是网络或防火墙配置的问题。这种情况下，如果几秒连接不上，那么可能永远也连接不上。因此，设置特别长的连接超时意义不大，将其配置得短一些（比如
  1~5 秒）即可。如果是纯内网调用的话，这个参数可以设置得更短，在下游服务离线无法连接的时候，可以快速失败。
- 排查连接超时问题，却没理清连的是哪里。通常情况下，我们的服务会有多个节点，如果别的客户端通过客户端负载均衡技术来连接服务端，那么客户端和服务端会直接建立连接，此时出现连接超时大概率是服务端的问题；而如果服务端通过类似
  Nginx 的反向代理来负载均衡，客户端连接的其实是 Nginx，而不是服务端，此时出现连接超时应该排查 Nginx。

#### 读取超时参数和读取超时的误区

- 第一个误区：认为出现了读取超时，服务端的执行就会中断。

    - 只要服务端收到了请求，网络层面的超时和断开便不会影响服务端的执行。因此，出现读取超时不能随意假设服务端的处理情况，需要根据业务状态考虑如何进行后续处理。

- 第二个误区：认为读取超时只是 Socket 网络层面的概念，是数据传输的最长耗时，故将其配置得非常短，比如 100 毫秒。

    - 发生了读取超时，网络层面无法区分是服务端没有把数据返回给客户端，还是数据在网络上耗时较久或丢包。
    - 读取超时指的是，向 Socket 写入数据后，我们等到 Socket 返回数据的超时时间，其中包含的时间或者说绝大部分的时间，是服务端处理业务逻辑的时间。

- 第三个误区：认为超时时间越长任务接口成功率就越高，将读取超时参数配置得太长。
    - 进行 HTTP 请求一般是需要获得结果的，属于同步调用。如果超时时间很长，在等待服务端返回数据的同时，客户端线程（通常是
      Tomcat 线程）也在等待，当下游服务出现大量超时的时候，程序可能也会受到拖累创建大量线程，最终崩溃。

    - 对定时任务或异步任务来说，读取超时配置可以适当的长些问题不大。但面向用户响应的请求或是微服务短平快的同步接口调用，并发量一般较大，我们应该设置一个较短的读取超时时间，以防止被下游服务拖慢，通常不会设置超过
      30 秒的读取超时。

#### “写入超时”的概念

- 写入操作只是将数据写入 TCP 的发送缓冲区，已经发送到网络的数据依然需要暂存在发送缓冲区中，只有收到对方的 ack
  后，操作系统内核才从缓冲区中清除这一部分数据，为后续发送数据腾出空间。
- 如果接收端从 socket
  读取数据的速度太慢，可能会导致发送端发送缓冲区满，导致写入操作阻塞，产生写入超时。但是，因为有滑动窗口的控制，通常不太容易发生发送缓冲区满导致写入超时的情况。相反，读取超时包含了服务端处理数据执行业务逻辑的时间，所以读取超时是比较容易发生的。

### 2. Feign和Ribbon配合使用，你知道怎么配置超时吗？

参考代码：[feignandribbontimeout](feignandribbontimeout)

- 结论一：默认情况下 Feign 的读取超时是 1 秒，如此短的读取超时算是坑点一。

    - org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration

- 结论二：如果要配置 Feign 的读取超时，就必须同时配置连接超时，才能生效。

    - org.springframework.cloud.openfeign.FeignClientFactoryBean#configureUsingProperties

      ```properties
      feign.client.config.default.readTimeout=3000
      feign.client.config.default.connectTimeout=3000
      ```

- 结论三，单独的超时可以覆盖全局超时，这符合预期，不算坑。

  ```properties
  # 修改 Feign 客户端默认的两个全局超时时间，只修改读取超时，这样的配置是无法生效的！
  feign.client.config.default.readTimeout=3000
  feign.client.config.default.connectTimeout=3000
  
  # 针对单独的 Feign Client 设置超时时间，可以把 default 替换为 Client 的 name
  #feign.client.config.clientsdk.readTimeout=2000
  #feign.client.config.clientsdk.connectTimeout=2000
  ```

- 结论四，除了可以配置 Feign，也可以配置 Ribbon 组件的参数来修改两个超时时间。有点坑的是，参数首字母要大写，和 Feign 的配置不同。

  ```properties
  ribbon.ReadTimeout=4000
  ribbon.ConnectTimeout=4000
  ```

- 结论五，同时配置 Feign 和 Ribbon 的超时，以 Feign 为准

### 3. 你是否知道Ribbon会自动重试请求呢

参考代码：[ribbonretry](ribbonretry)

MaxAutoRetriesNextServer 参数默认为 1，也就是 Get 请求在某个服务端节点出现问题（比如读取超时）时，Ribbon 会自动重试一次

```java
// 客户端自作主张进行了一次重试，导致短信重复发送
public static final int DEFAULT_MAX_AUTO_RETRIES_NEXT_SERVER = 1;
```

解决办法有两个：

- 把发短信接口从 Get 改为 Post。其实，这里还有一个 API 设计问题，有状态的 API 接口不应该定义为 Get。根据 HTTP 协议的规范，Get
  请求用于数据查询，而 Post 才是把数据提交到服务端用于修改或新增。选择 Get 还是 Post 的依据，应该是 API
  的行为，而不是参数大小。这里的一个误区是，Get 请求的参数包含在 Url QueryString 中，会受浏览器长度限制，所以一些同学会选择使用
  JSON 以 Post 提交大参数，使用 Get 提交小参数。
- 将 MaxAutoRetriesNextServer 参数配置为 0，禁用服务调用失败后在下一个服务端节点的自动重试。在配置文件中添加一行即可：ribbon.MaxAutoRetriesNextServer=0

### 4. 并发限制了爬虫的抓取能力

参考代码：[routelimit](routelimit)

org.apache.http.impl.conn.PoolingHttpClientConnectionManager 有两个重要参数：

- defaultMaxPerRoute=2，也就是同一个主机 / 域名的最大并发请求数为 2。我们的爬虫需要 10 个并发，显然是默认值太小限制了爬虫的效率。
- maxTotal=20，也就是所有主机整体最大并发为 20，这也是 HttpClient 整体的并发度。目前，我们请求数是 10 最大并发是 10，20
  不会成为瓶颈。举一个例子，使用同一个 HttpClient 访问 10 个域名，defaultMaxPerRoute 设置为 10，为确保每一个域名都能达到 10
  并发，需要把 maxTotal 设置为 100。

HttpClient 是 Java 非常常用的 HTTP 客户端，这个问题经常出现。你可能会问，为什么默认值限制得这么小。其实，这不能完全怪
HttpClient，很多早期的浏览器也限制了同一个域名两个并发请求。对于同一个域名并发连接的限制，其实是 HTTP 1.1 协议要求的，这里有这么一段话：

```txt
Clients that use persistent connections SHOULD limit the number of simultaneous connections that they maintain to a given server. A single-user client SHOULD NOT maintain more than 2 connections with any server or proxy. A proxy SHOULD use up to 2*N connections to another server or proxy, where N is the number of simultaneously active users. These guidelines are intended to improve HTTP response times and avoid congestion.
```

HTTP 1.1 协议是 20 年前制定的，现在 HTTP 服务器的能力强很多了，所以有些新的浏览器没有完全遵从 2 并发这个限制，放开并发数到了
8 甚至更大。如果需要通过 HTTP 客户端发起大量并发请求，不管使用什么客户端，请务必确认客户端的实现默认的并发度是否满足需求。

### 5. Feign方法级别设置超时的例子：feignpermethodtimeout

参考代码：[feignpermethodtimeout](feignpermethodtimeout)

### 6. Nginx 的重试功能

> proxy_next_upstream error timeout http_500;

- proxy_next_upstream，用于指定在什么情况下 Nginx 会将请求转移到其他服务器上。其默认值是
  proxy_next_upstream error timeout，即发生网络错误以及超时，才会重试其他服务器。也就是说，默认情况下，服务返回 500
  状态码是不会重试的。

  ```xml
  proxy_next_upstream error timeout http_500;
  ```

- 需要注意的是，proxy_next_upstream 配置中有一个选项 non_idempotent，一定要小心开启。通
  常情况下，如果请求使用非等幂方法（POST、PATCH），请求失败后不会再到其他服务器进行重试。但是，加上 non_idempotent
  这个选项后，即使是非幂等请求类型（例如 POST 请求），发生错误后也会重试。