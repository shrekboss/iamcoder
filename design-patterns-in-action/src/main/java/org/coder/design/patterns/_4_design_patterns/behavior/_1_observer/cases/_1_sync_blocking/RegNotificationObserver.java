package org.coder.design.patterns._4_design_patterns.behavior._1_observer.cases._1_sync_blocking;

/**
 * 促销观察者
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RegNotificationObserver implements RegObserver {

    // 模拟依赖注入
    private NotificationService notificationService = new NotificationService();

    @Override
    public void handleRegSuccess(Long userId) {
        notificationService.sendInboxMessage(userId, "Welcome...");
    }
}
