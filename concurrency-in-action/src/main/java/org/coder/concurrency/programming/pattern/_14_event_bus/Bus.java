package org.coder.concurrency.programming.pattern._14_event_bus;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Bus {

    //将某个对象注册到Bus上，从此之后该类就称为Subscriber了
    void register(Object subscriber);

    //将某个对象从Bus上取消注册，取消注册之后就不会在接收到来自Bus的任何消息
    void unregister(Object subscriber);

    //提交Event到默认的topic
    void post(Object event);

    //提交Event到指定的topic
    void post(Object Event, String topic);

    //关闭该Bus
    void close();

    //返回Bus的名称标识
    String getBusName();
}
