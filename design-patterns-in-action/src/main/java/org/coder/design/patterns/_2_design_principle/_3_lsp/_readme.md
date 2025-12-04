## 如何理解“里式替换原则”？
> 里式替换原则的英文翻译是：Liskov Substitution Principle，缩写为 LSP。这个原则最早是在 1986 年由 Barbara Liskov 提出，他是这么描述
> 这条原则的：If S is a subtype of T, then objects of type T may be replaced with objects of type S, without breaking the 
> program。在 1996 年，Robert Martin 在他的 SOLID 原则中，重新描述了这个原则，英文原话是这样的：Functions that use pointers of 
> references to base classes must be able to use objects of derived classes without knowing it。我们综合两者的描述，将这条原
> 则用中文描述出来，是这样的：子类对象（object of subtype/derived class）能够替换程序（program）中父类对象（object of base/parent 
> class）出现的任何地方，并且保证原来程序的逻辑行为（behavior）不变及正确性不被破坏。

实例感受一下 LSP。父类 Transporter 使用 org.apache.http 库中的 HttpClient 类来传输网络数据。子类 SecurityTransporter 继承父类
Transporter，增加了额外的功能，支持传输 appId 和 appToken 安全认证信息。

```java
import sun.net.www.http.HttpClient;

public class Transporter {
    private HttpClient httpClient;

    public Transporter(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Response sendRequest(Request request) {
        // ...use httpClient to send request
    }
}

public class SecurityTransporter extends Transporter {
    private String appId;
    private String appToken;

    public SecurityTransporter(HttpClient httpClient, String appId, String appToken) {
        super(httpClient);
        this.appId = appId;
        this.appToken = appToken;
    }

    @Override
    public Response sendRequest(Request request) {
        if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appToken)) {
            request.addPayload("app-id", appId);
            request.addPayload("app-token", appToken);
        }
        return super.sendRequest(request);
    }
}

public class Demo {
    public void demoFunction(Transporter transporter) {
        Reuqest request = new Request();
        //...省略设置request中数据值的代码...
        Response response = transporter.sendRequest(request);
        //...省略其他逻辑...
    }

    public static void main(String[] args) {
        // 里式替换原则
        Demo demo = new Demo();
        demo.demofunction(new SecurityTransporter(/*省略参数*/));
    }
}

```

改造 SecurityTransporter 类中的 sendRequest()
> 改造后，如果 appId 或者 appToken 没有设置，则直接抛出 NoAuthorizationRuntimeException 未授权异常。

```java
// 改造前：
public class SecurityTransporter extends Transporter {
    //...省略其他代码..
    @Override
    public Response sendRequest(Request request) {
        if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(appToken)) {
            request.addPayload("app-id", appId);
            request.addPayload("app-token", appToken);
        }
        return super.sendRequest(request);
    }
}

// 改造后：
public class SecurityTransporter extends Transporter {
    //...省略其他代码..
    @Override
    public Response sendRequest(Request request) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(appToken)) {
            throw new NoAuthorizationRuntimeException(/* ... */);
        }
        request.addPayload("app-id", appId);
        request.addPayload("app-token", appToken);
        return super.sendRequest(request);
    }
}
```

改造之后的代码仍然可以通过 Java 的多态语法，动态地用子类 SecurityTransporter 来替换父类
Transporter，也并不会导致程序编译或者运行报错。但是，从设计思路上来讲，SecurityTransporter 的设计是不符合里式替换原则的。

稍微总结一下。虽然从定义描述和代码实现上来看，多态和里式替换有点类似，但它们关注的角度是不一样的。

- 多态是面向对象编程的一大特性，也是面向对象编程语言的一种语法。它是一种代码实现的思路。
- 而里式替换是一种设计原则，是用来指导继承关系中子类该如何设计的，子类的设计要保证在替换父类的时候，不改变原有程序的逻辑以及不破坏原有程序的正确性。

哪些代码明显违背了 LSP？
- 子类违背父类声明要实现的功能
- 子类违背父类对输入、输出、异常的约定
- 子类违背父类注释中所罗列的任何特殊说明