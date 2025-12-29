package org.coder.concurrency.programming.pattern._14_event_bus;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Deprecated
public class SimpleObject {

    @Subscribe(topic = "alex-topic")
    public void test2(Integer x) {

    }

    @Subscribe(topic = "test-topic")
    public void test3(Integer x) {

    }
}
