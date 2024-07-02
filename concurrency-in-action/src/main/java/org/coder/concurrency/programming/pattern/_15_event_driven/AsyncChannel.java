package org.coder.concurrency.programming.pattern._15_event_driven;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public abstract class AsyncChannel implements Channel<Event> {
    private final ExecutorService executorService;

    public AsyncChannel() {
        this(Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * 2));
    }

    public AsyncChannel(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * 重写 dispatch 方法，并且用 final 修饰，避免子类重写
     */
    @Override
    public final void dispatch(Event message) {
        executorService.submit(() -> this.handle(message));
    }

    /**
     * 提供抽象方法，供子类实现具体的 Message 处理
     */
    protected abstract void handle(Event message);

    void stop() {
        if (null != executorService && !executorService.isShutdown())
            executorService.shutdown();
    }
}
