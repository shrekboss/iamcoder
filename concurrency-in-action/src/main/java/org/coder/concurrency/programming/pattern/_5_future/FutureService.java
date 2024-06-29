package org.coder.concurrency.programming.pattern._5_future;

/**
 * (what) 主要用于提交任务
 * <p>
 * (why)
 * <p>
 * (how)
 * 提交任务主要有两种：
 * 1. 不需要返回值；
 * 2. 需要获得最终的计算结果
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface FutureService<IN, OUT> {

    /**
     * 提交不需要返回值的任务，Future.get 方法返回的将是 null
     */
    Future<?> submit(Runnable runnable);

    /**
     * 提交需要返回值的任务，其中 Task 接口代替了 Runnable 接口
     */
    Future<OUT> submit(Task<IN, OUT> task, IN input, Callback<OUT> callback);

    /**
     *  使用静态方法创建一个 FutureService 的实例
     */
    static <T, R> FutureService<T, R> newService() {
        return new FutureServiceImpl<>();
    }
}
