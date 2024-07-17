package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingWindowReservoir;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 3.Sliding Window Reservoirs
 * Sliding Window Reservoirs（滑动窗口）的原理非常简单，主要是在该窗口中存放最近的一定数量的值进行median（中间值）的计算。
 * 示例程序代码如下。
 * <p>
 * 在定义SlidingWindowReservoir滑动窗口时，我们需要指定该窗口的大小，比如上述代码中的50，这就意味着最多将针对最近的50个度量数据进行中间值的计算。
 */
public class SlidingWindowReservoirHistogramExample {
    //定义Metric Registry
    private final static MetricRegistry registry = new MetricRegistry();
    //构造Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //构造Histogram Metric 并且使用SlidingWindowReservoir，指定窗口大小为50
    private final static Histogram histogram = new Histogram(new SlidingWindowReservoir(50));

    public static void main(String[] args) {
        // 启动 Reporter
        reporter.start(10, TimeUnit.SECONDS);
        //将histogram metric注册到Registry中
        registry.register("SlidingWindowReservoir-Histogram", histogram);

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