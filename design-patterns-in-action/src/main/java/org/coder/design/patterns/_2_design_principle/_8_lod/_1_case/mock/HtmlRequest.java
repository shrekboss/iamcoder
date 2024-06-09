package org.coder.design.patterns._2_design_principle._8_lod._1_case.mock;

import lombok.Data;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class HtmlRequest {

    private String address;
    private String content;

    public HtmlRequest(String url) {
        // ...
    }

}
