package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.2.2 Gauge
 * Gauge是最简单的Metric类型，如图7-3所示，它只返回一个Value值，比如它可以用来查看某个关键队列在某个时刻的size，或者用来查看当前网站的在线人数等。
 * 虽然Gauge的作用比较简单，但是其在实际中的应用却是比较广泛的，为此，Metrics提供了5种不同的Gauge实现，下面就来逐一讲解。
 * <p>
 * 1.Simple Gauge 详解
 * Simple Gauge就像它的名字所表明的那样，非常简单，仅会返回需要我们关注的值。比如，在block queue中，多线程同时对其进行pop及add操作，
 * 如果想要知道在某个时刻该队列的size是多少，则可以借助于Simple Gauge来进行实现。Gauge接口非常简单，下面来看一段代码。
 * public interface Gauge<T> extends Metric {
 * T getValue();
 * }
 * <p>
 * 由上述代码段可知Gauge接口只有一个方法getValue()，因此我们可以将该接口称为FunctionalInterface。
 * 好了，接下来就写一个获取queue size的metric应用程序。
 * <p>
 * 与7.2.1节中定义的Metric不同的是，这次采用的是显式定义Metric的方式，然后将其注入注册表中，代码如下。
 * metricRegistry.register(MetricRegistry.name(SimpleGaugeExample.class, "queue-size"), (Gauge<Integer>) queue::size);
 * <p>
 * 运行上面的代码，我们会看到，每隔10秒的时间，ConsoleReporter将对queue的size进行输出。
 * <p>
 * 通过输出信息，可以看到queue size的变化。通过这个度量数据，我们很容易就能发现生产者线程和消费者线程的处理速度，以及队列出现的积压情况，
 * 这对我们分析工作线程的运行性能非常重要。
 */
public class SimpleGaugeExample {
    //定义一个metric registry
    private static final MetricRegistry metricRegistry = new MetricRegistry();
    //定义Console Reporter
    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //定义一个双向队列，这个队列是需要监控的队列
    private static final BlockingDeque<Long> queue = new LinkedBlockingDeque<>(1_000);

    public static void main(String[] args) {
        //定义一个Simple Gauge，并且将其注册到registry中
        //Gauge的实现仅仅是返回queue的size，queue::size静态推导
        metricRegistry.register(MetricRegistry.name(SimpleGaugeExample.class, "queue-size"), (Gauge<Integer>) queue::size);
        reporter.start(1, TimeUnit.SECONDS);

        //启动一个线程向队列中不断放入数据
        new Thread(() -> {
            for (; ; ) {
                randomSleep();
                queue.add(System.nanoTime());
            }
        }).start();
        //启动另外一个线程，从队列中不断地poll数据
        new Thread(() -> {
            for (; ; ) {
                randomSleep();
                queue.poll();
            }
        }).start();
    }

    //随机休眠
    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextLong(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}