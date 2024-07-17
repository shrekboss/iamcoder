package org.coder.concurrency.programming.juc._7_metrics.healthcheck;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;

import java.util.concurrent.TimeUnit;

/**
 * 3.Health Check Set
 * 7.4.1节分别针对线程死锁，以及RESTful API进行了健康检查，如果想要将所有的健康检查都纳入Metrics Reporter中，就像纳入了所有的Metric一样，我们又该如何操作呢？
 * 其实在Metrics中，这种需求非常容易得到满足，在下面的代码中，我们就将前两节中的健康检查都纳入了Console Reporter中。
 * <p>
 * 看起来只需要调用两次HealthCheckRegistry的registry方法即可，运行上面的程序，我们会看到每隔10秒的时间，
 * HealthCheckRegistry所有的HealthCheck子类都会被调用执行一次，程序输出具体如下。
 */
public class HealthCheckSetExample {

    public static void main(String[] args) throws InterruptedException {
        final HealthCheckRegistry hcRegistry = new HealthCheckRegistry();
        // 注册restful hc
        hcRegistry.register("restful-hc", new RESTfulServiceHealthCheck());
        // 注册线程死锁hc
        hcRegistry.register("thread-dead-lock-hc", new ThreadDeadlockHealthCheck());
        final MetricRegistry registry = new MetricRegistry();
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        registry.gauge("app-health-check", () -> hcRegistry::runHealthChecks);
        reporter.start(10, TimeUnit.SECONDS);
        Thread.currentThread().join();
    }

}