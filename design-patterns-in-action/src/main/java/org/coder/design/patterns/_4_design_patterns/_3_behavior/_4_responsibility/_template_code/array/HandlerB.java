package org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._template_code.array;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class HandlerB implements IHandler {

    @Override
    public boolean handle() {
        boolean handled = false;

        //...
        System.out.println("HandlerB#doHandle");

        return handled;
    }
}
