package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 第6章 Java Stream 详解
 * 在JDK的版本升级过程中，Java 8 绝对堪称一次里程碑式的升级，也是相较之前的历史版本改动最大的一次，
 * Java 8 提供了诸如lambda表达式、函数式编程的支持、静态推导、新的日期API等诸多新特性。
 * Stream是Java 8 中比较闪亮的一个新特性，但是它绝对不等同于IO包中的Stream和解析XML的Stream。
 * Java 8中的Stream也不是一个容器，它并不是用来存储数据的，而是对JDK中Collections的一个增强，它专注于对集合对象即便利又高效的聚合操作。
 * 它不仅支持串行的操作功能，而且还借助于JDK1.7中的Fork-join机制支持并行模式，开发者无须编写任何一行并行相关的代码，就能高效方便地写出高并发的程序，
 * 尤其是在当下多核CPU的时代，最大程度地利用CPU的超快计算能力显得尤为重要。
 * <p>
 * 6.1 Stream介绍及其基本操作
 * 很难想象程序员在开发程序中不使用Collection容器。我们通常会将数据加载到容器中进行处理计算，或者将计算的中间结果暂存到容器中，甚至是将整个容器作为结果保存在某个地方，等等。
 * 对容器的使用就像是使用基本数据类型一样已成为程序开发的基本操作，在JDK1.8版本中，Stream为容器的使用提供了新的方式，它允许我们通过陈述式的编码风格对容器中的数据进行分组、
 * 过滤、计算、排序、聚合、循环等操作。
 * <p>
 * 上面这段代码是对Stream的一个简单使用，首先我们通过books创建一个stream，然后通过级联式的调用方式（准确地讲是陈述式的编程风格）分别对列表进行过滤、排序、map运算，之后将结果聚合至一个新的结果集中。
 * 对比JDK1.8以前的代码，这种方式简洁清晰、可读性更强，如果你想并发地运行其中的每一个操作步骤，稍作修改即可实现。
 * <p>
 * 通过parallelStream()方法创建一个并行流，开发者及无需关心有多少个线程在工作，线程如何管理，也无须担心列表在并行流下的线程安全性问题。
 */
public class StreamExample1 {

    @Test
    public void stream() {
        List<Book> books = new ArrayList<>();
        final List<String> result = books.stream()
                //书的类目为Programming
                .filter(book -> book.category.equals("Programming"))
                //根据价格排序
                .sorted(Comparator.comparing(Book::getPrice))
                //只获取书的名字
                .map(Book::getName)
                //然后将所有符合条件的结果保存在一个新的List中
                .collect(Collectors.toList());
        System.out.println(result);
    }

    @Test
    public void parallelStream() {
        List<Book> books = new ArrayList<>();
        final List<String> result = books.parallelStream()
                .filter(book -> book.category.equals("Programming"))
                .sorted(Comparator.comparing(Book::getPrice))
                .map(Book::getName)
                .collect(Collectors.toList());
        System.out.println(result);
    }
}