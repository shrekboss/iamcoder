> 迪米特法则的英文翻译是：Law of Demeter，缩写是 LOD。单从这个名字上来看，我们完全猜不出这个原则讲的是什么。不过，它还有另外一个更加达意的名
> 字，叫作最小知识原则，英文翻译为：The Least Knowledge Principle。关于这个设计原则，我们先来看一下它最原汁原味的英文定义：Each unit 
> should have only limited knowledge about other units: only units “closely” related to the current unit. Or: Each unit 
> should only talk to its friends; Don’t talk to strangers.我们把它直译成中文，就是下面这个样子：每个模块（unit）只应该了解那些与它
> 关系密切的模块（units: only units “closely” related to the current unit）的有限知识（knowledge）。或者说，每个模块只和自己的朋
> 友“说话”（talk），不和陌生人“说话”（talk）。

## 案例分析一：简化版的搜索引擎爬取网页的功能

- 代码中包含三个主要的类
    - NetworkTransporter 类负责底层网络通信，根据请求获取数据；
    - HtmlDownloader 类用来通过 URL 获取网页；
    - Document 表示网页文档，后续的网页内容抽取、分词、索引都是以此为处理对象。

具体的代码实现如下所示：

```java
public class NetworkTransporter {
    // 省略属性和其他方法...
    public Byte[] send(HtmlRequest htmlRequest) {
        //...
    }
}

public class HtmlDownloader {
    private NetworkTransporter transporter;//通过构造函数或IOC注入

    public Html downloadHtml(String url) {
        Byte[] rawHtml = transporter.send(new HtmlRequest(url));
        return new Html(rawHtml);
    }
}

public class Document {
    private Html html;
    private String url;

    public Document(String url) {
        this.url = url;
        HtmlDownloader downloader = new HtmlDownloader();
        this.html = downloader.downloadHtml(url);
    }
    //...
}
```

存在一些问题：

- NetworkTransporter 类。
    - 作为一个底层网络通信类，希望它的功能尽可能通用，而不只是服务于下载 HTML，所以，不应该直接依赖太具体的发送对象
      HtmlRequest。
    - 应该把 address 和 content 交给 NetworkTransporter，而非是直接把 HtmlRequest 交给 NetworkTransporter。
    - 从这一点上讲，NetworkTransporter 类的设计违背迪米特法则，依赖了不该有直接依赖关系的 HtmlRequest 类。
- HtmlDownloader 类
- Document 类
    - 构造函数中的 downloader.downloadHtml() 逻辑复杂，耗时长，不应该放到构造函数中，会影响代码的可测试性。
    - 第二，HtmlDownloader 对象在构造函数中通过 new 来创建，违反了基于接口而非实现编程的设计思想，也会影响到代码的可测试性。
    - 第三，从业务含义上来讲，Document 网页文档没必要依赖 HtmlDownloader 类，违背了迪米特法则。

重构后参考代码：

- [NetworkTransporter.java](NetworkTransporter.java)
- [HtmlDownloader.java](HtmlDownloader.java)
- [Document.java](Document.java)
- [DocumentFactory.java](DocumentFactory.java)

## 案例分析二：

直接看代码：

```java
public class Serialization {
    public String serialize(Object object) {
        String serializedResult = ...;
        //...
        return serializedResult;
    }

    public Object deserialize(String str) {
        Object deserializedResult = ...;
        //...
        return deserializedResult;
    }
}
```

第二次重构后，代码如下：

```java
public class Serializer {
  public String serialize(Object object) {
    String serializedResult = ...;
    // ...
    return serializedResult;
  }
}

public class Deserializer {
  public Object deserialize(String str) {
    Object deserializedResult = ...;
    // ...
    return deserializedResult;
  }
}
```

尽管拆分之后的代码更能满足迪米特法则，但却违背了高内聚的设计思想。高内聚要求相近的功能要放到同一个类中，这样可以方便功能修改的时候，修改的地方不至于过于分散。

第二次重构后，参考代码：

- [Deserializable.java](_2_case%2FDeserializable.java)
- [Serialization.java](_2_case%2FSerialization.java)
- [Serializable.java](_2_case%2FSerializable.java)
- [DemoClass_1.java](_2_case%2FDemoClass_1.java)
- [DemoClass_2.java](_2_case%2FDemoClass_2.java)

对于刚刚这个 Serialization 类来说，只包含两个操作，确实没有太大必要拆分成两个接口。但是，如果我们对 Serialization
类添加更多的功能，实现更多更好用的序列化、反序列化函数，我们来重新考虑一下这个问题。修改之后的具体的代码如下：

```java
public class Serializer { // 参看JSON的接口定义
  public String serialize(Object object) { //... }
  public String serializeMap(Map map) { //... }
  public String serializeList(List list) { //... }
  
  public Object deserialize(String objectString) { //... }
  public Map deserializeMap(String mapString) { //... }
  public List deserializeList(String listString) { //... }
}
```

在这种场景下，第二种设计思路要更好些。