package org.coder.design.patterns._3_patterns.behavior._1_observer._template_code;

import java.util.ArrayList;
import java.util.List;

/**
 * 真正的被观察者
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ConcreteSubjectImpl implements Subject {

    private List<Observer> observers = new ArrayList<>();

    @Override
    public void register(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void remove(Observer observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(Message message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
