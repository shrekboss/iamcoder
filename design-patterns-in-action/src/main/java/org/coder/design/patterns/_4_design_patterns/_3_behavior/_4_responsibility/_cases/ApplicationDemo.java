package org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._cases;

import org.coder.design.patterns._4_design_patterns._3_behavior._4_responsibility._cases.simulate.Content;

public class ApplicationDemo {
    public static void main(String[] args) {
        SensitiveWordFilterChain filterChain = new SensitiveWordFilterChain();
        filterChain.addFilter(new AdsWordFilter());
        filterChain.addFilter(new SexyWordFilter());
        filterChain.addFilter(new PoliticalWordFilter());

        boolean legal = filterChain.filter(new Content());
        if (!legal) {
            // 不发表
        } else {
            // 发表
        }
    }
}