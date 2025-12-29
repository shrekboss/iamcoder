package org.coder.concurrency.programming.pattern._13_active_objects;

import org.coder.concurrency.programming.pattern._5_future.Future;
import org.coder.concurrency.programming.pattern._5_future.FutureService;

import java.util.concurrent.TimeUnit;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class OrderServiceImpl implements OrderService {

    @ActiveMethod
    @Override
    public Future<String> findOrderDetails(long orderId) {
        return FutureService.<Long, String>newService().submit(input -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                System.out.println("process the orderID->" + orderId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "The order Details Information";
        }, orderId, null);
    }

    @ActiveMethod
    @Override
    public void order(String account, long orderId) {
        try {
            TimeUnit.SECONDS.sleep(10);
            System.out.println("process the order for account " + account + ",orderId " + orderId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
