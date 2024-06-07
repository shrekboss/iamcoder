package org.coder.design.patterns._3_patterns.behavior._1_observer.cases._3_mock_eventbus;

import com.google.common.annotations.Beta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Subscribe 是一个注解，用于标明观察者中的哪个函数可以接收消息
 * <a href="https://github.com/google/guava">Google Guava EventBus 的源码</a>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Beta
public @interface Subscribe {}