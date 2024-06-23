package org.coder.design.patterns.common.controller;

import org.coder.design.patterns._2_design_principle._cases.generic_framework_design.prototype.Metrics;
import org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._1_resource_confic.Logger;
import org.coder.design.patterns.common.IUserController;
import org.coder.design.patterns.common.vo.UserVo;

import java.util.concurrent.TimeUnit;

/**
 * (what)
 * 应用场景：统计下面两个接口(注册和登录）的响应时间和访问次数
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserController implements IUserController {
    private Metrics metrics = new Metrics();

    public UserController() {
        metrics.startRepeatedReport(60, TimeUnit.SECONDS);
    }

    @Override
    public void register(UserVo user) {
        long startTimestamp = System.currentTimeMillis();
        metrics.recordTimestamp("register", startTimestamp);
        //...
        long respTime = System.currentTimeMillis() - startTimestamp;
        metrics.recordResponseTime("register", respTime);
    }

    @Override
    public UserVo login(String telephone, String password) {
        long startTimestamp = System.currentTimeMillis();
        metrics.recordTimestamp("login", startTimestamp);
        //...
        long respTime = System.currentTimeMillis() - startTimestamp;
        metrics.recordResponseTime("login", respTime);

        Logger.getInstance().log(telephone + " logined!");

        return new UserVo(telephone, password);
    }

    @Override
    public UserVo register(String telephone, String password) {
        //...省略register逻辑...
        //...返回UserVo数据...
        return new UserVo(telephone, password);
    }
}