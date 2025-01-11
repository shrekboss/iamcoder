package org.coder.concurrency.programming.juc._7_metrics.healthcheck;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;

import java.util.concurrent.TimeUnit;

/**
 * 7.4 Metric Plugins
 * 截至目前，我们基本上掌握了Metrics的大部分技术细节和使用方式，通过前文（尤其是7.2节“五大Metric详解”和7.3节“Reporter详解”）的学习，
 * 我们将Metrics引入自己的项目中作为一个度量工具应该说是毫无压力了。
 * <p>
 * Metrics之所以如此受欢迎，是它除了提供了一套度量指标的标准之外，其源码层次分明、可扩展性强也是一个非常重要的因素。
 * 在GitHub上，与Metrics插件相关的项目非常多，远远超过了Metrics内核本身的代码量，本节也将详细介绍其中的两个插件，
 * 以方便大家在工作中遇到好的Metrics插件时可以快速地引入，并直接使用。
 * <p>
 * 7.4.1 Health Check
 * 通常情况下，在我们的应用程序部署到服务器之后，首先会做一次冒烟测试（测试应用程序的基本功能，以确保其能够提供正常的服务），有些人将其称之为release的Health Check，
 * 当然，程序release之后需要可持续的在线服务，Health Check同样也是非常重要的，这有助于运维人员被动式地收到告警，及时解决问题，本节就将会学习Metrics Health Check插件的使用方法。
 * <p>
 * Health check 的依赖并没有包含在Metrics核心源码中，需要我们手动引入，引入代码如下所示。
 * <dependency>
 * <groupId>io.dropwizard.metrics</groupId>
 * <artifactId>metrics-healthchecks</artifactId>
 * <version>3.2.6</version>
 * </dependency>
 * <p>
 * 1.死锁检查
 * 在使用Health Check这一插件的时候，我们需要定义HealthCheckRegistry，但是这个Registry与MetricRegistry完全不是一个概念，
 * 虽然它们都是Registry，前者主要用于注册HealthCheck的子类，后者主要用于注册各种不同类型的Metric。
 * 首先需要将你所关心的检查内容（HealthCheck子类）注册至HealthCheckRegistry中，然后将Health Check所有子类的运行结果作为一个Gauge注册至Metric Registry中。
 * <p>
 * 上面这段文字的描述，可能并不是很直观，下面通过一个检测JVM应用进程是否出现死锁的健康检查需要依赖Metrics JVM插件，因为这里还需要引入对Metrics JVM的依赖，引入代码如下。
 * <dependency>
 * <groupId>io.dropwizard.metrics</groupId>
 * <artifactId>metrics-jvm</artifactId>
 * <version>3.2.6</version>
 * </dependency>
 * <p>
 * 添加了相关的pom依赖之后，我们就可以开发第一个Health Check程序了，代码如下所示。
 * <p>
 * 在这段代码中，我们使用ThreadDeadlockHealthCheck进行当前JVM是否出现了死锁的健康检查，
 * 但是在使用的过程中似乎出现了一些麻烦，初学者会感到有些不知所云，下面就来针对这段代码进行一个比较详细的解释。
 * 1.首先在代码注释1处，我们需要定义一个HealthCheckRegistry，这个Registry只能存放HealthCheck类的子类，
 * 无论是HealthCheckRegistry还是HealthCheck都与Metrics核心框架没有多大的关系，
 * 虽然HealthCheckRegistry看起来也是一个Registry，会让人误以为是MetricRegistry的子类或者扩充类什么的。
 * 2.在代码注释2处，将ThreadDeadlockHealthCheck注册至hcRegistry中，该类是一个HealthCheck的实现，
 * 其内部原理就是通过JVM MBean的形式获得当前JVM的线程堆栈。
 * 3.对于代码注释3处，相信无需做过多解释，大家也都很清楚，但是这里需要思考一个问题，健康检查的Registry是如何绑定在Metrics的Registry中的呢？
 * 它们彼此之间是没有任何关系的。
 * 4.事实上，它们两者之间确实没有任何关系，想要Health Check的执行以及输出被Metrics接管，我们需要将其转换为某个Metric，因此代码注释4处的作用正是如此，
 * 它将Health Check Registry中的所有Health Check实例都执行了一遍，最后将结果作为一个Gauge Metric传递给Metrics Regitsry，
 * 这种方式非常巧妙地制造了它们之间的连接。
 * <p>
 * 好了，运行下面的程序代码，我们看到如下的输出信息，该输出告知我们，当前的JVM没有发生死锁的情况（isHealthy=true）。
 */
public class DeadLockHealthCheckExample {

    public static void main(String[] args) throws InterruptedException {
        // 1.定义HealthCheckRegistry
        final HealthCheckRegistry hcRegistry = new HealthCheckRegistry();
        // 2.注册ThreadDeadlockHealthCheck
        hcRegistry.register("thread-dead-lock-hc", new ThreadDeadlockHealthCheck());

        // 3.定义MetricRegistry
        final MetricRegistry registry = new MetricRegistry();
        final ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();

        //执行HealthCheckRegistry中所有的hc，并将结果作为Gauge
        registry.gauge("thread-dead-lock-hc", () -> hcRegistry::runHealthChecks);
        reporter.start(10, TimeUnit.SECONDS);
        //join主线程，防止程序退出
        Thread.currentThread().join();
    }

}