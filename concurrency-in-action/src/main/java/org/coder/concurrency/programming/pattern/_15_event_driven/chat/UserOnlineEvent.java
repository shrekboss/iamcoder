package org.coder.concurrency.programming.pattern._15_event_driven.chat;

import org.coder.concurrency.programming.pattern._15_event_driven.Event;

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
public class UserOnlineEvent extends Event {
    private final User user;

    public UserOnlineEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
