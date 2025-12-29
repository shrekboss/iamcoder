package org.coder.design.patterns._4_design_patterns._1_creation._2_factory._3_abs_factory;

import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.IRuleConfigParser;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface IConfigParserFactory {

    IRuleConfigParser createRuleParser();

    ISystemConfigParser createSystemParser(); //此处可以扩展新的parser类型，比如IBizConfigParser
}
