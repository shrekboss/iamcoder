package org.coder.design.patterns._2_design_principle._4_isp;

import java.util.Map;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Viewer {

    String outputInPlainText();
    Map output();
}
