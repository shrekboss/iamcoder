package org.coder.design.patterns._3_patterns.behavior._1_observer.cases._2_eventbus;

import com.google.common.eventbus.Subscribe;
import org.coder.design.patterns._3_patterns.behavior._1_observer.cases._1_sync_blocking.NotificationService;
import org.coder.design.patterns._3_patterns.behavior._1_observer.cases._1_sync_blocking.RegObserver;

/**
 * 促销观察者
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RegNotificationObserver implements RegObserver {

    // 模拟依赖注入
    private NotificationService notificationService = new NotificationService();

    // // 更新点 5：+ @Subscribe
    @Subscribe
    @Override
    public void handleRegSuccess(Long userId) {
        notificationService.sendInboxMessage(userId, "Welcome...");
    }
}