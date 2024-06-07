package org.coder.design.patterns._3_patterns.behavior._1_observer._template_code;

/**
 * 真正的观察者实现1
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ConcreteObserverImplOne implements Observer {

    @Override
    public void update(Message message) {
        // 获取消息通知，执行自己的逻辑...
        System.out.println("ConcreteObserverImplOne is notified.");
    }
}
