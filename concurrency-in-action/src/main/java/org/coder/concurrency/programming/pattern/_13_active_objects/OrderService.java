package org.coder.concurrency.programming.pattern._13_active_objects;

import org.coder.concurrency.programming.pattern._5_future.Future;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface OrderService {

    /**
     * 根据订单编码查询订单明细，有入参也有返回值，但是返回类型必须是 Future
     */
    Future<String> findOrderDetails(long orderId);

    /**
     * 提交订单，没有返回值
     */
    void order(String account, long orderId);
}
