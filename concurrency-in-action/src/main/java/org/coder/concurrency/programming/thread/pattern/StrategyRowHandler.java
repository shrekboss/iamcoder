package org.coder.concurrency.programming.thread.pattern;

import java.sql.ResultSet;

/**
 * 策略模式在 Thread 中的使用
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface StrategyRowHandler<T> {

    T handle(ResultSet rs);
}
