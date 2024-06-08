package org.coder.design.patterns._3_design_patterns.creation._1_singleton._template_code.register;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RegisterSingletonChildA extends RegisterSingleton {

    public static RegisterSingletonChildA getInstance() {
        return (RegisterSingletonChildA) RegisterSingletonChildA
                .getInstance("org.coder.design.patterns._3_design_patterns.creation._1_singleton._template_code.register.RegisterSingletonChildA");
    }

    //随便写一个测试的方法
    public String about() {
        return "----> 我是 RegisterSingleton的一个子类 RegisterSingletonChildA";
    }
}
