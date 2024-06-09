package org.coder.design.patterns._2_design_principle._5_dip.di;

/**
 * 依赖注入的实现方式(DI)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Notification {
    private MessageSender messageSender;

    // 通过构造函数将 messageSender 传递进来，还是需要我们程序员自己来实现
    public Notification(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendMessage(String cellphone, String message) {
        //...省略校验逻辑等...
        this.messageSender.send(cellphone, message);
    }
}

// 使用 Notification
// MessageSender messageSender = new SmsSender();
// Notification notification = new Notification(messageSender);