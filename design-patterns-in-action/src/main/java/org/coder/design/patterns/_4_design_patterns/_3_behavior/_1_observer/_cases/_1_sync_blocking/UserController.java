package org.coder.design.patterns._4_design_patterns._3_behavior._1_observer._cases._1_sync_blocking;

import org.coder.design.patterns.simulate.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserController {

    // 模拟依赖注入
    private UserService userService = new UserService();

    private List<RegObserver> regObservers = new ArrayList<>();

    // 一次性设置好，之后也不可能动态的修改
//    public void setRegObservers(List<RegObserver> regObservers) {
//        regObservers.addAll(regObservers);
//    }
    private void setRegObservers(RegObserver regObserver) {
        regObservers.add(regObserver);
    }

    public Long register(String telephone, String password) {
        // 省略输入参数的校验代码
        // 省略userService.register()异常的try-catch代码
        long userId = userService.register(telephone, password);

        setRegObservers(new RegPromotionObserver());
        setRegObservers(new RegNotificationObserver());

        for (RegObserver regObserver : regObservers) {
            regObserver.handleRegSuccess(userId);
        }

        return userId;
    }
}
