package org.coder.concurrency.programming.pattern._15_event_driven;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Event implements Message {
    @Override
    public Class<? extends Message> getType() {
        return getClass();
    }
}
