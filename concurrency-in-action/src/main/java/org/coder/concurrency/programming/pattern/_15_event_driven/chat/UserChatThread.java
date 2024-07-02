package org.coder.concurrency.programming.pattern._15_event_driven.chat;

import org.coder.concurrency.programming.pattern._15_event_driven.AsyncEventDispatcher;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ThreadLocalRandom.current;

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
public class UserChatThread extends Thread {
    private final User user;
    private final AsyncEventDispatcher dispatcher;

    public UserChatThread(User user, AsyncEventDispatcher dispatcher) {
        super(user.getName());
        this.user = user;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        try {
            dispatcher.dispatch(new UserOnlineEvent(user));
            for (int i = 0; i < 5; i++) {
                dispatcher.dispatch(new UserChatEvent(user, getName() + "-Hello-" + i));
                TimeUnit.SECONDS.sleep(current().nextInt(10));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            dispatcher.dispatch(new UserOfflineEvent(user));
        }
    }
}
