package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy._cglib;

import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.IUserDao;
import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.UserDaoImpl;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class CglibProxyApp {
    public static void main(String[] args) {
        UserDaoImpl target = new UserDaoImpl();
        IUserDao proxy = (IUserDao) new UserDaoImplWithCglibProxy(target).getProxyInstance();
        System.out.println(proxy);

        proxy.save();
        proxy.edit();
        proxy.delete();
    }
}
