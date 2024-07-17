package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.2.4 Histogram
 * 直方图（Histogram）又称质量分布图，是一种统计报告图，由一系列高度不等的众向条纹或线段表示数据分布的情况。一般用横轴表示数据类型，众轴表示分布情况。
 * <p>
 * 直方图是数值数据分布的精确图形表示，这是一个对连续变量（定量变量）的概率分布的估计，并且由卡尔皮尔逊首先引入，它是一种条形图。
 * 构建直方图的步骤是，首先对值的范围进行分段，即将整个值的范围分成一系列的间隔，然后计算每个间隔中有多少个值。
 * 这些值通常被指定为连续的、不重叠的变量间隔。间隔必须相邻，并且通常是（不是必需的）相等的大小的。
 * <p>
 * Metrics还为我们提供了Histogram的数据统计方式，本节将为大家介绍如何通过Histogram Metric进行度量数据的统计。
 * 假设系统为用户提供了商品搜索功能，如果想要统计每一次用户通过关键词的搜索会产生多少条结果条目，那么我们需要特别关注搜索的结果，因此需要将其纳入度量中来，请看下面的程序示例。
 * <p>
 * 运行上面的程序，你会发现具有非常多的度量数据统计信息，这些信息对我们的帮助是非常大的，
 * 它可以很准确地告诉我们这些数据的分布情况，比如，有75%的搜索结果都少于8个条目，程序输出具体如下。
 * <p>
 * 除了要对数据区间进行统计之外，还有些数据也是非常重要的，下面就来简单介绍一下。
 * 1.count：参与统计的数据有多少条。
 * 2.min：在所有统计数据中哪个值是最小的。
 * 3.max：在所有统计数据中哪个值是最大的。
 * 4.mean：所有数据的平均值。
 * 5.stddev：统计结果的标准误差率。
 * 6.median：所有统计数据的中间值。
 * <p>
 * 上述几项统计结果中，除了median（中间值）之外，其他的都比较容易计算。
 * 传统的计算中间值的方式是所有的数据都放到一个数据集合中，然后对其进行排序整理，最后采取折半的方式获取其中的一个值，就认为其是中间值。
 * 但是，这种计算中间值的前提是需要在数据集合中记录所有的数据，这会导致我们的应用程序存放大量的度量数据，进而带来应用程序发生内存溢出的风险，显然这种方式是不可取的。
 * Metrics在设计的过程中也充分考虑到了这一点，因此为我们提供了4种解决方案来解决这样的问题，本节接下来将详细介绍。
 * <p>
 * 1.Uniform Reservoirs
 * 2.Exponentially Decaying Reservoirs
 * 3.Sliding Window Reservoirs
 * 4.Sliding Time Window Reservoirs
 */
public class HistogramExample {
    //定义Metric Registry
    private final static MetricRegistry registry = new MetricRegistry();
    //构造Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //构造Histogram Metric 并且将其注册到Registry中
    private final static Histogram histogram = registry.histogram("search-result");

    public static void main(String[] args) {
        // 启动 Reporter
        reporter.start(10, TimeUnit.SECONDS);
        //无限循环，模拟持续服务
        while (true) {
            //根据用户提交的关键字进行搜索
            doSearch();
            randomSleep();
        }
    }

    private static void doSearch() {
        // 搜索结果从随机数获得0~9之间的结果条目
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