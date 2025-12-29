package org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._cases;

import org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._cases.simulate.Content;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface SensitiveWordFilter {

    boolean doFilter(Content content);
}
