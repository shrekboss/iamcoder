package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.customJDKProxy;

import org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy.simulate.IUserDao;

import java.lang.reflect.Method;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserDaoImplWithCrayzerProxy implements CrayzerInvocationHandler {
    private IUserDao target;

    public UserDaoImplWithCrayzerProxy(IUserDao target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        return MyProxy.newProxyInstance(
                new MyClassLoader(),
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
