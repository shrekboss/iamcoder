package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._1_static_proxy._static;

import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.UserDaoImpl;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class StaticProxyApp {
    public static void main(String[] args) {
        UserDaoImpl userDao = new UserDaoImpl();
        UserDaoImplProxy proxy = new UserDaoImplProxy(userDao);

        proxy.save();
    }
}
