package org.coder.design.patterns._4_design_patterns._3_behavior._10_interpreter._cases;

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
public class AlertRuleInterpreter {

    private Expression expression;

    // key1 > 100 && key2 < 1000 || key3 == 200
    public AlertRuleInterpreter(String ruleExpression) {
        this.expression = new OrExpression(ruleExpression);
    }

    //<String, Long> apiStat = new HashMap<>();
    //apiStat.put("key1", 103);
    //apiStat.put("key2", 987);
    public boolean interpret(Map<String, Long> stats) {
        return expression.interpret(stats);
    }
}

