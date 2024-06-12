package org.coder.design.patterns._4_design_patterns._2_behavior._1_observer.cases._3_mock_eventbus;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ObserverAction 类用来表示 @Subscribe 注解的方法
 *
 * 主要用在 ObserverRegistry 观察者注册表中
 * <a href="https://github.com/google/guava">Google Guava EventBus 的源码</a>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ObserverAction {

    /**
     * target 表示观察者类
     */
    private Object target;

    /**
     * method 表示方法
     */
    private Method method;

    public ObserverAction(Object target, Method method) {
        this.target = Preconditions.checkNotNull(target);
        this.method = method;
        this.method.setAccessible(true);
    }

    /**
     *
     * @param event method 方法的参数
     */
    public void execute(Object event) {
        try {
            method.invoke(target, method);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
