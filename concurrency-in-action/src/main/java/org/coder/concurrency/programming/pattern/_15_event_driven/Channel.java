package org.coder.concurrency.programming.pattern._15_event_driven;

/**
 * (what) 第二个比较重要的概念就是 Channels, Channel 主要用于接受来自 Event Loop 分配的消息，每一个 Channel 负责处理一种类型的消息。
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Channel<E extends Message> {

    /**
     * 用于负责 Message 的调度
     */
    void dispatch(E message);
}
