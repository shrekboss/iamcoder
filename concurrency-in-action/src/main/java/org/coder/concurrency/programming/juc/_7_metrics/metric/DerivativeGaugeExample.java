package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.DerivativeGauge;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * 5.Derivative Gauge 详解
 * Derivative Gauge允许从某个Gauge value中获取特定的属性和值，比如，我们将Cache的Stats作为一个Metric，Stats中包含了非常多的属性，但是我们只需要其中的一两个，
 * 比如Cache没命中率、Cache加载异常统计等，此时我们就可以借助Derivative Gauge来派生这样的功能。
 * <p>
 * 下面的代码中，我们使用了Google Guava的Cache功能。关于Google Guava 请读者查阅官方文档自行学习。
 * <p>
 * 首先，我们使用了Simple Gauge创建了一个获取cache stats value的Metric，然后使用该Gauge派生出了两个不同的Gauge Metric，运行下面的程序我们会看到如下的输出。
 * <p>
 * 通过输出我们不难发现，record stats gauge会输出所有的cache stats信息，而其他两个则只会输出派生出来的value。
 */
public class DerivativeGaugeExample {
    //定义Cache
    private final static LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterAccess(5, TimeUnit.SECONDS)
            //开启Cache Stats统计功能
            .recordStats()
            .build(new CacheLoader<String, String>() {

                @Override
                public String load(String key) throws Exception {
                    return key.toUpperCase();
                }

            });
    //定义一个metric registry
    private final static MetricRegistry registry = new MetricRegistry();
    //定义Console Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    public static void main(String[] args) throws InterruptedException {
        //启动Reporter，每隔10秒的时间输出一次数据
        reporter.start(10, TimeUnit.SECONDS);
        //注册一个Gauge Metric，value是cache的stats
        Gauge<CacheStats> cacheGauge = registry.gauge("cache-stats", () -> cache::stats);

        //通过cacheGauge派生missCount，并且注册到Registry
        registry.register("missCount", new DerivativeGauge<CacheStats, Long>(cacheGauge) {

            @Override
            protected Long transform(CacheStats stats) {
                return stats.missCount();
            }
        });
        //通过cacheGauge派生loadExceptionCount，并且注册到Registry
        registry.register("loadExceptionCount", new DerivativeGauge<CacheStats, Long>(cacheGauge) {

            @Override
            protected Long transform(CacheStats stats) {
                return stats.loadExceptionCount();
            }

        });

        while (true) {
            business();
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static void business() {
        cache.getUnchecked("alex");
    }

}