package org.coder.concurrency.programming.juc._7_metrics.report;

import com.codahale.metrics.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.3 Reporter详解
 * 7.2节非常详细地介绍了Metrics的五大度量方式，其中我们接触到了如何将Metrics工具收集到的数据进行输出（通过控制台的方式进行输出），
 * Metrics内置了4种输出度量报告的形式，同时，我们也可以看到某些第三方平台和框架也开发了对应的Reporter插件，
 * 比如将Metrics度量数据发布到Ganglia、Graphite等监控工具中，当然如果你非常熟悉某个监控工具，那么你甚至可以自定义属于自己的Reporter，然后进行展示。
 * <p>
 * 7.3.1 ConsoleReporter
 * ConsoleReporter正如其名字一样，用于将Metric度量数据进行控制台输出，也是大家演示的一种Reporter。
 * 当然这种Reporter主要应用于开发阶段，并不推荐将其直接使用在生产环境中进行度量数据的汇总输出。
 * <p>
 * 在构造ConsoleReporter的时候，我们采用它所提供的静态方法forRegistry()对其进行构造。
 * 这也不难理解，Reporter的数据主要来源于MetricRegistry中的各种Metric，调用了forRegistry方法之后，
 * 事实上会返回一个Builder对象，该对象提供了很多用于控制信息的输出方式和方法参数。
 * 本节简单列举几个进行说明即可，其余的则可以采用默认的方式。
 * 1.convertRatesTo
 * Rate速率的意思，当通过convertRatesTo()方法传递给某个时间单位时，速率的统计单位就会以指定的单位进行输出（比如，每秒、每分、每毫秒），
 * 其实在Gauge和Counter中，关于速率的单位设定将会忽略，因为它们不进行任何关于速率的统计度量，虽然Histogram是一种统计方式，
 * 但是它同样不会用到关于速率的单位，因此我们的设定也会被忽略。所以在之前学习过的五大Metric中，只有Metric和Timer会用到Rate的时间单位设置。
 * <p>
 * 2.convertDurationsTo
 * Durations即时长的意思，当通过convertDurationsTo方法传递给某个时间单位时，时长/耗时的单位就会以我们设定的时间单位进行输出，
 * 比如，执行某个方法花费了多长时间，除了Timer会用到时长的时间单位之外，其他的四个Metric都不会使用到这个单位设定。
 * <p>
 * 3.reporter的start
 * 当reporter的所有参数通过Builder对象设置完毕之后，我们需要对其进行start调用。
 * 在start方法中，第一个参数是报告输出的时间间隔，第二个参数是时间间隔的单位。
 * 比如reporter.start(10, TimeUnit.SECONDS)：每10秒进行一次报告的输出。
 * <p>
 * 4.其他
 * 一般情况下其他的参数只需要保持默认即可，这并不会影响我们对Metrics的使用，但有些时候，如果想要做一些特殊化的定制，
 * 那就需要了解Builder提供的额外方法了。Builder提供的额外方法具体如下。
 * 1).shutdownExecutorOnStop：通过Reporter的stop方法来停止内置的线程服务，默认为true。
 * 2).scheduleOn：给定自定义的线程服务替换内置的线程服务。
 * 3).outputTo：控制台输出方式，默认为System.out，当然你也可以将其替换为文件的PrintStream，将其输出到某个文件中，甚至输出到某个网络套接字中。
 * 4).formattedFor(Locale locale)：主要用于设置Locale相关。
 * 5).withClock：主要用于控制时间（毫秒）相关。
 * 6).formattedFor(TimeZone timeZone)：主要用于设置时区相关。
 * 7).filter：如果不想让MetricRegistry中的某个Metric在该Reporter中输出，则可以传入一个MetricFilter接口实现，至此之后关于该Metric的度量信息将不会出现在Reporter的输出中。
 * 8).disabledMetricAttributes：屏蔽某个Metric属性，比如，如果不想让Timer输出太多的信息，则可以通过该方法对某个属性进行屏蔽。
 * <p>
 * 5.ConsoleReporter的综合练习
 * 了解了ConsoleReporter所有的参数及其所代表的意义之后，我们来做个比较综合的练习。
 * 在该练习中，我们假设提供了一个云盘服务接口，该接口对外提供将文件存储在云盘的功能，
 * 作为对其的监控，我们很想知道如下Metric度量信息。
 * 1).总共上传了多少次文件？
 * 2).上传总共成功了多少次？
 * 3).上传总共失败了多少次？
 * 4).上传的成功率是多少？
 * 5).上传文件大小的Histogram应该如何统计？
 * 6).上传一个文件大概需要多长的时间？
 * <p>
 * 在下述代码的注释中，笔者详细地注明了关键代码所代表的意思，因此这里就不再赘述了，我们直接运行即可，下面是程序的输出。
 * <p>
 * 通过控制台输出Reporter，我们可以很清晰地看到目前云盘的工作情况，其中有一个地方需要注意，
 * 比如cloud-disk-upload-total为49，其他的值为48，甚至成功和失败之和也等于48，这种情况也是非常容易解释的，
 * 在Reporter进行输出的时候，恰好cloud-disk-upload-total进行了一次自增操作，
 * 其他的度量值没有更新之前就被Reporter进行了输出，因此看起来存在一定的误差。
 */
