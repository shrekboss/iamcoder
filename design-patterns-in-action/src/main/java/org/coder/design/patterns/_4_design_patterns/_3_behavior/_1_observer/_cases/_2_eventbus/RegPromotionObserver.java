package org.coder.design.patterns._4_design_patterns._3_behavior._1_observer._cases._2_eventbus;

import com.google.common.eventbus.Subscribe;
import org.coder.design.patterns._4_design_patterns._3_behavior._1_observer._cases._1_sync_blocking.PromotionService;
import org.coder.design.patterns._4_design_patterns._3_behavior._1_observer._cases._1_sync_blocking.RegObserver;

/**
 * 促销观察者
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RegPromotionObserver implements RegObserver {

    // 模拟依赖注入
    private PromotionService promotionService = new PromotionService();

    // // 更新点 5：+ @Subscribe
    @Subscribe
    @Override
    public void handleRegSuccess(Long userId) {
        promotionService.issueNewUserExperienceCash(userId);
    }
}
