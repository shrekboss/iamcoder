package org.coder.design.patterns._4_design_patterns.creation._2_factory._3_abs_factory;

import org.coder.design.patterns._4_design_patterns.creation._2_factory.simulate.IRuleConfigParser;
import org.coder.design.patterns._4_design_patterns.creation._2_factory.simulate.JsonRuleConfigParser;
import org.coder.design.patterns._4_design_patterns.creation._2_factory.simulate.JsonRuleSystemConfigParser;

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
public class JsonConfigParserFactory implements IConfigParserFactory {
    @Override
    public IRuleConfigParser createRuleParser() {
        return new JsonRuleConfigParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new JsonRuleSystemConfigParser();
    }
}
