package org.coder.design.patterns._4_design_patterns._3_behavior._2_template._callback;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class AClass {
    public static void main(String[] args) {
        BClass b = new BClass();
        //回调对象
        b.process(new ICallback() {
            @Override
            public void methodToCallback() {
                System.out.println("Call back me.");
            }
        });
    }
}
