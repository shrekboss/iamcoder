package org.coder.concurrency.programming.juc._7_metrics.metric;

/**
 * 第七章 Metrics(Powerful Toolkit For Measure)
 * Metrics最早是在Java的另外一个开源项目dropwizard中使用，主要是为了提供对应用程序各种关键指标的度量手段以及报告方式，
 * 由于其内部的度量手段科学合理，源码本身可扩展性极强，现在已经被广泛使用在各大框架平台中，比如我们常见的Kafka，Apache Storm，
 * Spring Cloud等。dropwizard项目的官方地址为：https://www.dropwizard.io/en/latest/感兴趣的读者也可以了解一下。
 * 在本章中我们将会全面详细的了解什么是Metrics，如何使用Metrics，在Metrics中有哪些组件（Metric，MetricsRegister， Report，Metric常见的插件等），
 * Metrics会对我们的应用程序带来哪些监控度量方面的便利等。由于其内部实现源码非常优雅，因此在本章中我们也会讲述Metrics的部分核心源码，
 * 让读者更加深入Metrics的原理，方便根据自己的业务需求进行二次开发。
 * 
 * 7.1 Metrics快速入门
 * Metrics是一个非常轻量级的框架，其核心jar包只有134KB的大小，在使用的过程中只需要通过Maven对其进行引入即可。
 * 在正式学习Metrics之前我们先来讨论一下几种常见的应用程序监控度量手段。
 * 
 * 7.1.1 如何监控度量应用程序
 * 在将应用程序部署到了生产环境中之后，我们一般很想知道某些指标的数据，比如当前有多少用户在线程、有哪些服务的调用出现了问题、某个服务接口被调用了多少次、业务受理的成功率（或失败率）、服务接口的平均响应时长等。
 * 当然我们有多种办法实现这样的功能，比如以下几种。
 * 1.实时更新所要监控的数据并将其记录在数据库中：这种方式毫无疑问可以实现我们想要的性能数据，但是可能会对数据库形成一定的压力，并且让业务程序与性能监控程序产生耦合。
 * 2.将所要监控的数据写入日志：通过输出日志的方式记录所要监控的数据，然后由另外的程序（Apache Flume、LogStash、splunk）采集日志文本，经过分析之后存入关系型数据库中。
 * 		这种解决方案目前应用比较广泛，因为它做到了真正的无入侵性，应用程序根本不知道监控程序的存在，只需要根据一定的规范打印日志即可。
 * 3.采用JMX的方式监控性能数据：将需要监控的性能数据封装成符合JMX规范的MBean，这样我们就可以借助于JMX客户端程序（jconsole、jvisualvm）进行远程查看。
 * 4.提供嵌入式的RESTful接口：如同JMX监控的方式一样，我们可以提供RESTful服务接口，将需要特别监控的数据封装成Resource，对外提供HTTP的访问。
 * 5.借助于Metrics工具集：收集性能监控数据，然后将数据交给Reporter进行不同形式的展现，甚至还可以将Metrics收集到的数据与目前比较强大的运维监控工具Ganglia、Graphite等结合在一起。
 * 		虽然Metrics收集到的数据也需要在应用程序中侵入性能数据收集的相关代码，但是这种方式基本上不会影响业务程序的运行，因为它对度量数据的report完全是以异步的方式进行的。
 * 
 * 7.1.2 Metrics 环境搭建
 * Metrics提供了非常强大的性能数据收集方式，并且在Metrics内部集成了CSV、JMX、Log、Console四大Reporter，
 * 除此之外，Reporter是一个非常易于扩展的接口，使用者可以通过自定义Reporter的形式将Metrics收集到的数据展示（存储）在任何地方。
 * 
 * metrics的官网地址：https://metrics.dropwizard.io/3.1.0/
 * metrics就是一个jar包，我们可以通过增加pom的方式为应用程序加入依赖。
 * <dependency>
 * 		<groupId>io.dropwizard.metrics</groupId>
 * 		<artifactId>metrics-core</artifactId>
 * 		<version>3.2.6</version>
 * </dependency>
 */
public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}