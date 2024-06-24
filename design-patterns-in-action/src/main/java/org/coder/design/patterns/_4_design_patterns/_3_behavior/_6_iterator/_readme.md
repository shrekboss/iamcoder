## 迭代器模式

Iterator 接口有两种定义方式。

参考代码如下：

- [Iterator.java](Iterator.java)
- [Iterator1.java](Iterator1.java)

第一种定义方式更加灵活一些，比如可以多次调用 currentItem() 查询当前元素，而不移动游标。

参考完整代码如下：

- [List.java](List.java)
- [ArrayList.java](ArrayList.java)
- [Iterator.java](Iterator.java)
- [ArrayIterator.java](ArrayIterator.java)
- [Demo.java](Demo.java)

### 为什么通过迭代器就能安全的删除集合中的元素呢？

> 稍微提醒一下，在 Java 实现中，迭代器类是容器类的内部类，并且 next() 函数不仅将游标后移一位，还会返回当前的元素。

```java
import java.util.ConcurrentModificationException;import java.util.NoSuchElementException;public class ArrayList<E> {
  transient Object[] elementData;
  private int size;

  public Iterator<E> iterator() {
    return new Itr();
  }

  private class Itr implements Iterator<E> {
    int cursor;       // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such
    int expectedModCount = modCount;

    Itr() {}

    public boolean hasNext() {
      return cursor != size;
    }

    @SuppressWarnings("unchecked")
    public E next() {
      checkForComodification();
      int i = cursor;
      if (i >= size)
        throw new NoSuchElementException();
      Object[] elementData = ArrayList.this.elementData;
      if (i >= elementData.length)
        throw new ConcurrentModificationException();
      cursor = i + 1;
      return (E) elementData[lastRet = i];
    }
    
    public void remove() {
      if (lastRet < 0)
        throw new IllegalStateException();
      checkForComodification();

      try {
        ArrayList.this.remove(lastRet);
        cursor = lastRet;
        lastRet = -1;
        expectedModCount = modCount;
      } catch (IndexOutOfBoundsException ex) {
        throw new ConcurrentModificationException();
      }
    }
  }
}
```

在上面的代码实现中，迭代器类新增了一个 lastRet 成员变量，用来记录游标指向的前一个元素。通过迭代器去删除这个元素的时候，我们可以更新迭代器中的游标和 lastRet 值，来保证不会因为删除元素而导致某个元素遍历不到。如果通过容器来删除元素，并且希望更新迭代器中的游标值来保证遍历不出错，我们就要维护这个容器都创建了哪些迭代器，每个迭代器是否还在使用等信息，代码实现就变得比较复杂了。

### 支持“快照”功能的迭代器

参考代码如下：
- [ArrayList.java](snapshot%2FArrayList.java)
- [SnapshotArrayIterator.java](snapshot%2FSnapshotArrayIterator.java)