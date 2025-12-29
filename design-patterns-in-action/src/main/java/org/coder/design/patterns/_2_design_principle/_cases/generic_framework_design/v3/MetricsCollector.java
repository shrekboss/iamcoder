package org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v3;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v1.MetricsStorage;
import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v1.RedisMetricsStorage;
import org.coder.design.patterns.simulate.vo.RequestInfo;

import java.util.concurrent.Executors;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class MetricsCollector {

    public static final int DEFAULT_STORAGE_THREAD_POOL_SIZE = 20;

    private MetricsStorage metricsStorage;
    private EventBus eventBus;


    // 兼顾代码的易用性，新增一个封装了默认依赖的构造函数
    public MetricsCollector() {
        this(new RedisMetricsStorage(), DEFAULT_STORAGE_THREAD_POOL_SIZE);
    }

    // 兼顾灵活性和代码的可测试性，这个构造函数继续保留
    public MetricsCollector(MetricsStorage metricsStorage) {
        this(metricsStorage, DEFAULT_STORAGE_THREAD_POOL_SIZE);
    }

    public MetricsCollector(MetricsStorage metricsStorage, int threadNumToSaveData) {
        this.metricsStorage = metricsStorage;
        this.eventBus = new AsyncEventBus(Executors.newFixedThreadPool(threadNumToSaveData));
        this.eventBus.register(new EventListener());
    }

    // 用一个函数代替了最小原型中的两个函数
    public void recordRequest(RequestInfo requestInfo) {
        if (requestInfo == null || StringUtils.isBlank(requestInfo.getApiName())) {
            return;
        }
        eventBus.post(requestInfo);
    }

    private class EventListener {
        @Subscribe
        public void saveRequestInfo(RequestInfo requestInfo) {
            metricsStorage.saveRequestInfo(requestInfo);
        }
    }
    // 省略其他代码...
}
