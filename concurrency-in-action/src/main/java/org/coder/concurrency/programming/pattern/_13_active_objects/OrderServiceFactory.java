package org.coder.concurrency.programming.pattern._13_active_objects;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Deprecated
public final class OrderServiceFactory {

    // 将 ActiveMessageQueue 定义成 static 的目的是，保持其在整个 JVM 进程中是唯一的，并且 ActiveDaemonThread 会在此刻启动
    private final static ActiveMessageQueue activeMessageQueue = new ActiveMessageQueue();

    /**
     * 不允许外部通过 new 的方式构建
     */
    private OrderServiceFactory() {
    }

    public static OrderService toActiveObject(OrderService orderService) {
//        return new OrderServiceProxy(orderService, activeMessageQueue);
        return null;
    }
}
