package org.coder.design.patterns._4_design_patterns._3_behavior._2_template._callback;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class BClass {
    public void process(ICallback callback) {
        //...
        callback.methodToCallback();
        //...
    }
}
