## 工厂模式

原始代码如下：

```java
public class RuleConfigSource {
    public RuleConfig load(String ruleConfigFilePath) {
        String ruleConfigFileExtension = getFileExtension(ruleConfigFilePath);
        IRuleConfigParser parser;
        if ("json".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new JsonRuleConfigParser();
        } else if ("xml".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new XmlRuleConfigParser();
        } else if ("yaml".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new YamlRuleConfigParser();
        } else if ("properties".equalsIgnoreCase(ruleConfigFileExtension)) {
            parser = new PropertiesRuleConfigParser();
        } else {
            throw new InvalidRuleConfigException("Rule config file format is not supported: " + ruleConfigFilePath);
        }

        String configText = "";
        //从ruleConfigFilePath文件中读取配置文本到configText中
        RuleConfig ruleConfig = parser.parse(configText);
        return ruleConfig;
    }

    private String getFileExtension(String filePath) {
        //...解析文件名获取扩展名，比如rule.json，返回json
        return "json";
    }
}
```

重构为：简单工厂，参考代码为：

- [第一种实现方法](_1_simple_factory%2FRuleConfigParserFactory.java)
- [第二种实现方式](_1_simple_factory%2FRuleConfigParserFactory2.java)
- [RuleConfigSource.java](_1_simple_factory%2FRuleConfigSource.java)

尽管简单工厂模式的代码实现中，有多处 if 分支判断逻辑，违背开闭原则，但权衡扩展性和可读性，这样的代码实现在大多数情况下（比如，不需要频繁地添加
parser，也没有太多的 parser）是没有问题的。

重构为：工厂方法，参考代码为：

- [IRuleConfigParserFactory.java](_2_factory_method%2FIRuleConfigParserFactory.java)
- [JsonRuleConfigParserFactory.java](_2_factory_method%2FJsonRuleConfigParserFactory.java)
- [PropertiesRuleConfigParserFactory.java](_2_factory_method%2FPropertiesRuleConfigParserFactory.java)
- [XmlRuleConfigParserFactory.java](_2_factory_method%2FXmlRuleConfigParserFactory.java)
- [YamlRuleConfigParserFactory.java](_2_factory_method%2FYamlRuleConfigParserFactory.java)
- [RuleConfigParserFactoryMap.java](_2_factory_method%2FRuleConfigParserFactoryMap.java)
- [RuleConfigSource.java](_2_factory_method%2FRuleConfigSource.java)

重构为：抽象工厂，参考代码为：

- [IConfigParserFactory.java](_3_abs_factory%2FIConfigParserFactory.java)
- [IRuleConfigParser.java](_3_abs_factory%2FIRuleConfigParser.java)
- [ISystemConfigParser.java](_3_abs_factory%2FISystemConfigParser.java)
- [JsonConfigParserFactory.java](_3_abs_factory%2FJsonConfigParserFactory.java)
- [PropertiesConfigParserFactory.java](_3_abs_factory%2FPropertiesConfigParserFactory.java)
- [XmlConfigParserFactory.java](_3_abs_factory%2FXmlConfigParserFactory.java)
- [YamlConfigParserFactory.java](_3_abs_factory%2FYamlConfigParserFactory.java)

案例分析：

- [工厂模式：如何设计实现一个Dependency Injection框架？](cases%2F_readme.md)