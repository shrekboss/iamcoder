package org.coder.concurrency.programming.pattern._14_event_bus;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Test {

    public static void main(String[] args) {
        //1.同步Event Bus
        Bus bus = new EventBus("TestBus");
        bus.register(new SimpleSubscriber1());
        bus.register(new SimpleSubscriber2());
        bus.post("Hello");
        System.out.println("-----------------------");
        bus.post("Hello", "test");

        //2.异步Event Bus
        //Bus bus = new AsyncEventBus("TestBus", (ThreadPoolExecutor) Executors.newFixedThreadPool(10));
        //bus.register(new SimpleSubscriber1());
        //bus.register(new SimpleSubscriber2());
        //bus.post("Hello");
        //System.out.println("-----------------------");
        //bus.post("Hello", "test");
    }

}
