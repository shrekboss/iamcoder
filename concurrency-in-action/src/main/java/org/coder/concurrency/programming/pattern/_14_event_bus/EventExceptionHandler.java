package org.coder.concurrency.programming.pattern._14_event_bus;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface EventExceptionHandler {

    void handle(Throwable cause, EventContext context);
}
