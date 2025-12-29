package org.coder.design.patterns._4_design_patterns._1_creation._2_factory._3_abs_factory;

import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.IRuleConfigParser;
import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.JsonRuleConfigParser;
import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.PropertiesRuleSystemConfigParser;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class PropertiesConfigParserFactory implements IConfigParserFactory {
    @Override
    public IRuleConfigParser createRuleParser() {
        return new JsonRuleConfigParser();
    }

    @Override
    public ISystemConfigParser createSystemParser() {
        return new PropertiesRuleSystemConfigParser();
    }
}
