package org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator.snapshot;

import org.coder.design.patterns._4_design_patterns._3_behavior._6_iterator.Iterator;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SnapshotArrayIterator<E> implements Iterator<E> {
    private long snapshotTimestamp;
    // 在整个容器中的下标，而非快照中的下标
    private int cursorInAll;
    // 快照中还有几个元素未被遍历
    private int leftCount;
    private ArrayListDef<E> arrayList;

    public SnapshotArrayIterator(ArrayListDef<E> arrayList) {
        this.snapshotTimestamp = System.currentTimeMillis();
        this.cursorInAll = 0;
        this.leftCount = arrayList.actualSize();

        this.arrayList = arrayList;
        // 先跳到这个迭代器快照的第一个元素
        justNext();
    }

    @Override
    public boolean hasNext() {
        // 注意是>=, 而非>
        return this.leftCount >= 0;
    }

    @Override
    public E next1() {
        E currentItem = arrayList.get(cursorInAll);
        justNext();
        return currentItem;
    }

    private void justNext() {
        while (cursorInAll < arrayList.totalSize()) {
            long addTimestamp = arrayList.getAddTimestamp(cursorInAll);
            long delTimestamp = arrayList.getDelTimestamp(cursorInAll);
            if (snapshotTimestamp > addTimestamp && snapshotTimestamp < delTimestamp) {
                leftCount--;
                break;
            }
            cursorInAll++;
        }
    }

    // ================ todo ================
    @Override
    public void next() {

    }

    @Override
    public E currentItem() {
        return null;
    }

    @Override
    public void remove() {

    }
    // ================ todo ================
}
