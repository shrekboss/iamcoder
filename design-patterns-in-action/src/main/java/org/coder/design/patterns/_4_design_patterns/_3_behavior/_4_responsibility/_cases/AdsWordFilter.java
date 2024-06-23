package org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._cases;

import org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._cases.simulate.Content;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class AdsWordFilter implements SensitiveWordFilter {
    
    @Override
    public boolean doFilter(Content content) {
        boolean legal = true;

        //...

         return legal;
    }
}
