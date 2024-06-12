package org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v1;

import org.apache.commons.lang3.StringUtils;
import org.coder.design.patterns._2_design_principle.cases.generic_framework_design._simulate.RequestInfo;

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
public class MetricsCollector {

    // 基于接口而非实现编程
    private MetricsStorage metricsStorage;

    // 依赖注入
    public MetricsCollector(MetricsStorage metricsStorage) {
        this.metricsStorage = metricsStorage;
    }

    // 用一个函数代替了最小原型中的两个函数
    public void recordRequest(RequestInfo requestInfo) {
        if (requestInfo == null || StringUtils.isBlank(requestInfo.getApiName())) {
            return;
        }
        metricsStorage.saveRequestInfo(requestInfo);
    }
}