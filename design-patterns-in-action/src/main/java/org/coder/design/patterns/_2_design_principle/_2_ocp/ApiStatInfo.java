package org.coder.design.patterns._2_design_principle._2_ocp;

import lombok.Data;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class ApiStatInfo {

    private String api;
    private long requestCount;
    private long errorCount;
    private long durationOfSeconds;
    // 改动一：添加新字段
    private long timeoutCount;
}
