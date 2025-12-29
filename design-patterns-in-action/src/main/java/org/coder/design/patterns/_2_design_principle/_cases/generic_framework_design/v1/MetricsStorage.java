package org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v1;

import org.coder.design.patterns.simulate.vo.RequestInfo;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface MetricsStorage {

    void saveRequestInfo(RequestInfo requestInfo);

    List<RequestInfo> getRequestInfos(String apiName, long startTimeInMillis, long endTimeInMillis);

    Map<String, List<RequestInfo>> getRequestInfos(long startTimeInMillis, long endTimeInMillis);
}
