package org.coder.design.patterns._2_design_principle._4_isp;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class DbMetrics implements Viewer {
    @Override
    public String outputInPlainText() {
        return "";
    }

    @Override
    public Map output() {
        return Collections.emptyMap();
    }
}
