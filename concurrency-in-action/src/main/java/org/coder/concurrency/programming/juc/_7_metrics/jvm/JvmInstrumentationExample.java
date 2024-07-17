package org.coder.concurrency.programming.juc._7_metrics.jvm;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import java.util.concurrent.TimeUnit;

/**
 * 7.4.2 JVM Instrumentation
 * JVM Instrumentation插件，提供了大量的针对Java虚拟机的相关信息度量，并且这里面所有的Metric都是以Metric Set的形式出现的，
 * 关于Metric Set，将在7.5.3节中进行了介绍，当前版本的Metrics-JVM插件大致提供了如下的Metric Set。
 * 1.BufferPoolMetricSet：JVM缓冲池相关。
 * 2.CachedThreadStatesGaugeSet：与JVM线程信息相关的CachedGauge。
 * 3.ClassLoadingGaugeSet：类加载器相关。
 * 4.FileDescriptorRatioGauge：文件句柄或者文件描述符的使用率。
 * 5.GarbageCollectorMetricSet：JVM垃圾回收器相关的MetricSet。
 * 6.MemoryUsageGaugeSet：JVM内存使用情况的MetricSet。
 * 7.ThreadStatesGaugeSet：线程状态的MetricSet。
 * <p>
 * 下面就来使用其中的几个为大家演示一下，剩下的如果大家感兴趣则可以自行尝试，使用起来都是非常简单的，示例程序代码如下。
 * <p>
 * 在下面的代码中，我们在MetricRegistry中注册了三个MetricSet，运行下面的代码，将会得到如下的程序输出。
 */
public class JvmInstrumentationExample {

    private final static MetricRegistry registry = new MetricRegistry();
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();

    public static void main(String[] args) throws InterruptedException {
        reporter.start(0, 10, TimeUnit.SECONDS);
        // 注册MetricSet需要调用registryAll方法
        registry.registerAll(new GarbageCollectorMetricSet());
        registry.registerAll(new ThreadStatesGaugeSet());
        registry.registerAll(new ClassLoadingGaugeSet());
        Thread.currentThread().join();
    }

}