package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.JmxAttributeGauge;
import com.codahale.metrics.MetricRegistry;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.concurrent.TimeUnit;

/**
 * 2.JMX Attribute Gauge 详解
 * 除了在应用程序中可以定义很多符合JMX标准的MBean之外，JDK还为我们提供了非常多的MBean(如图7-4)，用于诊断JVM的一些运行指标数据。
 * 如果想要获取JVM的MBean，则需要借助于jconsole、jvisualvm、jprofiler这样的工具；如果想要远程查看，那么还必须打开JMX服务端口。
 * -Djava.rmi.server.hostname=192.168.2.142
 * -Dcom.sun.management.jmxremote.port=12345
 * -Dcom.sun.management.jmxremote.ssl=false
 * -Dcom.sum.management.jmxremote.authenticate=false
 * <p>
 * 那么有某种方式，可以将MBean提供的数据直接输出到日志或者控制台上呢？答案是肯定的。
 * 比如，如果想要查看当前应用程序堆区与非堆区的使用大小情况，就可以借助于JmxAttributeGauge来很好地完成，下面看一下示例代码。
 * <p>
 * 运行上面的程序，我们会看到当前JVM堆内存的信息及非堆内存的信息每隔10秒的时间被输出一次，具体如下。
 */
public class JmxAttributeGaugeExample {
    //定义一个metric registry
    private final static MetricRegistry registry = new MetricRegistry();
    //定义Console Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    public static void main(String[] args) throws MalformedObjectNameException, IllegalArgumentException, InterruptedException {
        //启动Reporter，每隔10秒的时间输出一次数据
        reporter.start(10, TimeUnit.SECONDS);
        //注册JmxAttributeGauge，主要输出推内存的使用情况
        registry.register(
                MetricRegistry.name(JmxAttributeGaugeExample.class, "HeapMemory"),
                new JmxAttributeGauge(new ObjectName("java.lang:type=Memory"), "HeapMemoryUsage")
        );
        //注册JmxAttributeGauge，主要输出非推内存的使用情况
        registry.register(
                MetricRegistry.name(JmxAttributeGaugeExample.class, "NonHeapMemoryUsage"),
                new JmxAttributeGauge(new ObjectName("java.lang:type=Memory"), "NonHeapMemoryUsage")
        );
        //让主线程join，目的是不让程序退出
        Thread.currentThread().join();
    }

}