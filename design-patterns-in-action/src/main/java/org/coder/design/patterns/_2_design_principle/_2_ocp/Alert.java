package org.coder.design.patterns._2_design_principle._2_ocp;

import org.coder.design.patterns.common.alert.AlertHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Alert {

    private List<AlertHandler> alertHandlerList = new ArrayList<>();

    public void addAlertHandler(AlertHandler handler) {
        this.alertHandlerList.add(handler);
    }

    public void check(ApiStatInfo apiStatInfo) {
        for (AlertHandler handler : alertHandlerList) {
            handler.check(apiStatInfo);
        }
    }
}
