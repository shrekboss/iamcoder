package org.coder.concurrency.programming.pattern._15_event_driven;

import java.util.HashMap;
import java.util.Map;

/**
 * (what) EventDispatcher 是对 DynamicRouter 的一个最基本的实现，适合在单线程的情况下进行使用，因此不需要考虑线程安全的问题。
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class EventDispatcher implements DynamicRouter<Message> {
    private final Map<Class<? extends Message>, Channel> routerTable;

    public EventDispatcher() {
        this.routerTable = new HashMap<>();
    }

    @Override
    public void dispatch(Message message) {
        if (routerTable.containsKey(message.getType())) {
            routerTable.get(message.getType()).dispatch(message);
        } else
            throw new MessageMatcherException("Can't match the channel for [" + message.getType() + "] type");
    }

    @Override
    public void registerChannel(Class<? extends Message> messageType,
                                Channel<? extends Message> channel) {
        this.routerTable.put(messageType, channel);
    }
}
