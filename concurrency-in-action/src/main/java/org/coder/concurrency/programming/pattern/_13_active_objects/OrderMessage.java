package org.coder.concurrency.programming.pattern._13_active_objects;

import java.util.Map;

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
@Deprecated
public class OrderMessage extends MethodMessage {

    public OrderMessage(Map<String, Object> params,
                        OrderService orderService) {
        super(params, orderService);
    }

    @Override
    public void execute() {
        String account = (String) params.get("account");
        long orderId = (long) params.get("orderId");
        orderService.order(account, orderId);
    }
}
