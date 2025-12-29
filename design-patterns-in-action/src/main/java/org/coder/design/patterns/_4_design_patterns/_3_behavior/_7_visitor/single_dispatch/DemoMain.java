package org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor.single_dispatch;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class DemoMain {

    public static void main(String[] args) {
        SingleDispatchClass demo = new SingleDispatchClass();
        ParentClass p = new ChildClass();
        // 执行哪个对象的方法，由对象的实际类型决定
        demo.polymorphismFunction(p);
        // 执行对象的哪个方法，由参数对象的声明类型决定
        demo.overloadFunction(p);
    }
}
