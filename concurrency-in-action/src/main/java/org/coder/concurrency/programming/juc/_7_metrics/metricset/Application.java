package org.coder.concurrency.programming.juc._7_metrics.metricset;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * 一切准备就绪，下面写一个简单的入口程序，启动云盘文件上传服务。
 * <p>
 * 运行效果与7.3.3节中的结果一致，但是整个Metric的注册就显得简洁了许多。
 * <p>
 * 2.SharedMetricRegistries
 * 在你的应用程序中可能不止需要一个Metric Registry进行Metric的注册，如果将一些互不相干的Metric注册到一个Metric Registry中，
 * 那么对应的reporter在输出的时候就需要进行一些复杂的过滤操作，最好的一种方式就是将它们分别放在不同的Metric Registry中，
 * 这种方式又会引入另一个新的问题，那就是多个Metric Registry的管理。Metrics官方已经提前想到了这一点，并且提供了一个全局的单实例，
 * 以及线程安全的类用于维护和管理多个MetricRegistry，即SharedMetricRegsitries，由于它的使用比较简单，本书将不再赘述，
 * 读者若有需要可以自行去官方网站查看文档，或者直接使用该API进行操作。
 * <p>
 * 7.6 本章总结
 * Metrics在dropwizard框架中大获成功之后，其迅速收获了大批的支持者和使用者，现阶段几乎所有新的框架平台都在内部嵌入Metrics，
 * 比如Apache Kafka就是Metrics的重度使用者，Apache Storm、Apache Spark、Spring Cloud、Apache Flink等中，
 * 都有对Metrics的使用或扩展，强烈建议大家在自己的应用程序开发中加入对Metrics的应用，它绝对不会让你失望的，
 * 正如Metrics所提倡的那样：Measure，Don't Guess（度量，别去猜测）！
 */
public class Application {
    //定义Metric Registry
    private final static MetricRegistry registry = new MetricRegistry();
    //定义Jmx Reporter
    private final static JmxReporter reporter = JmxReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    public static void main(String[] args) {
        // 启动Reporter
        reporter.start();
        BusinessService businessService = new BusinessService();
        //直接将BusinessService作为一个Metric加入注册表中，而不再逐个单独注册
        registry.registerAll(businessService);
        businessService.start();
    }

}