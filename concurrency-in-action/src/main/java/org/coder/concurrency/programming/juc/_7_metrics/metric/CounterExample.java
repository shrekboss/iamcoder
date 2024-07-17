package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.2.3 Counter
 * 7.2.2节对Simple Gauge进行了详细讲述，我们使用一个简单的Gauge获取了queue的当前size作为一个Metric，这种方式看起来能够正常运行，
 * 但是调用相关API的方式获取value会影响到其他线程使用queue本身的性能，这种度量方式也会对应用程序带来性能上的侵入损耗。
 * <p>
 * Counter Metric提供了一个64位数字的递增和递减的解决方案，可以帮助我们解决在度量的过程中性能侵入的问题。
 * 下面我们采用Counter的方式改写7.2.2节Simple Gauge中所编写的程序，改写的程序代码如下。
 * <p>
 * 在下面的程序中，我们不再调用queue的size()方法作为度量值的获取方法，因为这种方式存在对被度量资源的侵入性，Counter Metric经过改造之后同样可以完成我们想要的效果。
 */
public class CounterExample {
    //定义一个metric registry
    private static final MetricRegistry metricRegistry = new MetricRegistry();
    //定义Console Reporter
    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.SECONDS).build();
    //定义Blocking双向队列，size为1000
    private static final BlockingDeque<Long> queue = new LinkedBlockingDeque<>(1_000);

    public static void main(String[] args) {

        reporter.start(10, TimeUnit.SECONDS);
        //定义并注册Counter metric到Registry中
        Counter counter = metricRegistry.counter("queue-count", Counter::new);
        //定义一个线程，用于将元素添加到queue中，但是在增加了元素之后，调用counter的递增方法
        new Thread(() -> {
            for (; ; ) {
                randomSleep();
                queue.add(System.nanoTime());
                counter.inc();
            }
        }).start();
        //定义另外一个线程，从queue中poll元素，当元素被poll出后，调用counter的递减方法
        new Thread(() -> {
            for (; ; ) {
                randomSleep();
                if (queue.poll() != null) {
                    counter.dec();
                }
            }
        }).start();
    }


    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextLong(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}