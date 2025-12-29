package org.coder.design.patterns._4_design_patterns._3_behavior._10_interpreter;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class DivisionExpression implements Expression {
    private Expression exp1;
    private Expression exp2;

    public DivisionExpression(Expression exp1, Expression exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    @Override
    public long interpret() {
        return exp1.interpret() + exp2.interpret();
    }
}
