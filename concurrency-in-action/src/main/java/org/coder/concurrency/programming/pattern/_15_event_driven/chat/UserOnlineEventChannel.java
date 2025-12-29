package org.coder.concurrency.programming.pattern._15_event_driven.chat;

import org.coder.concurrency.programming.pattern._15_event_driven.AsyncChannel;
import org.coder.concurrency.programming.pattern._15_event_driven.Event;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserOnlineEventChannel extends AsyncChannel {
    @Override
    protected void handle(Event message) {
        UserOnlineEvent event = (UserOnlineEvent) message;
        System.out.println("The User[" + event.getUser().getName() + "] is online.");
    }
}
