package org.coder.concurrency.programming.juc._4_colleciton;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 3.跳表(SkipList)性能测试
 * 跳表的基本功能已经完成，并且进行了最基本的功能测试，本节就来对比一下我们实现的跳表和ArrayList在二分法的加持下的性能对比，
 * 鉴于跳表是一种以空间换取时间的数据结构，比较耗费内存，因此本节使用50万数量级的数据进行基准测试。
 * <p>
 * 基准测试的代码比较简单，直接运行输出性能对比结果，你会发现跳表的表现是如此的优异(虽然看起来还是没有ArrayList的搜索性能高，
 * 但是别忘了，ArrayList中的元素经过了排序，并且是在采用二分法进行查找的情况下完成的，而跳表在存入数据时就已经完成了排序，
 * 并且不需要使用二分法进行查找)。
 * Benchmark                                                  Mode  Cnt  Score   Error  Units
 * ArrayListVsSkipListBinarySearch.binarySearchFromArrayList  avgt   20  0.351 ± 0.007  us/op
 * ArrayListVsSkipListBinarySearch.binarySearchFromSkipList   avgt   20  0.693 ± 0.002  us/op
 * <p>
 * 4.1.4 链表总结
 * 本节首先介绍了链表、链表最基本的实现方式，紧接着又详细介绍了优先级链表，也就是数据在进入链表后会进入某种规则的排序。
 * 由于链表结构要进行某个特定值的查找，因此检索的速度，我们第一时间会想到平衡树的数据结构，但是实现平衡树相对来说比较复杂，
 * 于是我们有借助于多层链表，也就是跳表的数据结构来完成。4.1.3节中实现了一个最简单、最基础的跳表程序，
 * 并且对比分析了数据的检索性能，发现并不会输给已排序的ArrayList在二分法加持下的性能。其实自jdk1.7版本开始就已经引入了跳表的实现类，
 * 在本章稍后的内容中，将会学习ConcurrentSkipListMap和ConcurrentSkipListSet，其内部的主要数据结构就是跳表。
 */
@Warmup(iterations = 20)
@Measurement(iterations = 20)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class ArrayListVsSkipListBinarySearch {

    private ArrayList<Integer> arrayList;
    private SimpleSkipList skipList;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        this.random = new Random(System.currentTimeMillis());
        this.arrayList = new ArrayList<>();
        this.skipList = new SimpleSkipList();
        for (int i = 0; i < 500_000; i++) {
            arrayList.add(i);
            skipList.add(i);
        }
    }

    @Benchmark
    public void binarySearchFromArrayList(Blackhole blackhole) {
        int randomValue = random.nextInt(500_000);
        int result = Collections.binarySearch(arrayList, randomValue);
        blackhole.consume(result);
    }

    @Benchmark
    public void binarySearchFromSkipList(Blackhole blackhole) {
        int randomValue = random.nextInt(500_000);
        int result = skipList.get(randomValue);
        blackhole.consume(result);
    }

    public static void main(String[] args) throws RunnerException {
        // TODO Auto-generated method stub
        final Options opt = new OptionsBuilder().include(ArrayListVsSkipListBinarySearch.class.getSimpleName()).build();
        new Runner(opt).run();
    }

}