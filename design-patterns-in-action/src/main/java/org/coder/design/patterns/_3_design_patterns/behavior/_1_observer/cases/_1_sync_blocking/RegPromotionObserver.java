package org.coder.design.patterns._3_design_patterns.behavior._1_observer.cases._1_sync_blocking;

/**
 * 促销观察者
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RegPromotionObserver implements RegObserver {

    // 模拟依赖注入
    private PromotionService promotionService = new PromotionService();

    @Override
    public void handleRegSuccess(Long userId) {
        promotionService.issueNewUserExperienceCash(userId);
    }
}