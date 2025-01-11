package org.coder.design.patterns._2_design_principle._8_lod._2_case;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Deserializable {

    Object deserialize(String text);
}