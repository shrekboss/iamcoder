package org.coder.design.patterns._3_design_patterns.behavior._1_observer._template_code;

/**
 * 被观察者(Observable)接口定义
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Subject {

    // attach
    void register(Observer observer);

    // detach
    void remove(Observer observer);

    void notifyObservers(Message message);
}
