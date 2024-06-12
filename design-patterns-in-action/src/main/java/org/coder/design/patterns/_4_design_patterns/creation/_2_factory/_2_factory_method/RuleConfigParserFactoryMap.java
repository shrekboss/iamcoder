package org.coder.design.patterns._4_design_patterns.creation._2_factory._2_factory_method;

import java.util.HashMap;
import java.util.Map;

/**
 * (what)
 * 工厂的工厂
 * <p>
 * (why)
 * 因为工厂类只包含方法，不包含成员变量，完全可以复用，不需要每次都创建新的工厂类对象，所以，简单工厂模式的第二种实现思路更加合适。
 * <p>
 * (how)
 * 工厂类再创建一个简单工厂，也就是工厂的工厂，用来创建工厂类对象
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RuleConfigParserFactoryMap {

    private static final Map<String, IRuleConfigParserFactory> cachedFactories = new HashMap<>();

    static {
        cachedFactories.put("json", new JsonRuleConfigParserFactory());
        cachedFactories.put("xml", new XmlRuleConfigParserFactory());
        cachedFactories.put("yaml", new YamlRuleConfigParserFactory());
        cachedFactories.put("properties", new PropertiesRuleConfigParserFactory());
    }

    public static IRuleConfigParserFactory getParserFactory(String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return cachedFactories.get(type.toLowerCase());
    }
}
