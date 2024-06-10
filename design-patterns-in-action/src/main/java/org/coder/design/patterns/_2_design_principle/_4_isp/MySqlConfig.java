package org.coder.design.patterns._2_design_principle._4_isp;

import org.coder.design.patterns._2_design_principle._4_isp.simulate.ConfigSource;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class MySqlConfig implements Viewer {

    //配置中心（比如zookeeper）
    private ConfigSource configSource;

    public MySqlConfig(ConfigSource configSource) {
        this.configSource = configSource;
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
