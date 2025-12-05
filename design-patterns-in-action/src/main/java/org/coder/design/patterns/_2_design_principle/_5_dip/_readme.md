> 依赖反转原则的英文翻译是 Dependency Inversion Principle，缩写为 DIP。中文翻译有时候也叫依赖倒置原则。为了追本溯源，我先给出这条原则最原
> 汁原味的英文描述：High-level modules shouldn’t depend on low-level modules. Both modules should depend on abstractions. 
> In addition, abstractions shouldn’t depend on details. Details depend on abstractions.我们将它翻译成中文，大概意思就是：高层
> 模块（high-level modules）不要依赖低层模块（low-level）。高层模块和低层模块应该通过抽象（abstractions）来互相依赖。除此之外，
> 抽象（abstractions）不要依赖具体实现细节（details），具体实现细节（details）依赖抽象（abstractions）。

## 控制反转（IOC）

> 控制反转实际上，控制反转是一个比较笼统的设计思想，并不是一种具体的实现方法，一般用来指导框架层面的设计。这里所说的“控制”指的是对程序执行流程
> 的控制，而“反转”指的是在没有使用框架之前，程序员自己控制整个程序的执行。在使用框架之后，整个程序的执行流程通过框架来控制。流程的控制权从程序
> 员“反转”给了框架。

先通过一个例子来看一下，感受一下什么是控制反转。

原始代码如下：所有的流程都由程序员来控制。

```java
public class UserServiceTest {
    public static boolean doTest() {
        // ... 
    }

    public static void main(String[] args) {//这部分逻辑可以放到框架中
        if (doTest()) {
            System.out.println("Test succeed.");
        } else {
            System.out.println("Test failed.");
        }
    }
}
```

抽象改造之后，参考代码如下：整个程序的执行流程可以通过框架来控制

- [JunitApplication.java](ioc%2FJunitApplication.java)
- [TestCase.java](ioc%2FTestCase.java)
- [UserServiceTest.java](ioc%2FUserServiceTest.java)

- “控制”指的是对程序执行流程的控制
- “反转”指的是流程的控制权从程序员“反转”到了框架。

## 依赖注入（DI）

> 依赖注入和控制反转恰恰相反，它是一种具体的编码技巧。我们不通过 new 的方式在类内部创建依赖类的对象，而是将依赖的类对象在外部创建好之后，通过
> 构造函数、函数参数等方式传递（或注入）给类来使用。

Notification 类负责消息推送，依赖 MessageSender 类实现推送商品促销、验证码等消息给用户。

非依赖注入的方式，具体的实现代码如下所示：

```java
// 非依赖注入实现方式
public class Notification {
    private MessageSender messageSender;

    public Notification() {
        //此处有点像hardcode
        this.messageSender = new MessageSender(); 
    }

    public void sendMessage(String cellphone, String message) {
        //...省略校验逻辑等...
        this.messageSender.send(cellphone, message);
    }
}

public class MessageSender {
    public void send(String cellphone, String message) {
        //....
    }
}

// 使用Notification
Notification notification = new Notification();
```

依赖注入的实现方式，参考代码如下：

- [MessageSender.java](di%2FMessageSender.java)
- [InboxSender.java](di%2FInboxSender.java)
- [MessageSender.java](di%2FMessageSender.java)
- [Notification.java](di%2FNotification.java)

## 依赖注入框架

> 我们通过依赖注入框架提供的扩展点，简单配置一下所有需要的类及其类与类之间依赖关系，就可以实现由框架来自动创建对象、管理对象的生命周期、依赖注入
> 等原本需要程序员来做的事情。