package org.coder.design.patterns._4_design_patterns._3_behavior._10_interpreter;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Expression {
    long interpret();
}

// SubtractionExpression/MultiplicationExpression/DivisionExpression与AdditionExpression代码结构类似，这里就省略了

