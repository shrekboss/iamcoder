package org.coder.design.patterns._5_open_source_practices._spring;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * (what) Publisher发送者
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Component
public class DemoPublisher {
    @Resource
    private ApplicationContext applicationContext;

    public void publishEvent(DemoEvent demoEvent) {
        this.applicationContext.publishEvent(demoEvent);
    }
}
