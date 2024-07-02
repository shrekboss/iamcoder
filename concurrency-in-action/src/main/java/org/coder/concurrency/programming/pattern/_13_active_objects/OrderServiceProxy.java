package org.coder.concurrency.programming.pattern._13_active_objects;

/**
 * (what) 主要作用是将 {@link org.coder.concurrency.programming.pattern._13_active_objects.OrderService} 接口定义的方法封装成 MethodMessage，然后 offer 给 ActiveMessageQueue
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
//@Deprecated
//public class OrderServiceProxy implements OrderService{
//
//    private final OrderService orderService;
//    private final ActiveMessageQueue activeMessageQueue;
//
//    public OrderServiceProxy(OrderService orderService, ActiveMessageQueue activeMessageQueue) {
//        this.orderService = orderService;
//        this.activeMessageQueue = activeMessageQueue;
//    }
//
//    @Override
//    public Future<String> findOrderDetails(long orderId) {
//        final ActiveFuture<String> activeFuture = new ActiveFuture<>();
//        Map<String, Object> params = new HashMap<>();
//        params.put("orderId", orderId);
//        params.put("activeFuture", activeFuture);
//        MethodMessage message = new FindOrderDetailsMessage(params, orderService);
//        activeMessageQueue.offer(message);
//        return activeFuture;
//    }
//
//    @Override
//    public void order(String account, long orderId) {
//        Map<String, Object> params = new HashMap<>();
//        params.put("account", account);
//        params.put("orderId", orderId);
//        MethodMessage message = new OrderMessage(params, orderService);
//        activeMessageQueue.offer(message);
//    }
//}
