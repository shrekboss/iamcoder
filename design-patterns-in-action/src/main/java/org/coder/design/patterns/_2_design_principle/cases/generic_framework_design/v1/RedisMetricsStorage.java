package org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v1;

import org.coder.design.patterns._2_design_principle.cases.generic_framework_design.simulate.RequestInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
public class RedisMetricsStorage implements MetricsStorage {

    @Override
    public void saveRequestInfo(RequestInfo requestInfo) {

    }

    @Override
    public List<RequestInfo> getRequestInfos(String apiName, long startTimeInMillis, long endTimeInMillis) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, List<RequestInfo>> getRequestInfos(long startTimeInMillis, long endTimeInMillis) {
        return Collections.emptyMap();
    }
}
