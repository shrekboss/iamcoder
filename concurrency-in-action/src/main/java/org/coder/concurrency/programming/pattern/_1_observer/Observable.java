package org.coder.concurrency.programming.pattern._1_observer;

/**
 * Observable 被观察者接口定义，主要是暴露调用者使用的
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Observable {

    /**
     * 任务生命周期的枚举类型
     */
    enum Cycle {
        STARTED, RUNNING, DONE, ERROR
    }

    /**
     * 获取当前任务的生命周期状态
     */
    Cycle getCycle();

    /**
     * 设置当前任务的生命周期状态
     */
    void setCycle(Cycle cycle);

    /**
     * 定义启动线程的方法，主要作用是为了屏蔽 Thread 的其他方法
     */
    void start();

    /**
     * 定义线程的中断方法，主要作用是为了屏蔽 Thread 的其他方法
     */
    void interrupt();
}
