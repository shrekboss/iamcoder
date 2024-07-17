package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * 6.3 Parallel Stream 详解
 * 本章其实已经提到过关于并行流（Parallel Stream）的使用，在Java的Stream中想要创建一个并行流是非常方便的事情，这就允许我们的应用程序对集合中、IO中、数组中等其他的元素以并行的的方式计算处理元素数据。
 * 在并行流的处理过程中，元素会被拆分为多个元素块（chunks），每一个元素块都包含了若干元素，该元素块将被一个独立的线程运算，当所有的元素块被不同的线程运算结束之后，结果汇总将会作为最后的结果，
 * 这一切的一切都是由并行流（Parallel Stream）替我们完成的。本节将学习并掌握并行流的知识，理解在并行流中元素块是如何进行拆分的，以及开发者是如何自定义这种拆分逻辑（Spliterator）的。
 */
public class ParallelStreamExample {

    /**
     * 6.3.1 并行流Parallel Stream
     * 为了能够快速体验Parallel Stream带来的性能提升，下面先从一个简单的例子起步，慢慢地迭代出Parallel Stream的使用方法。
     * 这里我们以一个最简单的自然数累加运算为例。
     * <p>
     * 下面的代码片段中，我们想要使基于并行的方式运算将是非常困难的，最起码在JDK1.7版本以前，我们需要很好地处理数据的分区，需要为每一个分区分配不同的线程，并且对线程进行管理，
     * 然后还要处理资源竞争的情况，以及最后等待不同线程任务的结束和汇总最终的结果。当然，由于Fork Join计算框架的引入，我们可以通过划分不同的RecursiveTask来处理对应的数据分区，
     * 但是仍然需要我们根据对应的逻辑，显式地对数据元素进行子任务拆分，借助于Stream就不用那么麻烦了。
     * <p>
     * 在并行流的运算过程中，开发者无需关心需要多少个线程一起并行地工作，更不需要关心如何对最终的结果进行汇总，计算过程中共享数据将以何种方式进行同步，或者根本就是无锁的操作形式，
     * 总之，并行流的操作为开发者很好地屏蔽了这一切。根据第4章学习到的Fork Join的知识，很容易就能画出并行流下reduce操作的执行流程（如图6-10所示）。
     */
    @Test
    public void test() {
        long sum = 0L;
        for (long l = 0; l < 10_000_000; l++) {
            sum += l;
        }
    }

    /**
     * 下面的代码是经过Stream重构后的代码片段。
     */
    @Test
    public void test2() {
        long sum = Stream.iterate(0L, l -> l + 1L).limit(10_000_000).reduce(0L, Long::sum);
        System.out.println(sum);//49999995000000
    }

    /**
     * 针对Stream重构之后的代码，我们想要使其并行化工作就是一件非常容易的事情了，只需要将串行流转换为并行流即可，代码如下所示。
     */
    @Test
    public void test3() {
        long sum = Stream.iterate(0L, l -> l + 1L).limit(10_000_000)
                .parallel()//将串行流并行化（转换为并行流）
                .reduce(0L, Long::sum);
        System.out.println(sum);//49999995000000
    }

    @Test
    public void test4() {
        long sum = LongStream.range(0, 10_000_000).reduce(0L, Long::sum);
    }

    @Test
    public void test5() {
        long sum = LongStream.range(0, 10_000_000).parallel().reduce(0L, Long::sum);
    }
}