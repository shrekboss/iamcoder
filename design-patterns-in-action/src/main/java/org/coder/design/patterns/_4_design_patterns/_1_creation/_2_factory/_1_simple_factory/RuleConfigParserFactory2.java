package org.coder.design.patterns._4_design_patterns._1_creation._2_factory._1_simple_factory;

import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RuleConfigParserFactory2 {

    public static final Map<String, IRuleConfigParser> cachedParsers = new HashMap<>();

    static {
        cachedParsers.put("json", new JsonRuleConfigParser());
        cachedParsers.put("xml", new XmlRuleConfigParser());
        cachedParsers.put("yaml", new YamlRuleConfigParser());
        cachedParsers.put("properties", new PropertiesRuleConfigParser());
    }

    public static IRuleConfigParser createParser(String configFormat) {
        if (configFormat == null || configFormat.isEmpty()) {
            return null;
            //返回 null 还是 IllegalArgumentException 全凭你自己说了算
        }
        return cachedParsers.get(configFormat.toLowerCase());
    }
}
