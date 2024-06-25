package org.coder.design.patterns.simulate.vo;

import lombok.Data;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class RequestInfo {

    private String apiName;
    private double responseTime;
    private long timestamp;

    public RequestInfo(String apiName, long responseTime, long timestamp) {

        this.apiName = apiName;
        this.responseTime = responseTime;
        this.timestamp = timestamp;
    }
}
