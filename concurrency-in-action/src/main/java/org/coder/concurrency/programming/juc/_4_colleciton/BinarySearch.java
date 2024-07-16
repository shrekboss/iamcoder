package org.coder.concurrency.programming.juc._4_colleciton;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 4.1.3 跳表(SkipList)
 * 无论是什么样的数据结构，存入数据的主要目的之一是对数据进行读取或者检索，当然4.1.1节中使用链表结构实现的栈已经规范了数据的检索形式：只能从栈顶弹出。
 * 既然数据结构的主要用途之一是检索，那么我们思考一下，针对链表类型的数据结构进行特定元素的查找，它的效率表现会是怎样的呢？很明显它的时间复杂度会是O(1)或者O(n)。
 * <p>
 * 相比基于数组结构的线性表，数组结构根据下标的元素进行检索的时间复杂度始终为O(1)。即使是通过特定元素对其进行查找，在元素进行了排序的前提下，借助于一些查找算法，
 * 比如二分法，它的查找速度表现始终是非常优异的。而链表这样的数据结构在二分法中进行查找，则会由于维护数据索引的成本比数组高很多(在4.1.1节和4.1.2节的链表实现中压根就没有维护数据索引)，
 * 因此它的表现肯定要比基于数组的线性链表差很多。下面通过一个基准测测试对比ArrayList和Linked在二分查找算法下的性能表现。
 * <p>
 * 在下面的基准测试代码中，我们在一千万的数据量中使用二分法分别对ArrayList和LinkedList进行了性能对比，对比结果显示两者的差异是非常惊人的。
 * Benchmark                                Mode  Cnt      Score      Error  Units
 * BinarySearch.binarySearchFromArrayList   avgt   20      0.984 ±    0.017  us/op
 * BinarySearch.binarySearchFromLinkedList  avgt   20  83670.108 ± 2927.518  us/op
 */
@Warmup(iterations = 20)
@Measurement(iterations = 20)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class BinarySearch {

    private ArrayList<Integer> arrayList;
    private LinkedList<Integer> linkedList;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        this.random = new Random(System.currentTimeMillis());
        this.arrayList = new ArrayList<>();
        this.linkedList = new LinkedList<>();
        for (int i = 0; i < 10_000_000; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }
    }

    @Benchmark
    public void binarySearchFromArrayList(Blackhole blackhole) {
        int randomValue = random.nextInt(10_000_000);
        int result = Collections.binarySearch(arrayList, randomValue);
        blackhole.consume(result);
    }

    @Benchmark
    public void binarySearchFromLinkedList(Blackhole blackhole) {
        int randomValue = random.nextInt(10_000_000);
        int result = Collections.binarySearch(linkedList, randomValue);
        blackhole.consume(result);
    }

    public static void main(String[] args) throws RunnerException {
        // TODO Auto-generated method stub
        final Options opt = new OptionsBuilder().include(BinarySearch.class.getSimpleName()).build();
        new Runner(opt).run();
    }

}