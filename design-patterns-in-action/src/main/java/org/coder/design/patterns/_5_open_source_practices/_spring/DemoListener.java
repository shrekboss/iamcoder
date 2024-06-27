package org.coder.design.patterns._5_open_source_practices._spring;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * (what) Listener监听者
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Component
public class DemoListener implements ApplicationListener<DemoEvent> {

    @Override
    public void onApplicationEvent(DemoEvent demoEvent) {
        String message = demoEvent.getMessage();
        System.out.println(message);
    }
}
