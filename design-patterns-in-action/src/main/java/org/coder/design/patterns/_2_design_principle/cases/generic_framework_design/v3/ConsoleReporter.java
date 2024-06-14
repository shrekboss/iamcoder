package org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v3;

import org.coder.design.patterns._2_design_principle.cases.generic_framework_design._simulate.RequestStat;
import org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v1.MetricsStorage;
import org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v1.RedisMetricsStorage;
import org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v2.Aggregator;
import org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v2.ConsoleViewer;
import org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v2.StatViewer;
import org.coder.design.patterns.common.vo.RequestInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
public class ConsoleReporter extends ScheduledReporter {

    private final ScheduledExecutorService executor;

    // 兼顾代码的易用性，新增一个封装了默认依赖的构造函数
    public ConsoleReporter() {
        this(new RedisMetricsStorage(), new Aggregator(), new ConsoleViewer());
    }

    // 兼顾灵活性和代码的可测试性，这个构造函数继续保留
    public ConsoleReporter(MetricsStorage metricsStorage, Aggregator aggregator, StatViewer viewer) {
        super(metricsStorage, aggregator, viewer);
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void startRepeatedReport(long periodInSeconds, long durationInSeconds) {
        executor.scheduleAtFixedRate(() -> {
            long durationInMillis = durationInSeconds * 1000;
            long endTimeInMillis = System.currentTimeMillis();
            long startTimeInMillis = endTimeInMillis - durationInMillis;
            Map<String, List<RequestInfo>> requestInfos = metricsStorage.getRequestInfos(startTimeInMillis, endTimeInMillis);
            Map<String, RequestStat> requestStats = aggregator.aggregate(requestInfos, durationInMillis);
            viewer.output(requestStats, startTimeInMillis, endTimeInMillis);
        }, 0L, periodInSeconds, TimeUnit.SECONDS);
    }
}
