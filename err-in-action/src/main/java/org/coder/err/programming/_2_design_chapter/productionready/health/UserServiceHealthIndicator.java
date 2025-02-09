package org.coder.err.programming._2_design_chapter.productionready.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Component
@Slf4j
public class UserServiceHealthIndicator implements HealthIndicator {
    @Resource
    private RestTemplate restTemplate;

    @Override
    public Health health() {
        long begin = System.currentTimeMillis();
        long userId = 1L;
        User user = null;
        try {
            user = restTemplate.getForObject("http://localhost:45678/user?userId=" + userId, User.class);
            if (user != null && user.getUserId() == userId) {
                return Health.up()
                        .withDetail("user", user)
                        .withDetail("took", System.currentTimeMillis() - begin)
                        .build();
            } else {
                return Health.down().withDetail("took", System.currentTimeMillis() - begin).build();
            }
        } catch (Exception ex) {
            log.warn("health check failed!", ex);
            return Health.down(ex).withDetail("took", System.currentTimeMillis() - begin).build();
        }
    }
}
