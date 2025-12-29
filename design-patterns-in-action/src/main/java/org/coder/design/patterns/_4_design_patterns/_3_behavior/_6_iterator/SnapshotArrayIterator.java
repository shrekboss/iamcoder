package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SnapshotArrayIterator<E> implements Iterator<E> {
    private int cursor;
    private ArrayListDef<E> snapshot;

    public SnapshotArrayIterator(ArrayListDef<E> arrayList) {
        this.cursor = 0;
        this.snapshot = new ArrayListDef<>();
        this.snapshot.addAll(arrayList);
    }

    @Override
    public boolean hasNext() {
        return cursor < snapshot.size();
    }

    @Override
    public void next() {
        cursor++;
    }

    @Override
    public E next1() {
        E currentItem = snapshot.get(cursor);
        cursor++;
        return currentItem;
    }

    @Override
    public E currentItem() {
        // todo
        return null;
    }

    @Override
    public void remove() {
        // todo
    }
}