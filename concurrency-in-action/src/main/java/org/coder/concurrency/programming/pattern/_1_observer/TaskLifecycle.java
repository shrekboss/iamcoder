package org.coder.concurrency.programming.pattern._1_observer;

/**
 * TaskLifecycle 接口定义：任务执行的生命周期中会触发的接口，其中 EmptyLifecycle 是一个空实现，主要是为了让使用者
 * 保持对 Thread 类的使用习惯
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface TaskLifecycle<T> {

    /**
     * 任务启动时会触发 onStart 方法
     */
    void onStart(Thread thread);

    /**
     * 任务正在运行时会触发 onRunning 方法
     */
    void onRunning(Thread thread);

    /**
     * 任务运行结束时会触发 onFinish 方法，其中 result 是任务执行结束后的结果
     */
    void onFinish(Thread thread, T result);

    /**
     * 任务执行报错时会触发 onError 方法
     */
    void onError(Thread thread, Exception e);

    /**
     * 生命周期接口的空实现(Adaptor)
     * @param <T>
     */
    class EmptyLifecycle<T> implements TaskLifecycle<T> {

        @Override
        public void onStart(Thread thread) {
            //do nothing
        }

        @Override
        public void onRunning(Thread thread) {
            //do nothing
        }

        @Override
        public void onFinish(Thread thread, T result) {
            //do nothing
        }

        @Override
        public void onError(Thread thread, Exception e) {
            //do nothing
        }
    }
}
