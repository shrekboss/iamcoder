package org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Visitor {

    void visit(PdfFile pdfFile);

    void visit(PPTFile pdfFile);

    void visit(WordFile pdfFile);
}
