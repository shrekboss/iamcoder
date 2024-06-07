package org.coder.design.patterns._3_patterns.creation.singleton.cases._1_resource_confic;

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

class OrderVo {
}