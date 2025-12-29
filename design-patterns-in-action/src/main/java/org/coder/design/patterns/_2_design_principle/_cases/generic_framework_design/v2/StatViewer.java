package org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v2;

import org.coder.design.patterns._2_design_principle._cases.generic_framework_design._simulate.RequestStat;

import java.util.Map;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface StatViewer {

    void output(Map<String, RequestStat> requestStats, long startTimeInMillis, long endTimeInMills);
}
