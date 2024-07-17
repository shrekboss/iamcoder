package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 2.Exponentially Decaying Reservoirs
 * Exponentially Decaying Reservoirs（指数衰变）的方式既是Metrics的默认方式，也是官网推荐的一种方式，建议在平时的工作中使用这种方式即可。
 * Exponentially Decaying Reservoirs 通过一个正向衰减优先级列表来实现，该列表用于更新维护数据的指数权重，使得需要计算中间值的数据集合维持在一个特定的数量区间中，然后对其进行取中间值运算。
 * 示例程序代码如下。
 */
public class ExponentialyDecayingReservoirHistogramExample {
    //定义Metric Registry
    private final static MetricRegistry registry = new MetricRegistry();
    //构造Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //构造Histogram Metric 并且使用ExponentiallyDecayingReservoir
    private final static Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());

    public static void main(String[] args) {
        // 启动 Reporter
        reporter.start(10, TimeUnit.SECONDS);
        //将histogram metric注册到Registry中
        registry.register("ExponentiallyDecayingReservoir", histogram);
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