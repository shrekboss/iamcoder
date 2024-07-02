## Event Driven 设计模式

EDA 参考代码：

- [Message.java](Message.java)
    - [Event.java](Event.java)
- [Channel.java](Channel.java)
- [DynamicRouter.java](DynamicRouter.java)
- [EventDispatcher.java](EventDispatcher.java)
- [MessageMatcherException.java](MessageMatcherException.java)
- [EventDispatcherExample.java](EventDispatcherExample.java)

异步EDA 参考代码：

- [AsyncChannel.java](AsyncChannel.java)
- [AsyncEventDispatcher.java](AsyncEventDispatcher.java)
- [AsyncEventDispatcherExample.java](AsyncEventDispatcherExample.java)

### Event-Driven 实战 - Chat

参考代码：

- [User.java](chat%2FUser.java)
- [UserOnlineEvent.java](chat%2FUserOnlineEvent.java)
    - [UserOfflineEvent.java](chat%2FUserOfflineEvent.java)
    - [UserChatEvent.java](chat%2FUserChatEvent.java)
- [UserOnlineEventChannel.java](chat%2FUserOnlineEventChannel.java)
- [UserOfflineEventChannel.java](chat%2FUserOfflineEventChannel.java)
- [UserChatEventChannel.java](chat%2FUserChatEventChannel.java)
- [UserChatThread.java](chat%2FUserChatThread.java)
- [UserChatApplication.java](chat%2FUserChatApplication.java)

Message(Event) 无论是同步还是在异步的 EDA 中，都没有使用任何同步的方式对其进行控制，根本原因是 Event 被设计成了不可变对象，因为
Event 在经过每一个 Channel(Handler) 的时候都会创建一个新的 Event，多个线程之间不会出现资源竞争，因此不需要同步的保护