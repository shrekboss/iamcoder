package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowReservoir;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 4.Sliding Time Window Reservoirs
 * Sliding Time Window Reservoirs（时间滑动窗口）的原理也是非常简单的，主要是根据指定的、最近的时间范围内的数据进行median（中间值）的计算，
 * 示例程序代码如下。
 * <p>
 * 在定义SlidingTimeWindowReservoir时间滑动窗口的时候，我们需要指定该窗口的时间大小，
 * 比如上述代码中的30秒，这就是意味着最多将针对最近30秒以上内的度量数据进行中间值的计算。
 */
public class SlidingTimeWindowReservoirHistogramExample {
    //定义Metric Registry
    private final static MetricRegistry registry = new MetricRegistry();
    //构造Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //构造Histogram Metric 并且使用SlidingTimeWindowReservoir
    private final static Histogram histogram = new Histogram(new SlidingTimeWindowReservoir(30, TimeUnit.SECONDS));

    public static void main(String[] args) {
        // 启动 Reporter
        reporter.start(10, TimeUnit.SECONDS);
        //将histogram metric注册到Registry中
        registry.register("SlidingTimeWindowReservoir-Histogram", histogram);
        while (true) {
            doSearch();
            randomSleep();
        }
    }

    private static void doSearch() {
        histogram.update(ThreadLocalRandom.current().nextInt(10));
    }

    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextLong(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}