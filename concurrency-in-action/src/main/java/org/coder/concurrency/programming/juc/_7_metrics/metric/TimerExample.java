package org.coder.concurrency.programming.juc._7_metrics.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.2.5 Timer
 * Timer是基于Histogram和Meter的一种针对度量数据进行统计的方式，主要用于统计业务方法的响应速度，简单来说就是调用某个业务接口共花费了多少时间。
 * 示例程序代码如下。
 * <p>
 * 代码中的注释已对Timer的相关说明做了比较详细的介绍，这里不再赘述。运行上面的程序将会看到business方法的调用度量数据统计结果，输出如下。
 * <p>
 * 通过度量统计我们可以发现，business方法执行一次大约会耗费5.28秒，也就是在1秒内会被调用0.17次。
 */
public class TimerExample {
    //定义Metric Registry
    private final static MetricRegistry registry = new MetricRegistry();
    //构造Reporter
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //定义Timer的Metric并且注入到Registry中
    private final static Timer timer = registry.timer("request", Timer::new);

    public static void main(String[] args) {
        // 启动 Reporter
        reporter.start(10, TimeUnit.SECONDS);
        //无限循环，模拟持续服务
        while (true) {
            business();
        }
    }

    //业务受理方法
    private static void business() {
        //在方法体中定义Timer上下文
        Timer.Context context = timer.time();

        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //方法执行结束之后stop timer 上下文
            context.stop();
        }
    }

}