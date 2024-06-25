package org.coder.design.patterns.simulate.alert;

import org.coder.design.patterns._2_design_principle._2_ocp.AlertRule;
import org.coder.design.patterns._2_design_principle._2_ocp.ApiStatInfo;
import org.coder.design.patterns.simulate.Notification;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public abstract class AlertHandler {

    protected AlertRule rule;
    protected Notification notification;

    public AlertHandler(AlertRule rule, Notification notification) {
        this.rule = rule;
        this.notification = notification;
    }

    public abstract void check(ApiStatInfo apiStatInfo);

}
