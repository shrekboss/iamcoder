package org.coder.design.patterns._2_design_principle._2_ocp;

import lombok.Data;

/**
 * 存储告警规则，可以自由设置
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class AlertRule {

    private long maxTps = 10;
    private long maxErrorCount = 10;

    public AlertRule getMatchedRule(String api) {
        // simulate
        return new AlertRule();
    }

}
