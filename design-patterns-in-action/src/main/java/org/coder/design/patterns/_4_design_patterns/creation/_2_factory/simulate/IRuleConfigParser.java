package org.coder.design.patterns._4_design_patterns.creation._2_factory.simulate;

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
public interface IRuleConfigParser {

    RuleConfig parse(String configText);
}