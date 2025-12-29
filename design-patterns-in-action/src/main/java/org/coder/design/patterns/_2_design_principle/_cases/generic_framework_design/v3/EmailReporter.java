package org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v3;

import com.google.common.annotations.VisibleForTesting;
import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v1.MetricsStorage;
import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v1.RedisMetricsStorage;
import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v2.Aggregator;
import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v2.EmailViewer;
import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v2.StatViewer;
import org.coder.design.patterns.simulate.vo.RequestInfo;

import java.util.*;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class EmailReporter extends ScheduledReporter {

    private static final Long DAY_HOURS_IN_SECONDS = 86400L;

    private MetricsStorage metricsStorage;
    private Aggregator aggregator;
    private StatViewer viewer;

    // 兼顾代码的易用性，新增一个封装了默认依赖的构造函数
    public EmailReporter(List<String> emailToAddresses) {
        this(new RedisMetricsStorage(), new Aggregator(), new EmailViewer(emailToAddresses));
    }

    // 兼顾灵活性和代码的可测试性，这个构造函数继续保留
    public EmailReporter(MetricsStorage metricsStorage, Aggregator aggregator, StatViewer viewer) {
        super(metricsStorage, aggregator, viewer);
    }

    public void startDailyReport() {

        Date firstTime = trimTimeFieldsToZeroOfNextDay(new Date());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long durationInMillis = DAY_HOURS_IN_SECONDS * 1000;
                long endTimeInMillis = System.currentTimeMillis();
                long startTimeInMillis = endTimeInMillis - durationInMillis;
                Map<String, List<RequestInfo>> requestInfos = metricsStorage.getRequestInfos(startTimeInMillis, endTimeInMillis);
                Map stats = aggregator.aggregate(requestInfos, durationInMillis);
                viewer.output(stats, startTimeInMillis, endTimeInMillis);
            }
        }, firstTime, DAY_HOURS_IN_SECONDS * 1000);
    }

    /**
     * 设置成protected而非private是为了方便写单元测试
     */
    @VisibleForTesting
    protected Date trimTimeFieldsToZeroOfNextDay(Date date) {
        // 这里可以获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
