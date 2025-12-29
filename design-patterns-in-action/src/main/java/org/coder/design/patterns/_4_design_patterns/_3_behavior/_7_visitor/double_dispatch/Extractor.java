package org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor.double_dispatch;

import org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor.PPTFile;
import org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor.WordFile;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */ //...PPTFile、WordFile代码省略...
public class Extractor {
    public void extract2txt(PPTFile pptFile) {
        //...
        System.out.println("Extract PPT.");
    }

    public void extract2txt(PdfFile pdfFile) {
        //...
        System.out.println("Extract PDF.");
    }

    public void extract2txt(WordFile wordFile) {
        //...
        System.out.println("Extract WORD.");
    }
}
