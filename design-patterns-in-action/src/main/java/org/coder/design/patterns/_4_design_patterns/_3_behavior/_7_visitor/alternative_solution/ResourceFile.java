package org.coder.design.patterns._4_design_patterns._3_behavior._7_visitor.alternative_solution;

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
public abstract class ResourceFile {
    protected String filePath;
    public ResourceFile(String filePath) {
        this.filePath = filePath;
    }
    public abstract ResourceFileType getType();
}
