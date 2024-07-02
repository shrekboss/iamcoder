package org.coder.concurrency.programming.pattern._15_event_driven.chat;

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
public class UserOfflineEvent extends UserOnlineEvent {
    public UserOfflineEvent(User user) {
        super(user);
    }
}
