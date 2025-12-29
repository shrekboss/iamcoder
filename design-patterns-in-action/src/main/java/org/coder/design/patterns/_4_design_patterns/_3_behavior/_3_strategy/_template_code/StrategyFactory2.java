package org.coder.design.patterns._4_design_patterns._3_behavior._3_strategy._template_code;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class StrategyFactory2 {
    public static Strategy getStrategy(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("type should not be empty.");
        }

        if (type.equals("A")) {
            return new ConcreteStrategyA();
        } else if (type.equals("B")) {
            return new ConcreteStrategyB();
        }

        return null;
    }
}
