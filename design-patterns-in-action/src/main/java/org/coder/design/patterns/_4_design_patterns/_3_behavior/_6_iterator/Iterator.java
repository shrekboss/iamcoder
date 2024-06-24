package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator;

/**
 * (what) 接口定义方式一
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public interface Iterator<E> {
    boolean hasNext();

    void next();

    E next1();

    E currentItem();

    void remove();
}

