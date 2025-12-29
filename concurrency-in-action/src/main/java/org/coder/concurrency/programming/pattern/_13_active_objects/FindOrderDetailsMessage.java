package org.coder.concurrency.programming.pattern._13_active_objects;

import org.coder.concurrency.programming.pattern._5_future.Future;

import java.util.Map;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Deprecated
public class FindOrderDetailsMessage extends MethodMessage {
    public FindOrderDetailsMessage(Map<String, Object> params
            , OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {
        Future<String> realFuture = orderService.findOrderDetails((Long) params.get("orderId"));
        ActiveFuture<String> activeFuture = (ActiveFuture<String>) params.get("activeFuture");
        try {
            String result = realFuture.get();
            activeFuture.finish(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
            activeFuture.finish(null);
        }
    }
}
