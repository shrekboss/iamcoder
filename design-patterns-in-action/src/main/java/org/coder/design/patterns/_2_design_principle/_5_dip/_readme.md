## 控制反转（IOC）

> 控制反转并不是一种具体的实现技巧，而是一个比较笼统的设计思想，一般用来指导框架层面的设计。

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

> 它是一种具体的编码技巧

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