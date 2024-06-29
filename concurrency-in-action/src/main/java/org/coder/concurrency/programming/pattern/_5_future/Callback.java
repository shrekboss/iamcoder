package org.coder.concurrency.programming.pattern._5_future;

/**
 * (what) Callback 接口很简单，类似 JDK 8 中的 Consumer 函数式接口
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface Callback<T> {

    void call(T t);
}
