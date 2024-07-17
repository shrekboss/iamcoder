package org.coder.concurrency.programming.juc._7_metrics.report;

import com.codahale.metrics.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.3.3 JMXReporter
 * 除了使用Log相关的Reporter替代Console Reporter之外，将Metric的度量报告通过JMX MBean的方式展现出来，其实是一种更好的方式，
 * 尤其是在提供在线服务的平台、服务中，比如Apache Kafka的Metric信息就提供了非常详细的JMX接口暴露，这样的话，如果我们想要远程监控获取某些性能指标，就非常容易了。
 * <p>
 * JMXReporter与Console Reporter、Logger Reporter不一样的是，其内部并未提供定时线程服务，因此我们在对其进行start操作时，无需给定任何时间间隔及时间单位，
 * JMXReporter更多的操作其实是将Metric Registry中的所有Metric定义成MBean，并且注册到Object NameFactory中。
 * <p>
 * 1.JMXReporter实战
 * 如3.2.3节一样，我们只需要将对应的Reporter替换成JMX的Reporter即可，但是这里需要注意的一点是，start方法与前两个方法（Console、 Logger）在形式上和本质是不同的。
 * <p>
 * 2.使用JMX客户端获取Metric度量数据报告
 * 运行修改后的代码，然后打开JMX的客户端（比如，jconsole），将会看到jmxReporter已经自动将Metrics中注册的Metric转换成了Mbean，
 * 届时，我们可以通过JMX统计进行度量指标的观察。
 */
public class JMXReporterExample {

    private final static MetricRegistry registry = new MetricRegistry();

    private final static Counter totalBusiness = new Counter();
    private final static Counter successBusiness = new Counter();
    private final static Counter failBusiness = new Counter();
    private final static Timer timer = new Timer();
    private final static Histogram volumeHistogram = new Histogram(new ExponentiallyDecayingReservoir());
    private final static JmxReporter reporter = JmxReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();

    private final static RatioGauge successGauge = new RatioGauge() {

        @Override
        protected Ratio getRatio() {
            return Ratio.of(successBusiness.getCount(), totalBusiness.getCount());
        }

    };

    static {
        registry.register("cloud-disk-upload-total", totalBusiness);
        registry.register("cloud-disk-upload-success", successBusiness);
        registry.register("cloud-disk-upload-failure", failBusiness);
        registry.register("cloud-disk-upload-frequency", timer);
        registry.register("cloud-disk-upload-volume", volumeHistogram);
        registry.register("cloud-disk-upload-suc-rate", successGauge);
    }

    public static void main(String[] args) {
        reporter.start();
        while (true) {
            upload(new byte[ThreadLocalRandom.current().nextInt(10_000)]);
        }
    }

    private static void upload(byte[] buffer) {
        totalBusiness.inc();
        Timer.Context context = timer.time();
        try {
            int x = 1 / ThreadLocalRandom.current().nextInt(10);
            TimeUnit.MILLISECONDS.sleep(200);
            volumeHistogram.update(buffer.length);
            successBusiness.inc();
        } catch (Exception e) {
            failBusiness.inc();
        } finally {
            context.close();
        }
    }

}