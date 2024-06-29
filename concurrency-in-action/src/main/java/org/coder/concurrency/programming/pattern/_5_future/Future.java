package org.coder.concurrency.programming.pattern._5_future;

/**
 * (what) 获取计算结果和判断任务是否完成的两个接口
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Future<T> {

    /**
     * 返回计算结后的结果，该方法会陷入阻塞状态
     */
    T get() throws InterruptedException;

    /**
     * 判断任务是否已经被执行完成
     */
    boolean done();
}
