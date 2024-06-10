package org.coder.design.patterns._4_design_patterns.creation._1_singleton._template_code.register;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RegisterSingletonChildB extends RegisterSingleton {

    public static RegisterSingletonChildB getInstance() {
        return (RegisterSingletonChildB) RegisterSingletonChildB
                .getInstance("org.coder.design.patterns._4_design_patterns.creation._1_singleton._template_code.register.RegisterSingletonChildB");
    }

    //随便写一个测试的方法
    public String about() {
        return "---->我是 RegisterSingleton 的一个子类 RegisterSingletonChildB";
    }
}
