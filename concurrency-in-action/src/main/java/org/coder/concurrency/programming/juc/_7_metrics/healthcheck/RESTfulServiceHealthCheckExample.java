package org.coder.concurrency.programming.juc._7_metrics.healthcheck;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

import java.util.concurrent.TimeUnit;

/**
 * 定义扩展了RESTful接口的健康检查，我们需要将其应用在Metrics中，实现方式与7.4.1节中示例代码非常类似。
 * 
 * 上面的代码除了注释1之外，其余的代码都与7.4.1节第1小节中的代码一致，因此，这里就不做详细解释了。
 * 运行上面的程序，我们会看到每隔10秒的时间，RESTful接口会被执行一次调用，用于相应的健康检查，程序输出具体如下。
 */
public class RESTfulServiceHealthCheckExample {

	public static void main(String[] args) throws InterruptedException {
		final HealthCheckRegistry hcRegistry = new HealthCheckRegistry();
		// 1.将RESTfulServiceHealthCheck注册至HealthCheckRegistry中
		hcRegistry.register("restful-hc", new RESTfulServiceHealthCheck());
		
		final MetricRegistry registry = new MetricRegistry();
		final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
		registry.gauge("restful-hc", () -> hcRegistry::runHealthChecks);
		reporter.start(10, TimeUnit.SECONDS);
		Thread.currentThread().join();
	}

}