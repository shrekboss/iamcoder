package org.coder.design.patterns._4_design_patterns._3_behavior._1_observer.cases._2_eventbus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.coder.design.patterns._4_design_patterns._3_behavior._1_observer.cases._1_sync_blocking.RegObserver;
import org.coder.design.patterns._4_design_patterns._3_behavior._1_observer.cases._1_sync_blocking.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class UserController {

    // 模拟依赖注入
    private UserService userService = new UserService();

    private List<RegObserver> regObservers = new ArrayList<>();

    // 更新点 1：
    private EventBus eventBus;
    private static final int DEFAULT_EVENTBUS_THREAD_POOL_SIZE = 20;

    // 更新点 2：
    public UserController() {
        // 同步阻塞模式
//        this.eventBus = new EventBus();
        // 异步非阻塞
        this.eventBus = new AsyncEventBus(Executors.newFixedThreadPool(DEFAULT_EVENTBUS_THREAD_POOL_SIZE));
    }

    // 更新点 3：
    public void setRegObservers(List<RegObserver> regObservers) {
//        regObservers.addAll(regObservers);
        for (RegObserver regObserver : regObservers) {
            eventBus.register(regObserver);
        }
    }

    public Long register(String telephone, String password) {
        // 省略输入参数的校验代码
        // 省略userService.register()异常的try-catch代码
        long userId = userService.register(telephone, password);

        // 更新点 4：
//        for (RegObserver regObserver : regObservers) {
//            regObserver.handleRegSuccess(userId);
//        }
        eventBus.post(userId);

        return userId;
    }
}
