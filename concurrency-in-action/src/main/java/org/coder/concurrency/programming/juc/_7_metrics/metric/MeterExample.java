package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.2 五大Metric详解
 * 简单了解了Metrics的作用之后，下面就来看看如何使用Metrics为应用程序提供度量手段。
 * Metrics包含三大组件，分别是Reporter、Metric及MetricRegistry。
 * Metrics组件关系如图7-1所示。
 * <p>
 * 从图7-1所示的Metrics组件关系图中，我们可以看到各个组件的关系，首先，在应用程序中植入Metric用于收集系统运行时产生的性能数据，
 * 各个Metric被注册在MetricRegistry中，Reporter从MetricRegistry中获取各个Metric的数据，然后进行输出或存储等操作。
 * <p>
 * Metrics为我们提供了五大可用的Metric组件，本节将详细介绍每一个Metric的作用及用法。
 * <p>
 * 7.2.1 Meter
 * Meter主要用来测量一组事件发生的速率，比如我们可以用它来度量某个服务接口被调用的频率，甚至可以用它来度量某些网络操作的吞吐量。
 * 下面来看一下示例代码。
 * <p>
 * 下面简单解释一下上面的这段代码。
 * 1).定义一个MetricRegistry，它的作用就是一个Metric的注册表，其将所有的Metric注册在该表中，以方便Reporter对其进行获取。
 * 2).定义了一个用于度量TQS的Meter，通过registry创建meter，除了会创建出一个Metric之外，还会将创建好的Metric顺便注册到注册表中。
 * 3).同2，定义了一个用于度量VOLUME的Meter。
 * 4).注释4处定义了一个ConsoleReporter，并且指定了将从哪个registry中获取Metric的度量数据。
 * 5).启动Reporter，每隔10秒的时间将会对Registry中的所有Metric进行一次report。
 * 6).注释6处采用无限循环的方式模拟程序提供了不间断的服务。
 * 7).注释7处调用数据上传方法，上传数据的大小是根据随机数获得的。
 * 8).注释8处短暂休眠一段随机的时间。
 * 9).对upload方法的每一次调用都会对tqs meter 进行一次mark，也就意味着对其进行了一次计数。
 * 10).对upload方法的每一次调用都会通过volume meter对上传上来的字节流进行计数，以用于度量吞吐量。
 * <p>
 * 运行下面的程序，我们会看到ConsoleReporter会每隔10秒的时间对对量数据进行输出。
 * <p>
 * 通过Reporter的输出我们可以看到，upload方法调用了10次，
 * 通过EWMA模型（Exponentially Weighted Moving-Average，指数加权移动平均值的控制图）的统计可以得出，
 * 这些数据一分钟的平均速率将是14.88次，每分钟上传文件的平均字节数是7723.02字节。
 */
public class MeterExample {
    //1.定义MetricRegistry
    private final static MetricRegistry registry = new MetricRegistry();
    //2.定义名为tqs的Meter
    private final static Meter requestMeter = registry.meter("tqs");
    //3.定义名为volume的Meter
    private final static Meter sizeMeter = registry.meter("volume");

    public static void main(String[] args) {
        //4.定义ConsoleReporter并且设定相关的参数
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.MINUTES)
                .convertDurationsTo(TimeUnit.MINUTES)
                .build();
        //5.启动Reporter，每隔10秒运行一次
        reporter.start(10, TimeUnit.SECONDS);
        //6.提供在线服务
        for (; ; ) {
            //7.上传数据
            upload(new byte[ThreadLocalRandom.current().nextInt(1000)]);
            //8.随机休眠
            randomSleep();
        }
    }

    /**
     * 上传数据到服务器
     */
    private static void upload(byte[] request) {
        //9.对每一次的update方法调用一次mark
        requestMeter.mark();
        //10.对上传的数据长度进行mark
        sizeMeter.mark(request.length);
    }

    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextLong(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}