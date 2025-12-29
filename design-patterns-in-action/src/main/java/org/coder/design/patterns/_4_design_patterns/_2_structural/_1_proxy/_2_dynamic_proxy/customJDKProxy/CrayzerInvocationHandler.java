package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.customJDKProxy;

import java.lang.reflect.Method;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface CrayzerInvocationHandler {
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
