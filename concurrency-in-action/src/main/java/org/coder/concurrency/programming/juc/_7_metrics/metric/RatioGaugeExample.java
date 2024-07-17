package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 3.Ratio Gauge 详解
 * Ratio Gauge可用于创建两个数字之间的某种比率，比如业务受理的成功率或失败率等。
 * 通常，我们在处理订单的时候，由于用户的原因可能会进行取消订单的操作，中断整个订单执行的流程。
 * 对于这样的数据统计，Ratio Gauge将会是一个非常好的选择，我们来看一下示例程序的代码。
 * <p>
 * 注释中对代码的解释已经非常清楚了，这里就不再赘述了，直接看运行结果吧。
 * <p>
 * 每隔10秒的时间，业务的成功率度量信息将会输出到控制台上，除了可以看到成功率之外，我们还可以看到ERROR字样（分母为零时会出现）。
 * 当然，totalMeter和successMeter完全可以使用AtomicLong替代，这没有任何问题，目的都主要是数值进行存储。
 */
public class RatioGaugeExample {
    //定义一个metric registry
    private final static MetricRegistry register = new MetricRegistry();
    //定义Console Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(register)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //定义两个Mertric
    private final static Meter totalMeter = new Meter();
    private final static Meter successMeter = new Meter();

    public static void main(String[] args) {
        //启动Reporter，每隔10秒的时间输出一次数据
        reporter.start(10, TimeUnit.SECONDS);
        //注册Ratio Gauge
        register.gauge("success-rate", () -> new RatioGauge() {

            @Override
            protected Ratio getRatio() {
                // ratio值等于successMeter和totalMeter
                return Ratio.of(successMeter.getCount(), totalMeter.getCount());
            }
        });
        //无限循环，模拟程序持续服务
        for (; ; ) {
            //短暂休眠
            shortSleep();

            //受理业务
            business();
        }
    }

    private static void business() {
        // 不论正确与否，total都会自增
        //total inc
        totalMeter.mark();
        try {
            //随机数有可能会是0，因此这个操作可能会出现错误
            int x = 10 / ThreadLocalRandom.current().nextInt(6);
            //success inc
            //成功受理之后，success会自增
            successMeter.mark();
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    private static void shortSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextLong(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}