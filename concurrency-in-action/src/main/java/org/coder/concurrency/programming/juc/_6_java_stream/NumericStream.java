package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.util.stream.IntStream;

/**
 * 6.1.4 NumericStream详解
 * 6.1.2节及6.1.3节中，关于Stream的所有操作都是基于Stream<T>这个泛型接口而来的，由于是泛型接口，因此其意味着可以支持任意类型的数据元素。
 * 但是在Java中，为何还要提供NumericStream（NumericStream是一个总称，代表着具体数据类型的Stream，比如IntStream、LongStream等）呢？
 * 本节就来揭晓答案，Java中提供了如下几种NumericStream。
 * 1).IntStream:元素为int类型的Stream。
 * 2).DoubleStream：元素double类型的Stream。
 * 3).LongStream:元素为long类型的Stream。
 * <p>
 * 6.1.5 Stream总结
 * 本节首先快速地了解了如何使用Stream丰富Collection的操作，然后非常详细地讲解了如何通过不同的方式创建Stream。
 * Stream的操作方式包含了很多种，但是总体来说可以分为两大类Intermediate和Terminal，
 * 前者操作之后会产生一个全新的Stream，后者则会终止整个Stream pipeline，并且得到最终的返回结果，
 * 同样本节几乎介绍了Stream操作的每一个方法，由于Collector相对比较复杂且内容丰富，因此6.2节将会专门讲解。
 * 本节的最后还列举了不同Stream之间的转换，并且分析了NumericStream存在的意义。
 */
public class NumericStream {

    /**
     * 1.为何要有NumericStream
     * 首先NumericStream提供了更多针对数据类型的操作方式，
     * 比如可以通过sum这个Terminal操作直接获取IntStream中所有int元素相加的和，
     * 可以通过max操作直接获取在IntStream中最大的int类型的元素而无须传入Comparator，
     * 还可以通过min操作直接获取在IntStream中最小的int类型的元素而无须传入Comparator。
     * 示例代码如下。
     */
    @Test
    public void test() {
        assert IntStream.of(1, 2, 3, 4, 5).sum() == 15;
        assert IntStream.of(1, 2, 3, 4, 5).max().getAsInt() == 5;
        assert IntStream.of(1, 2, 3, 4, 5).min().getAsInt() == 1;
    }

    /**
     * 2.Stream之间的互转
     * 在基准测试StreamIntegerVsIntStream.java的streamIntegerUnboxThenReduce方法中，
     * 已经提到如何将一个Stream<Integer>转换为IntStream的操作，IntStream想要转换为Stream<Integer>，采用的也是类似的操作，本节就来简单总结一下。
     * <p>
     * (1)Stream转换为NumericStream
     * 1.IntStream mapToInt(ToIntFunction<? super T> mapper):转换为IntStream。
     * 2.LongStream mapToLong(ToLongFunction<? super T> mapper):转换为LongStream。
     * 3.DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper):转换为DoubleStream。
     * (2)IntStream转换为其他Stream
     * 1.Stream<Integer> boxed():转换为Stream<Integer>。
     * 2.Stream<U> mapToObj(IntFunction<? extends U> mapper):转换为Stream<U>。
     * 3.LongStream mapToLong(IntToLongFunction mapper):转换为LongStream。
     * 4.DoubleStream mapToDouble(IntToDoubleFunction mapper):转换为DoubbleStream。
     * 5.LongStream asLongStream():转换为LongStream。
     * 6.DoubleStream asDoubleStream():转换为DoubleStream。
     * (3)LongStream转换为其他Stream
     * 1.Stream<Long> boxed():转换为Stream<Long>。
     * 2.DoubleStream asDoubleStream():转换为DoubleStream。
     * 3.Stream<U> mapToObj(LongFunction<? extends U> mapper):转换为Stream<U>。
     * 4.IntStream mapToObj(LongToIntFunction mapper):转换为IntStream。
     * 5.DoubleStream mapToDouble(LongToDoubleFunction mapper):转换为DoubleStream。
     * (4)DoubleStream转换为其他Stream
     * 1.Stream<Double> boxed():转换为Stream<Double>。
     * 2.Stream<U> mapToObj(DoubleFunction<? extends U> mapper):转换为Stream<U>。
     * 3.IntStream mapToInt(DoubleToIntFunction mapper):转换为IntStream。
     * 4.LongStream mapToLong(DoubleToLongFunction mapper):转换为LongStream。
     * (5)串行流与并行流之间的转换
     * 默认情况下，我们创建的Stream都是sequential(串行的)，但是如果想将其转换为并行流，则可以借助于parallel()方法将一个串行流转换为并行流，在并行流的运算中，操作将被并行化地运行。
     */
    @Test
    public void parallel() {
        //串行流转换为并行流
        IntStream.of(1, 2, 3, 4, 5).parallel().forEach(System.out::println);
        //并行流
        IntStream parallelStream = IntStream.of(1, 2, 3, 4, 5).parallel();
        //filter和map操作并行运行，然后使stream串行化后再进行操作
        parallelStream.filter(i -> i > 1).map(i -> i * 10).sequential().forEach(System.out::println);
    }
}