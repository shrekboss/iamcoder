package org.coder.concurrency.programming.pattern._14_event_bus;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SimpleSubscriber1 {

    @Subscribe
    public void method1(String message) {
        System.out.println("==SimpleSubscriber1==method1==" + message);
    }

    @Subscribe(topic = "test")
    public void method2(String message) {
        System.out.println("==SimpleSubscriber1==method2==" + message);
    }
}
