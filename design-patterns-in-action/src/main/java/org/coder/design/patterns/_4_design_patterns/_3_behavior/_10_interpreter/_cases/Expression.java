package org.coder.design.patterns._4_design_patterns._3_behavior._10_interpreter._cases;

import java.util.Map;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Expression {

    boolean interpret(Map<String, Long> stats);
}
