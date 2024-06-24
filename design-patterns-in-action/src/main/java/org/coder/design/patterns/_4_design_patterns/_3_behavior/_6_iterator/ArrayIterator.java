package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator;

import java.util.ConcurrentModificationException;

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
public class ArrayIterator<E> implements Iterator<E> {
    private int cursor;
    private ArrayListDef<E> arrayList;
    private int expectedModCount;

    public ArrayIterator(ArrayListDef<E> arrayList) {
        this.cursor = 0;
        this.arrayList = arrayList;
        this.expectedModCount = arrayList.modCount;
    }

    @Override
    public boolean hasNext() {
        checkForModification();
        //注意这里，cursor在指向最后一个元素的时候，hasNext()仍旧返回true。
        return cursor != arrayList.size();
    }

    @Override
    public void next() {
        checkForModification();
        cursor++;
    }

    @Override
    public E next1() {
        return null;
    }

    @Override
    public E currentItem() {
        checkForModification();
        return arrayList.get(cursor);
    }

    @Override
    public void remove() {
        arrayList.modCount += 1;
        arrayList.remove(cursor);
    }

    private void checkForModification() {
        if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();
    }
}

