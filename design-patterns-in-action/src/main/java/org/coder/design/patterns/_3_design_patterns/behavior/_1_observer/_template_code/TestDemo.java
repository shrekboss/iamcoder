package org.coder.design.patterns._3_design_patterns.behavior._1_observer._template_code;

/**
 * 常见观察者模式实现的测试入口
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TestDemo {

    public static void main(String[] args) {
        ConcreteSubjectImpl subject = new ConcreteSubjectImpl();
        subject.register(new ConcreteObserverImplOne());
        subject.register(new ConcreteObserverImplTwo());
        subject.notifyObservers(new Message());
    }
}
