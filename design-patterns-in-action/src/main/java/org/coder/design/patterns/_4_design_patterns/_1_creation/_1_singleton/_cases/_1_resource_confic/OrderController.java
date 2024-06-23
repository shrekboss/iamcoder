package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._1_resource_confic;

import org.coder.design.patterns.common.vo.OrderVo;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class OrderController {
    public void create(OrderVo order) {

        // ...省略业务逻辑代码...
        Logger.getInstance().log("Created a order: " + order.toString());
    }
}

