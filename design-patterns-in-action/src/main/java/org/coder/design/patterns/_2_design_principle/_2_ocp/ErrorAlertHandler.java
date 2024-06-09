package org.coder.design.patterns._2_design_principle._2_ocp;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ErrorAlertHandler extends AlertHandler {

    public ErrorAlertHandler(AlertRule rule, Notification notification) {
        super(rule, notification);
    }

    @Override
    public void check(ApiStatInfo apiStatInfo) {
        if (apiStatInfo.getErrorCount() > rule.getMatchedRule(apiStatInfo.getApi()).getMaxErrorCount()) {
            System.out.println("ErrorAlertHandler: 不通过");
            notification.notify(NotificationEmergencyLevel.SEVERE, "...");
        } else {
            System.out.println("ErrorAlertHandler: 通过");
        }
    }
}
