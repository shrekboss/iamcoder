package org.coder.design.patterns._2_design_principle.cases.generic_framework_design.v3;

import org.coder.design.patterns._2_design_principle.cases.generic_framework_design._simulate.RequestInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class PerfCounterTest {
    public static void main(String[] args) {
//        MetricsStorage storage = new RedisMetricsStorage();
//        Aggregator aggregator = new Aggregator();

        // 定时触发统计并将结果显示到终端
//        ConsoleViewer consoleViewer = new ConsoleViewer();
//        ConsoleReporter consoleReporter = new ConsoleReporter(storage, aggregator, consoleViewer);
//        consoleReporter.startRepeatedReport(60, 60);

        ConsoleReporter closeConsoleReporter = new ConsoleReporter();
        closeConsoleReporter.startRepeatedReport(60, 60);

        // 定时触发统计并将结果输出到邮件
//        EmailViewer emailViewer = new EmailViewer();
//        emailViewer.addToAddress("crazyer.chen@gmail.com");
//        EmailReporter emailReporter = new EmailReporter(storage, aggregator, emailViewer);
//        emailReporter.startDailyReport();
        List<String> emailToAddresses = new ArrayList<>();
        emailToAddresses.add("crazyer.chen@gmail.com");
        EmailReporter emailReporter = new EmailReporter(emailToAddresses);
        emailReporter.startDailyReport();

        // 收集接口访问数据
//        MetricsCollector collector = new MetricsCollector(storage);
        MetricsCollector collector = new MetricsCollector();

        collector.recordRequest(new RequestInfo("register", 123, 10234));
        collector.recordRequest(new RequestInfo("register", 223, 11234));
        collector.recordRequest(new RequestInfo("register", 323, 12334));
        collector.recordRequest(new RequestInfo("login", 23, 12434));
        collector.recordRequest(new RequestInfo("login", 1223, 14234));

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}