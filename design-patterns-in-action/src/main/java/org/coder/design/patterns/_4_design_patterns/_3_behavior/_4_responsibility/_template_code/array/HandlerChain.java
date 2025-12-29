package org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._template_code.array;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class HandlerChain {
    private final List<IHandler> handlers = new ArrayList<>();

    public void addHandler(IHandler handler) {
        this.handlers.add(handler);
    }

    public void handle() {
        for (IHandler handler : handlers) {
            boolean handled = handler.handle();
            if (handled) {
                break;
            }
        }
    }
}

// 使用举例
