package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._2_dynamic_proxy;

import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v3.MetricsCollector;
import org.coder.design.patterns.common.IUserController;
import org.coder.design.patterns.common.controller.UserController;
import org.coder.design.patterns.common.vo.RequestInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
public class MetricsCollectorProxy {

    private MetricsCollector metricsCollector;

    public MetricsCollectorProxy() {
        this.metricsCollector = new MetricsCollector();
    }

    public static void main(String[] args) {
        MetricsCollectorProxy proxy = new MetricsCollectorProxy();
        IUserController userController = (IUserController) proxy.createProxy(new UserController());

        userController.login("18510500000", "test");
    }

    public Object createProxy(Object proxyedObject) {

        Class<?>[] interfaces = proxyedObject.getClass().getInterfaces();
        DynamicProxyHandler dynamicProxyHandler = new DynamicProxyHandler(proxyedObject);
        return Proxy.newProxyInstance(proxyedObject.getClass().getClassLoader(), interfaces, dynamicProxyHandler);
    }

    private class DynamicProxyHandler implements InvocationHandler {

        private Object proxyObject;

        public DynamicProxyHandler(Object proxyedObject) {
            this.proxyObject = proxyedObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long startTimestamp = System.currentTimeMillis();
            Object result = method.invoke(proxyObject, args);
            long endTimestamp = System.currentTimeMillis();
            long responseTime = endTimestamp - startTimestamp;
            String apiName = proxyObject.getClass().getName() + ":" + method.getName();

            RequestInfo requestInfo = new RequestInfo(apiName, responseTime, startTimestamp);
            metricsCollector.recordRequest(requestInfo);

            return result;
        }
    }
}
