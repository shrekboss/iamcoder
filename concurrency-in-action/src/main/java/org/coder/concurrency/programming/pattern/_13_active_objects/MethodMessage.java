package org.coder.concurrency.programming.pattern._13_active_objects;

import java.util.Map;

/**
 * (what) 主要作用是手机收集每一个接口的方法参数，并且提供 execute 方法供 ActiveDaemonThread 直接调用，该对象就是典型的 Worker Thread
 * 模型中的 Product，execute 方法则是加工该产品的说明书。
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Deprecated
public abstract class MethodMessage {

    protected final Map<String, Object> params;

    protected final OrderService orderService;

    public MethodMessage(Map<String, Object> params, OrderService orderService) {
        this.params = params;
        this.orderService = orderService;
    }

    public abstract void execute();
}
