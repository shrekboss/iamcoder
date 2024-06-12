package org.coder.design.patterns._4_design_patterns._1_creation._2_factory._2_factory_method;

import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.IRuleConfigParser;
import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.InvalidRuleConfigException;
import org.coder.design.patterns._4_design_patterns._1_creation._2_factory.simulate.RuleConfig;

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
public class RuleConfigSource {
    public RuleConfig load(String ruleConfigFilePath) {
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);

        IRuleConfigParserFactory parserFactory = RuleConfigParserFactoryMap.getParserFactory(ruleConfigFileExtension);

//        if ("json".equalsIgnoreCase(ruleConfigFileExtension)) {
//            parserFactory = new JsonRuleConfigParserFactory();
//        } else if ("xml".equalsIgnoreCase(ruleConfigFileExtension)) {
//            parserFactory = new XmlRuleConfigParserFactory();
//        } else if ("yaml".equalsIgnoreCase(ruleConfigFileExtension)) {
//            parserFactory = new YamlRuleConfigParserFactory();
//        } else if ("properties".equalsIgnoreCase(ruleConfigFileExtension)) {
//            parserFactory = new PropertiesRuleConfigParserFactory();
//        } else {
//            throw new InvalidRuleConfigException("Rule config file format is not supported: " + ruleConfigFilePath);
//        }

        if (parserFactory == null) {
            throw new InvalidRuleConfigException("Rule config file format is not supported: " + ruleConfigFilePath);
        }
        IRuleConfigParser parser = parserFactory.createParser();

        String configText = "";
        //从ruleConfigFilePath文件中读取配置文本到configText中
        return parser.parse(configText);
    }

    private String getFileExtension(String filePath) {
        //...解析文件名获取扩展名，比如rule.json，返回json
        return "json";
    }
}
