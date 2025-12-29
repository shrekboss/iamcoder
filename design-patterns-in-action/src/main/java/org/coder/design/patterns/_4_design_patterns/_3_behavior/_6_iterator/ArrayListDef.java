package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ArrayListDef<E> extends java.util.ArrayList<E> implements ListDef<E> {
    public int modCount;

    //...
    @Override
    public Iterator iterator1() {
        return new ArrayIterator(this);
    }

    @Override
    public void remove1(E obj) {

    }

    @Override
    public void add1(E obj) {

    }
    //...省略其他代码
}
