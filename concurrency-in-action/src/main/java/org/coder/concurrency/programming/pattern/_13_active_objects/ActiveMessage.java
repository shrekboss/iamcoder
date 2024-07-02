package org.coder.concurrency.programming.pattern._13_active_objects;

import org.coder.concurrency.programming.pattern._5_future.Future;

import java.lang.reflect.Method;

/**
 * (what)
 * <p>
 * (why) 包可见，ActiveMessage 只在框架内部使用，不会对外暴露
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
class ActiveMessage {
    private final Object[] objects;

    private final Method method;

    private final ActiveFuture<Object> future;

    private final Object service;

    private ActiveMessage(Builder builder) {
        this.objects = builder.objects;
        this.method = builder.method;
        this.future = builder.future;
        this.service = builder.service;
    }

    public void execute() {
        try {
            Object result = method.invoke(service, objects);
            if (future != null) {
                Future<?> realFuture = (Future<?>) result;
                Object realResult = realFuture.get();
                future.finish(realResult);
            }
        } catch (Exception e) {

            // 如果发生异常，那么有返回值的方法将会显示地指定结果为 null，无返回值的接口方法则会忽略异常
            if (future != null) {
                future.finish(null);
            }
        }
    }

    /**
     * Builder 主要负责对 ActiveMessage 的构建，是一种典型的 Gof Builder 设计模式
     */
    static class Builder {
        private Object[] objects;

        private Method method;

        private ActiveFuture<Object> future;

        private Object service;

        public Builder useMethod(Method method) {
            this.method = method;
            return this;
        }

        public Builder returnFuture(ActiveFuture<Object> future) {
            this.future = future;
            return this;
        }

        public Builder withObjects(Object[] objects) {
            this.objects = objects;
            return this;
        }

        public Builder forService(Object service) {
            this.service = service;
            return this;
        }

        public ActiveMessage build() {
            return new ActiveMessage(this);
        }
    }
}
