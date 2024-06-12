package org.coder.design.patterns._4_design_patterns.creation._2_factory._2_factory_method;

import org.coder.design.patterns._4_design_patterns.creation._2_factory.simulate.IRuleConfigParser;
import org.coder.design.patterns._4_design_patterns.creation._2_factory.simulate.JsonRuleConfigParser;

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
public class JsonRuleConfigParserFactory implements IRuleConfigParserFactory {
    @Override
    public IRuleConfigParser createParser() {
        return new JsonRuleConfigParser();
    }
}
