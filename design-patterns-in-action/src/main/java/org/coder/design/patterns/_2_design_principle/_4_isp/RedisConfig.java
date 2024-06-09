package org.coder.design.patterns._2_design_principle._4_isp;

import lombok.Getter;
import org.coder.design.patterns._2_design_principle._4_isp.mock.ConfigSource;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RedisConfig implements Updater, Viewer {
    //配置中心（比如zookeeper）
    private ConfigSource configSource;
    @Getter
    private String address;
    private int timeout;
    private int maxTotal; //省略其他配置: maxWaitMillis,maxIdle,minIdle...

    public RedisConfig(ConfigSource configSource) {
        this.configSource = configSource;
    }

    //...省略其他get()、init()方法...

    /**
     * 从 configSource 加载配置到address/timeout/maxTotal...
     */
    @Override
    public void update() {
        //...
    }

    @Override
    public String outputInPlainText() {
        return "";
    }

    @Override
    public Map output() {
        return Collections.emptyMap();
    }
}
