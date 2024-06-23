package org.coder.design.patterns._4_design_patterns._3_behavior._1_observer._cases._3_mock_eventbus;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * EventBus 实现的是同步阻塞的观察者模式
 * <a href="https://github.com/google/guava">Google Guava EventBus 的源码</a>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class EventBus {

    private Executor executor;
    private ObserverRegistry registry = new ObserverRegistry();

    /**
     * MoreExecutors.directExecutor() 是 Google Guava 提供的工具类，看似是多线程，实际上是单线程。
     * 之所以要这么实现，主要还是为了跟 AsyncEventBus 统一代码逻辑，做到代码复用
     */
    public EventBus() {
        this(MoreExecutors.directExecutor());
    }

    public EventBus(Executor executor) {
        this.executor = executor;
    }

    public void register(Object observer) {
        registry.register(observer);
    }

    /**
     * @param event 方法的参数
     */
    public void post(Object event) {
        List<ObserverAction> observerActions = registry.getMatchedObserverActions(event);
        for (ObserverAction observerAction : observerActions) {
            executor.execute(() -> {
                observerAction.execute(event);
            });
        }
    }
}
