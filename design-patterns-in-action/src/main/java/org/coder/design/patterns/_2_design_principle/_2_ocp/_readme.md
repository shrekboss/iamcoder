> 开闭原则的英文全称是 Open Closed Principle，简写为 OCP。它的英文描述是：software entities (modules, classes, functions, etc.) 
> should be open for extension , but closed for modification。我们把它翻译成中文就是：软件实体（模块、类、方法等）应该“对扩展开放、
> 对修改关闭”。

# API 接口监控告警设计

- AlertRule 存储告警规则，可以自由设置。
- Notification 是告警通知类，支持邮件、短信、微信、手机等多种通知渠道。
- NotificationEmergencyLevel 表示通知的紧急程度，包括 SEVERE（严重）、URGENCY（紧急）、NORMAL（普通）、TRIVIAL（无关紧要），不同的紧急程度对应不同的发送渠道。

### 代码演变过程

原始版本：

```java
public class Alert {
    private AlertRule rule;
    private Notification notification;

    public Alert(AlertRule rule, Notification notification) {
        this.rule = rule;
        this.notification = notification;
    }

    public void check(String api, long requestCount, long errorCount, long durationOfSeconds) {
        long tps = requestCount / durationOfSeconds;
        if (tps > rule.getMatchedRule(api).getMaxTps()) {
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        }
        if (errorCount > rule.getMatchedRule(api).getMaxErrorCount()) {
            notification.notify(NotificationEmergencyLevel.SEVERE, "...");
        }
    }
}

```

添加一个功能: 当每秒钟接口超时请求个数，超过某个预先设置的最大阈值时，我们也要触发告警发送通知。具体的代码改动如下所示：

```java
public class Alert {
    // ...省略AlertRule/Notification属性和构造函数...

    // 改动一：添加参数 timeoutCount
    public void check(String api, long requestCount, long errorCount, long timeoutCount, long durationOfSeconds) {
        long tps = requestCount / durationOfSeconds;
        if (tps > rule.getMatchedRule(api).getMaxTps()) {
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        }
        if (errorCount > rule.getMatchedRule(api).getMaxErrorCount()) {
            notification.notify(NotificationEmergencyLevel.SEVERE, "...");
        }
        // 改动二：添加接口超时处理逻辑
        long timeoutTps = timeoutCount / durationOfSeconds;
        if (timeoutTps > rule.getMatchedRule(api).getMaxTimeoutTps()) {
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        }
    }
}
```

- 这样的代码修改实际上存在挺多问题的：
    - 对接口进行了修改，这就意味着调用这个接口的代码都要做相应的修改。
    - 修改了 check() 函数，相应的单元测试都需要修改。

- 重构一下之前的 Alert 代码，让它的扩展性更好一些。重构的内容主要包含两部分：
    - 将 check() 函数的多个入参封装成 ApiStatInfo 类；
    - 引入 handler 的概念，将 if 判断逻辑分散在各个 handler 中。

- 参考代码：
    - [AlertChain.java](AlertChain.java)
    - [AlertHandler.java](..%2F..%2Fcommon%2Falert%2FAlertHandler.java)
    - [ApiStatInfo.java](ApiStatInfo.java)
    - [TpsAlertHandler.java](..%2F..%2Fcommon%2Falert%2FTpsAlertHandler.java)
    - [ErrorAlertHandler.java](..%2F..%2Fcommon%2Falert%2FErrorAlertHandler.java)
    - [TimeoutAlertHandler.java](..%2F..%2Fcommon%2Falert%2FTimeoutAlertHandler.java)
    - [ApplicationContext.java](ApplicationContext.java)
    - [AlertRule.java](AlertRule.java)
    - [Notification.java](..%2F..%2Fcommon%2FNotification.java)
    - [NotificationEmergencyLevel.java](..%2F..%2Fcommon%2Fdefinition%2FNotificationEmergencyLevel.java)
    - [Demo.java](Demo.java)
