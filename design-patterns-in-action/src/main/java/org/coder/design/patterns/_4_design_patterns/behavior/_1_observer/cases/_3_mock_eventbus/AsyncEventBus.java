package org.coder.design.patterns._4_design_patterns.behavior._1_observer.cases._3_mock_eventbus;

import java.util.concurrent.Executor;

/**
 * AsyncEventBus 实现的是异步非阻塞的观察者模式
 * <a href="https://github.com/google/guava">Google Guava EventBus 的源码</a>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class AsyncEventBus extends EventBus {

    public AsyncEventBus(Executor executor) {
        super(executor);
    }
}
