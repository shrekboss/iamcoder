package org.coder.design.patterns._1_oop._4_polymorphism;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SortedDynamicArray extends DynamicArray {

    @Override
    public void add(Integer e) {
        ensureCapacity();

        int i;
        for (i = size - 1; i >= 0; i--) {
            if (elements[i] >= e) {
                elements[i + 1] = elements[i];
            } else {
                break;
            }
        }
        elements[i + 1] = e;
        ++size;
    }

}

class DynamicArray {

    private static final int DEFAULT_CAPACITY = 10;

    protected int size = 0;
    protected int capacity = DEFAULT_CAPACITY;
    protected Integer[] elements = new Integer[DEFAULT_CAPACITY];

    public int size() {
        return this.size;
    }

    public Integer get(int index) {
        return this.elements[index];
    }

    //...省略n多方法...

    public void add(Integer e) {
        ensureCapacity();
        elements[size++] = e;
    }

    protected void ensureCapacity() {
        // //...如果数组满了就扩容...代码省略...
    }
}
