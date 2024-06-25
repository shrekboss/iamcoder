package org.coder.design.patterns._4_design_patterns._2_structural._1_proxy._1_static_proxy;

import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.v3.MetricsCollector;
import org.coder.design.patterns.simulate.IUserController;
import org.coder.design.patterns.simulate.controller.UserController;
import org.coder.design.patterns.simulate.vo.RequestInfo;
import org.coder.design.patterns.simulate.vo.UserVo;

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
public class UserControllerProxy2 extends UserController {

    private MetricsCollector metricsCollector;

    public UserControllerProxy2() {
        this.metricsCollector = new MetricsCollector();
    }

    public static void main(String[] args) {

        // UserControllerProxy 使用举例
        UserController targetObj = new UserController();
        IUserController userController = new UserControllerProxy(targetObj);
    }

    public UserVo login(String telephone, String password) {
        long startTimestamp = System.currentTimeMillis();

        UserVo userVo = super.login(telephone, password);

        long endTimeStamp = System.currentTimeMillis();
        long responseTime = endTimeStamp - startTimestamp;
        RequestInfo requestInfo = new RequestInfo("login", responseTime, startTimestamp);
        metricsCollector.recordRequest(requestInfo);

        return userVo;
    }

    public UserVo register(String telephone, String password) {
        long startTimestamp = System.currentTimeMillis();

        UserVo userVo = super.register(telephone, password);

        long endTimeStamp = System.currentTimeMillis();
        long responseTime = endTimeStamp - startTimestamp;
        RequestInfo requestInfo = new RequestInfo("register", responseTime, startTimestamp);
        metricsCollector.recordRequest(requestInfo);

        return userVo;
    }
}

