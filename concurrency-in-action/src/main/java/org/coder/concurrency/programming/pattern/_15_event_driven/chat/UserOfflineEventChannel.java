package org.coder.concurrency.programming.pattern._15_event_driven.chat;

import org.coder.concurrency.programming.pattern._15_event_driven.AsyncChannel;
import org.coder.concurrency.programming.pattern._15_event_driven.Event;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserOfflineEventChannel extends AsyncChannel {
    @Override
    protected void handle(Event message) {
        UserOfflineEvent event = (UserOfflineEvent) message;
        System.out.println("The User[" + event.getUser().getName() + "] is offline.");
    }
}
