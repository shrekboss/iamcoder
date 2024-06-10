package org.coder.design.patterns._2_design_principle._8_lod._1_case;

import lombok.Data;
import org.coder.design.patterns._2_design_principle._8_lod._1_case.simulate.Html;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@Data
public class Document {

    private Html html;
    private String url;

    public Document(String url, Html html) {
        this.html = html;
        this.url = url;
    }

    // ...
}
