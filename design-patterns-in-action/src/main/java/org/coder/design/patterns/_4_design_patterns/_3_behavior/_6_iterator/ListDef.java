package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator;

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
public interface ListDef<E> extends java.util.List<E> {
    Iterator iterator1();

    void remove1(E obj);

    void add1(E obj);
    //...省略其他接口函数...
}
