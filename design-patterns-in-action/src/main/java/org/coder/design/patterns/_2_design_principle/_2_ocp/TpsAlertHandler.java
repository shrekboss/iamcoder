package org.coder.design.patterns._2_design_principle._2_ocp;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TpsAlertHandler extends AlertHandler {

    public TpsAlertHandler(AlertRule rule, Notification notification) {
        super(rule, notification);
    }

    @Override
    public void check(ApiStatInfo apiStatInfo) {
        long tps = apiStatInfo.getRequestCount() / apiStatInfo.getDurationOfSeconds();
        if (tps > rule.getMatchedRule(apiStatInfo.getApi()).getMaxTps()) {
            System.out.println("TpsAlertHandler: 不通过");
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        } else {
            System.out.println("TpsAlertHandler: 通过");
        }
    }
}