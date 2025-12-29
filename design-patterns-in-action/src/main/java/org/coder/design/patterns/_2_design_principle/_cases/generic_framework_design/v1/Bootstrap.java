package org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v1;

import org.coder.design.patterns.simulate.vo.RequestInfo;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Bootstrap {

    public static void main(String[] args) {
        MetricsStorage storage = new RedisMetricsStorage();
        ConsoleReporter consoleReporter = new ConsoleReporter(storage);
        consoleReporter.startRepeatedReport(60, 60);

        EmailReporter emailReporter = new EmailReporter(storage);
        emailReporter.addToAddress("crazyer.chen@gmail.com");
        emailReporter.startDailyReport();

        MetricsCollector collector = new MetricsCollector(storage);
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