public class ConsoleReporterExample {
    //定义MetricRegistry
    private final static MetricRegistry registry = new MetricRegistry();
    //定义用于统计所有文件上传的Counter Metric
    private final static Counter totalBusiness = new Counter();
    //定义用于统计所有文件上传成功的Counter Metric
    private final static Counter successBusiness = new Counter();
    //定义用于统计所有文件上传失败的Counter Metric
    private final static Counter failBusiness = new Counter();
    //定义用于统计每一个文件上传的耗时Timer Metric
    private final static Timer timer = new Timer();
    //定义用于统计上传字节volume的Histogram Metric
    private final static Histogram volumeHisto = new Histogram(new ExponentiallyDecayingReservoir());
    //定义ConsoleReporter，并且指定Rate和Duration时间单位
    private final static ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.SECONDS)
            .build();
    //定义用于统计文件上传成功率的Gauge metric
    private final static RatioGauge successGauge = new RatioGauge() {

        @Override
        protected Ratio getRatio() {
            //成功率来自successBusiness和totalBusiness counter
            return Ratio.of(successBusiness.getCount(), totalBusiness.getCount());
        }

    };

    static {
        //将所有Metric注册到Registry中
        registry.register("cloud-disk-upload-total", totalBusiness);
        registry.register("cloud-disk-upload-success", successBusiness);
        registry.register("cloud-disk-upload-failure", failBusiness);
        registry.register("cloud-disk-upload-frequency", timer);
        registry.register("cloud-disk-upload-volume", volumeHisto);
        registry.register("cloud-disk-upload-suc-rate", successGauge);
    }

    public static void main(String[] args) {
        // 启动console Reporter，每隔10秒的时间进行一次控制台输出
        reporter.start(10, TimeUnit.SECONDS);
        while (true) {
            upload(new byte[ThreadLocalRandom.current().nextInt(10_000)]);
        }
    }

    //模拟文件上传到云盘的方法
    private static void upload(byte[] buffer) {
        // 每一次文件的上传都会使totalBusiness进行一次自增操作
        totalBusiness.inc();
        //用于记录每一个文件成功写入网盘的耗时
        Timer.Context context = timer.time();
        try {
            //模拟计算，其中分母为0时代表上传失败
            int x = 1 / ThreadLocalRandom.current().nextInt(10);
            TimeUnit.MILLISECONDS.sleep(200);
            //上传成功后，将文件的字节数量纳入histogram统计中
            volumeHisto.update(buffer.length);
            //上传成功后，successBusiness自增
            successBusiness.inc();
        } catch (Exception e) {
            // 当失败发生时，failBusiness自增
            failBusiness.inc();
        } finally {
            //关闭Timer Context
            context.close();
        }
    }

}