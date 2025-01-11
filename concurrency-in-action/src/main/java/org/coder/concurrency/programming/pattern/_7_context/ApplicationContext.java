package org.coder.concurrency.programming.pattern._7_context;

import org.coder.concurrency.programming.pattern._7_context.simulate.ApplicationConfiguration;
import org.coder.concurrency.programming.pattern._7_context.simulate.RuntimeInfo;

import java.util.concurrent.ConcurrentHashMap;

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
public class ApplicationContext {

    private ApplicationConfiguration configuration;

    private RuntimeInfo runtimeInfo;

    private ConcurrentHashMap<Thread, ActionContext> contexts = new ConcurrentHashMap<>();

    /**
     * 需要从头到尾地传递 context，可以用线程的上下文设计来解决这样的问题。
     * 这样可以保证线程之间上下文的独立性，同时也不用考虑 ActionContext 的线程安全性(因为始终只有一个线程访问 ActionContext)，
     * 因此线程上下文又被称为“线程级别的单例”。
     * <p>
     * 通过这种方式定义线程上下文很可能会导致内存泄露， contexts 是一个 Map 的数据结构，用当前线程做 key，当线程的生命周期结束后，contexts
     * 中的 Thread 实例不会被释放，与之对应的 Value 也不会被释放，时间长了就会导致内存泄露(Memory Leak)，当然可以通过 soft reference 或者
     * weak reference等引用类型，JVM 会主动尝试回收。
     */
    public ActionContext getActionContext() {
//        ActionContext actionContext = contexts.get(Thread.currentThread());
//        if (actionContext == null) {
//            actionContext = new ActionContext();
//            contexts.put(Thread.currentThread(), actionContext);
//        }

//        return contexts;
        return contexts.computeIfAbsent(Thread.currentThread(), k -> new ActionContext());
    }

    private static class Holder {
        private static ApplicationContext instance = new ApplicationContext();
    }

    public static ApplicationContext getContext() {
        return Holder.instance;
    }

    public void setConfiguration(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    public ApplicationConfiguration getConfiguration() {
        return this.configuration;
    }

    public void setRuntimeInfo(RuntimeInfo runtimeInfo) {
        this.runtimeInfo = runtimeInfo;
    }

    public RuntimeInfo getRuntimeInfo() {
        return this.runtimeInfo;
    }
}
