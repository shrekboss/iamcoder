package org.coder.design.patterns._2_design_principle._2_ocp;

import lombok.Getter;
import org.coder.design.patterns._4_design_patterns._2_structural._2_Bridge.EmailMsgSender;
import org.coder.design.patterns.simulate.Notification;
import org.coder.design.patterns.simulate.alert.ErrorAlertHandler;
import org.coder.design.patterns.simulate.alert.TimeoutAlertHandler;
import org.coder.design.patterns.simulate.alert.TpsAlertHandler;

import java.util.Collections;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ApplicationContext {

    private AlertRule alertRule;
    private Notification notification;
    @Getter
    private Alert alert;

    public void initializeBeans () {
        alertRule = new AlertRule(/*.省略参数.*/);
        //省略一些初始化代码
        EmailMsgSender msgSender = new EmailMsgSender(Collections.singletonList("crayzer.chen@gmail.com"));
        notification = new Notification(msgSender) {
            @Override
            public void notify(String message) {
                // ...
            }
        }; //省略一些初始化代码
        alert = new Alert();
        alert.addAlertHandler(new TpsAlertHandler(alertRule, notification));
        alert.addAlertHandler(new ErrorAlertHandler(alertRule, notification));
        // 改动三：注册handler
        alert.addAlertHandler(new TimeoutAlertHandler(alertRule, notification));
    }

    @Getter
    private static final ApplicationContext instance = new ApplicationContext();

    private ApplicationContext() {
        initializeBeans();
    }

}
