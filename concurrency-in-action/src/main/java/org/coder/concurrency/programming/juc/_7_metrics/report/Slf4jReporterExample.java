package org.coder.concurrency.programming.juc._7_metrics.report;

import com.codahale.metrics.*;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 7.3.2 LogReporter
 * Console Reporter一节中曾经提到过，控制台报告不建议用于生产环境，因为它除了在某种情况下引起线程死锁的问题（System.out），
 * 还会导致程序的性能受到影响，Metric官方充分地考虑到了这一点，因此其提供了LogReporter的方式，在生产环境中使用这种方式其实是一种不错的选择，
 * 但是相较于Console Reporter，使用LogReporter的时候会相对麻烦一些。
 * <p>
 * 1.引入log的依赖
 * <dependency>
 * <groupId>org.slf4j</groupId>
 * <artifactId>slf4j-api</artifactId>
 * <version>1.7.7</version>
 * </dependency>
 * <dependency>
 * <groupId>ch.qos.logback</groupId>
 * <artifactId>logback-core</artifactId>
 * <version>1.1.7</version>
 * </dependency>
 * <dependency>
 * <groupId>ch.qos.logback</groupId>
 * <artifactId>logback-access</artifactId>
 * <version>1.1.7</version>
 * </dependency>
 * <dependency>
 * <groupId>ch.qos.logback</groupId>
 * <artifactId>logback-classic</artifactId>
 * <version>1.1.7</version>
 * </dependency>
 * <p>
 * 2.配置log appender
 * 当你引入了log的依赖之后，需要配置相关的log appender，Metric度量信息才能作用于日志文件中，示例程序代码如下。
 * <configuration>
 * <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
 * <encoder>
 * <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
 * </encoder>
 * </appender>
 * <appender name="metrics" class="ch.qos.logback.core.FileAppender">
 * <file>metrics.log</file>
 * <encoder>
 * <pattern>%msg%n</pattern>
 * </encoder>
 * </appender>
 * <logger name="org.coder.concurrency.programming" level="INFO">
 * <appender-ref ref="METRICS"/>
 * </logger>
 * <root level="debug">
 * <appender-ref ref="STDOUT"/>
 * </root>
 * </configuration>
 * 在上面的logback.xml配置文件中，org.coder.concurrency.programming会在LogReporter中使用到。
 * <p>
 * 3.SlfjReporter实战
 * 一切准备就绪，我们只需要将ConsoleReporter替换成为Logger相关的Reporter即可，其余的代码无需进行任何改动，示例程序代码如下所示。
 * <p>
 * 修改完代码之后再次运行，你会发现多了一个metrics.log的日志文件，对该日志文件的管理，完全可以交由日志框架来维护，我们的程序需要将Reporter的内容进行输出即可。
 */
public class Slf4jReporterExample {

    private final static MetricRegistry registry = new MetricRegistry();

    private final static Counter totalBusiness = new Counter();
    private final static Counter successBusiness = new Counter();
    private final static Counter failBusiness = new Counter();
    private final static Timer timer = new Timer();
    private final static Histogram volumeHistogram = new Histogram(new ExponentiallyDecayingReservoir());
    private final static Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
            .outputTo(LoggerFactory.getLogger("org.coder.concurrency.programming"))
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
        // TODO Auto-generated method stub
        reporter.start(10, TimeUnit.SECONDS);
        while (true) {
            upload(new byte[ThreadLocalRandom.current().nextInt(10_000)]);
        }
    }

    private static void upload(byte[] buffer) {
        // TODO Auto-generated method stub
        totalBusiness.inc();
        Timer.Context context = timer.time();
        try {
            int x = 1 / ThreadLocalRandom.current().nextInt(10);
            TimeUnit.MILLISECONDS.sleep(200);
            volumeHistogram.update(buffer.length);
            successBusiness.inc();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            failBusiness.inc();
        } finally {
            context.close();
        }
    }

}