package org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor.alternative_solution;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class PdfExtractor implements Extractor {
    @Override
    public void extract2txt(ResourceFile resourceFile) {
        //...
        System.out.println("Extract PDF.");
    }
}
