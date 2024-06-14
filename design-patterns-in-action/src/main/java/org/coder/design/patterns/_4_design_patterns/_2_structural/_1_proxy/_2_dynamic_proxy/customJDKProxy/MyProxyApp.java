package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.customJDKProxy;

import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.IUserDao;
import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.UserDaoImpl;

import java.io.IOException;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class MyProxyApp {
    public static void main(String[] args) throws IOException {
        UserDaoImpl userDaoImpl = new UserDaoImpl();
        IUserDao proxy = (IUserDao) new UserDaoImplWithCrayzerProxy(userDaoImpl).getProxyInstance();
        System.out.println(proxy);

        proxy.save();

        proxy.edit();

    }
}
