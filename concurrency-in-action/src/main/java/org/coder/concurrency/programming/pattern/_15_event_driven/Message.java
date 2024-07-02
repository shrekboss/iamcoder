package org.coder.concurrency.programming.pattern._15_event_driven;

/**
 * (what) 在基于 Message 的系统中，每一个 Event 也可以被称为 Message，Message 是对 Event 更高一个层级的抽象，每一个 Message 都有一个特定
 * 的 Type 用于与对应的 Handler 做关联。
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Message {

    Class<? extends Message> getType();
}
