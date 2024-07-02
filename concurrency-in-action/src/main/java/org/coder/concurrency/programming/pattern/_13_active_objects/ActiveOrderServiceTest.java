package org.coder.concurrency.programming.pattern._13_active_objects;

import org.coder.concurrency.programming.pattern._5_future.Future;

import static org.coder.concurrency.programming.pattern._13_active_objects.ActiveServiceFactory.active;

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
public class ActiveOrderServiceTest {

    public static void main(String[] args) throws InterruptedException {
//        OrderService orderService = OrderServiceFactory.toActiveObject(new OrderServiceImpl());
//        orderService.order("hello", 453453);
//        System.out.println("Return immediately");
//        currentThread().join();

        OrderService orderService = active(new OrderServiceImpl());
        Future<String> future = orderService.findOrderDetails(23423);
        System.out.println("i will be returned immediately");
        System.out.println(future.get());
    }
}
