package org.coder.design.patterns._4_design_patterns._1_creation._4_prototype.simulate;

import lombok.Data;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class SearchWord {

    private String keyword;
    private int count;
    private long lastUpdateTime;

    public SearchWord(String keyword, int count, long lastUpdateTime) {
        this.keyword = keyword;
        this.count = count;
        this.lastUpdateTime = lastUpdateTime;
    }
}
