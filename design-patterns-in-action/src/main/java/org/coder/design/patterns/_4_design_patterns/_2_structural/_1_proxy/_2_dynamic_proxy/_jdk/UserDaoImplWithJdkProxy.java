package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy._jdk;

import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.IUserDao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserDaoImplWithJdkProxy implements InvocationHandler {
    private IUserDao target;

    public UserDaoImplWithJdkProxy(IUserDao target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{IUserDao.class},
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        startTransaction();

        Object returnValue = method.invoke(target, args);

        commitTransaction();

        return returnValue;
    }

    private void startTransaction() {
        System.out.println("开始事务...");
    }

    private void commitTransaction() {
        System.out.println("提交事务...");
    }
}
