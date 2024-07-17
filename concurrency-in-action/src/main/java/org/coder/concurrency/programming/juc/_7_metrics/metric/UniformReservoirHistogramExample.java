package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.UniformReservoir;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 1.Uniform Reservoirs
 * Uniform Reservoirs采用随机的抽样来度量数据，然后存放在一个数据集合中进行中间值的统计，这种方法被称为Vitter R 算法详见。
 * 下面通过一个例子来展示并说明如何使用Uniform Reservoirs的方式进行median（中间值）的计算。示例程序如下。
 * <p>
 * 运行下面的程序，虽然输出结果与HistogramExample.java一样，但是其中对median的计算方式却是不同的。
 * 需要注意的是，这种方式非常适合于统计长时间运行的度量数据，千万不要用它来度量只需要关心最近一段时间的统计结果，因为它是采用随机抽样的方式为数据集合提供统计原料的。
 */
public class UniformReservoirHistogramExample {
    //定义Metric Registry
    private final static MetricRegistry registry = new MetricRegistry();
    //构造Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //构造Histogram Metric 并且使用UniformReservoir
    private final static Histogram histogram = new Histogram(new UniformReservoir());

    public static void main(String[] args) {
        // 启动 Reporter
        reporter.start(10, TimeUnit.SECONDS);
        //将histogram metric注册到Registry中
        registry.register("UniformReservoir-Histogram", histogram);

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