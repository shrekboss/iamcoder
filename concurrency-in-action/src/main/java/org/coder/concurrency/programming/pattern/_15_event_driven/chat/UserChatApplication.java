package org.coder.concurrency.programming.pattern._15_event_driven.chat;

import org.coder.concurrency.programming.pattern._15_event_driven.AsyncEventDispatcher;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserChatApplication {

    public static void main(String[] args) {
        final AsyncEventDispatcher dispatcher = new AsyncEventDispatcher();
        dispatcher.registerChannel(UserOnlineEvent.class, new UserOnlineEventChannel());
        dispatcher.registerChannel(UserOfflineEvent.class, new UserOfflineEventChannel());
        dispatcher.registerChannel(UserChatEvent.class, new UserChatEventChannel());
        new UserChatThread(new User("Leo"), dispatcher).start();
        new UserChatThread(new User("Alex"), dispatcher).start();
        new UserChatThread(new User("Tina"), dispatcher).start();
    }
}
