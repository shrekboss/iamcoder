package org.coder.concurrency.programming.pattern._14_event_bus.monitor;

import org.coder.concurrency.programming.pattern._14_event_bus.AsyncEventBus;
import org.coder.concurrency.programming.pattern._14_event_bus.DirectoryTargetMonitor;
import org.coder.concurrency.programming.pattern._14_event_bus.EventBus;
import org.coder.concurrency.programming.pattern._14_event_bus.FileChangeListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class MonitorClient {

    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() * 2);

        final EventBus eventBus = new AsyncEventBus(executor);
        //注册
        eventBus.register(new FileChangeListener());

        DirectoryTargetMonitor monitor = new DirectoryTargetMonitor(eventBus, "/Users/crayzer/workspaces/iamcoder/concurrency-in-action/src/main/java/org/coder/concurrency/programming/pattern/_14_event_bus/monitor");
        monitor.startMonitor();
    }
}
