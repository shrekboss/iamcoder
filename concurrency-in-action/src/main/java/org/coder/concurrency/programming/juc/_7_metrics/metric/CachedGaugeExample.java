package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

/**
 * 4.Cached Gauge 详解
 * 有时，我们想要获取的Gauge value对实时性的要求并没有那么高，
 * 比如我们想要从数据库中获取用户的状态，或者计算某个队列的size，
 * 这样我们就没有必要每次都计算它的真实Value，而是将计算结果暂时缓存一段时间，
 * 等设置的时间过期之后在重新获取。我们来看一下示例程序的代码。
 * <p>
 * 在下面的代码中，我们使用了Cached Gauge，在定义Cached Gauge时，我们需要指定value的超时时间和TimeUnit，并且重写loadValue方法。
 * 运行下面的程序我们会看到，在30秒内value值没有发生任何变化，因为它是直接从缓存中获取的数据。程序输出具体如下。
 */
public class CachedGaugeExample {
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
        //定义Metric，并且注册到Metric Registry中
        registry.gauge("cached-db-size", () -> new CachedGauge<Long>(30, TimeUnit.SECONDS) {

            @Override
            protected Long loadValue() {
                // 从数据库中查询数据
                return queryFromDB();
            }

        });
        Thread.currentThread().join();
    }

    private static long queryFromDB() {
        System.out.println("====queryFromDB====");
        return System.currentTimeMillis();
    }

}