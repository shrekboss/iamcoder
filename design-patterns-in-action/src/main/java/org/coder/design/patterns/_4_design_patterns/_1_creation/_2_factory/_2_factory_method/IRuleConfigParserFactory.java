package org.coder.design.patterns._4_design_patterns._1_creation._2_factory._2_factory_method;

import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.IRuleConfigParser;

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
public interface IRuleConfigParserFactory {

    IRuleConfigParser createParser();
}
