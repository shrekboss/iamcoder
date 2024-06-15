package org.coder.design.patterns.common.alert;

import org.coder.design.patterns._2_design_principle._2_ocp.AlertRule;
import org.coder.design.patterns._2_design_principle._2_ocp.ApiStatInfo;
import org.coder.design.patterns.common.Notification;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TimeoutAlertHandler extends AlertHandler {

    public TimeoutAlertHandler(AlertRule rule, Notification notification) {
        super(rule, notification);
    }

    @Override
    public void check(ApiStatInfo apiStatInfo) {
        //
    }
}
