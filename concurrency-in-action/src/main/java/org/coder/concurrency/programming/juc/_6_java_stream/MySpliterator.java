package org.coder.concurrency.programming.juc._6_java_stream;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * 2.自定义Spliterator及Stream
 * 了解了Spliterator的接口方法之后，我们需要将其串联在一起作为一个整体进行理解，最好的方式要么是直接看JDK提供的Spliterator实现，要么就是通过自定义的方式来加深体会，
 * 本节将通过一个自定义Spliterator操作数组的实例为大家讲解在Stream中如何使用Spliterator接口。
 *
 * @param <T>
 */
public class MySpliterator<T> implements Spliterator<T> {

    private final T[] elements;
    private int currentIndex = 0;
    private final int CAPACITY;

    //通过构造函数传入数组元素
    public MySpliterator(T[] elements) {
        this.elements = elements;
        this.CAPACITY = elements.length;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        //处理Stream中的元素
        action.accept(elements[currentIndex++]);
        //判断Stream中的元素是否已排干
        return currentIndex < CAPACITY;
    }

    @Override
    public Spliterator<T> trySplit() {
        //以10作为基准进行子任务拆分，若当前残留元素数量少于10，则不再拆分
        int remainingSize = CAPACITY - currentIndex;
        if (remainingSize < 10) {
            return null;
        }
        //拆分的过程，进行数组拷贝，并且返回一个新的Spliterator
        int middleSize = (remainingSize) / 2;
        T[] newElements = (T[]) new Object[middleSize];
        System.arraycopy(elements, currentIndex, newElements, 0, middleSize);
        final MySpliterator<T> spliterator = new MySpliterator<>(newElements);
        this.currentIndex = currentIndex + middleSize;
        return spliterator;
    }

    @Override
    public long estimateSize() {
        //由于数组是确定的，因此可以非常精准地得出Stream中的残留元素
        return CAPACITY - currentIndex;
    }

    //定义Spliterator的特征值
    @Override
    public int characteristics() {
        //有序、数量固定、子任务数量也固定，担保不存在非空值，并且不允许改变源
        return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL | Spliterator.IMMUTABLE;
    }

}