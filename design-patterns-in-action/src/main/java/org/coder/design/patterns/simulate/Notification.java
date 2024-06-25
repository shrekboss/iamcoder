package org.coder.design.patterns.simulate;

import lombok.Data;
import org.coder.design.patterns._2_design_principle._5_dip.di.MessageSender;
import org.coder.design.patterns._4_design_patterns._2_structural._2_Bridge.MsgSender;

/**
 * 告警通知类，支持邮件、短信、微信、手机等多种通知渠道
 * 依赖注入的实现方式(DI)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public abstract class Notification {

    protected MsgSender msgSender;
    private MessageSender messageSender;

    public Notification(MsgSender msgSender) {
        this.msgSender = msgSender;
    }

    //    public void notify(NotificationEmergencyLevel level, String msg) {
//        if (level.equals(NotificationEmergencyLevel.SEVERE)) {
//            //...自动语音电话
//        } else if (level.equals(NotificationEmergencyLevel.URGENCY)) {
//            //...发微信
//        } else if (level.equals(NotificationEmergencyLevel.NORMAL)) {
//            //...发邮件
//        } else if (level.equals(NotificationEmergencyLevel.TRIVIAL)) {
//            //...发邮件
//        }
//    }

    public abstract void notify(String message);

    // 通过构造函数将 messageSender 传递进来，还是需要我们程序员自己来实现
    public Notification(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendMessage(String cellphone, String message) {
        //...省略校验逻辑等...
        this.messageSender.send(cellphone, message);
    }
}
