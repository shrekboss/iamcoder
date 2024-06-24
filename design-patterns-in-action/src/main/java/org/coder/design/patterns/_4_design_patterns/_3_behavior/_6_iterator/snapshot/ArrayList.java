package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator.snapshot;

import org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator.Iterator;
import org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator.List;

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
public class ArrayList<E> extends java.util.ArrayList<E> implements List<E> {
    private static final int DEFAULT_CAPACITY = 10;

    private int actualSize; //不包含标记删除元素
    private int totalSize; //包含标记删除元素

    private final Object[] elements;
    private final long[] addTimestamps;
    private final long[] delTimestamps;

    public ArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.addTimestamps = new long[DEFAULT_CAPACITY];
        this.delTimestamps = new long[DEFAULT_CAPACITY];
        this.totalSize = 0;
        this.actualSize = 0;
    }

    @Override
    public void add1(E obj) {
        elements[totalSize] = obj;
        addTimestamps[totalSize] = System.currentTimeMillis();
        delTimestamps[totalSize] = Long.MAX_VALUE;
        totalSize++;
        actualSize++;
    }

    @Override
    public void remove1(E obj) {
        for (int i = 0; i < totalSize; ++i) {
            if (elements[i].equals(obj)) {
                delTimestamps[i] = System.currentTimeMillis();
                actualSize--;
            }
        }
    }

    public int actualSize() {
        return this.actualSize;
    }

    public int totalSize() {
        return this.totalSize;
    }

    public E get(int i) {
        if (i >= totalSize) {
            throw new IndexOutOfBoundsException();
        }
        return (E)elements[i];
    }

    public long getAddTimestamp(int i) {
        if (i >= totalSize) {
            throw new IndexOutOfBoundsException();
        }
        return addTimestamps[i];
    }

    public long getDelTimestamp(int i) {
        if (i >= totalSize) {
            throw new IndexOutOfBoundsException();
        }
        return delTimestamps[i];
    }


    @Override
    public Iterator iterator1() {
        return null;
    }
    // ================ todo ================
}

