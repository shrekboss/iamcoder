package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 6.1.1 如何创建Stream
 * 简单了解了Stream的基本用法之后，不难发现我们是通过一个容器（list）的stream()方法获取了java.util.stream.Stream，那么什么是Stream呢？
 * 简单来讲，Stream是支持顺序或者并行操作的元素序列。Java的Stream具有如下几个特点。
 * 1.Stream不存储数据，这是其与Collection最大的区别之一。
 * 2.Stream不是数据结构，而是从Collection、数组、I/O等获取输入。
 * 3.Stream不会改变原来的数据结构。
 * 4.Stream可以是无限元素集合。
 * 5.Stream支持lazy操作。
 * 6.每一个intermediate操作都将会以lazy的方式执行，并且返回一个新的Stream，比如filter()方法。
 * 7.Terminal操作将会结束Stream，并且返回最终结果，比如collect()方法。
 * 8.Stream无法被重用，即对Stream的每一次操作都会产生一个全新的Stream。
 * 9.Stream支持函数式编程。
 * 10.其他。
 * 大概了解了Stream之后，我们一起看一下可以通过哪些方式获取或者创建Stream。
 */
public class StreamExample2 {
    /**
     * (1).From Values：利用Stream接口提供的静态方法of获取一个Stream
     */
    private Stream<Integer> fromValues() {
        return Stream.of(1, 2, 3, 4);
    }

    /**
     * fromValues()方法中通过of可变长数组的方式获取了一个整数类型的Stream，我们通过map操作对每一个元素进行了double的操作从而产生了新的Stream。
     * 在打印分隔符之后使用stream的foreach操作（该操作为中断操作）输出每一个元素，大家通过执行这个代码片段可以发现map函数的执行是以lazy的方式进行的，类似的方法包含如下几个。
     * 1.<T> Stream<T> of(T t)
     * 2.<T> Stream<T> of(T ... values)
     * 3.<T> Stream<T> ofNullable(T t) （JDK 9 版本以上才支持）
     */
    @Test
    public void fromValuesTest() {
        Stream<Integer> stream = fromValues().map(i -> {
            System.out.println("multiply by 2");
            return i * 2;
        });
        System.out.println("============================");
        stream.forEach(System.out::println);
    }

    /**
     * (2).通过Stream.Builder来创建Stream
     * 借助于Stream的Builder也可以创建一个Stream，该Builder同时又继承自函数式接口Consumer<T>。
     *
     * @return
     */
    private Stream<Integer> fromBuilder() {
        return Stream.<Integer>builder()
                .add(1)
                .add(2)
                .add(3)
                .add(4)
                .build();
    }

    /**
     * (3).空Streams
     * 假设我们所写的方法其返回类型是Stream<T>类型，有些时候可能需要返回一个空的Stream，就像返回空的字符串、空的集合容器等一样，这里创建一个空的Stream<T>类型，并且将其作为返回值。
     * <p>
     * 如果是基本数据类型，则可以使用相关的NumericStream直接返回。
     * IntStream intStream = IntStream.empty();
     * LongStream longStream = LongStream.empty();
     * DoubleStream doubleStream = DoubleStream.empty();
     *
     * @return
     */
    private Stream<File> emptyStream() {
        return Stream.empty();
    }

    /**
     * (4).通过Functions创建无限元素的Stream
     * Stream<T>接口还提供了创建无限元素的Stream方法：generate和iterate方法。
     * <p>
     * generate方法需要一个Supplier函数式接口。
     *
     * @return
     */
    private Stream<Integer> infiniteStreamByGenerate() {
        return Stream.generate(() -> ThreadLocalRandom.current().nextInt(10));
    }

    /**
     * iterate方法需要一个seed和UnaryOperator函数式接口。
     * <p>
     * 通过下述代码获取的Stream，元素将会从100开始逐次加一，无限循环。
     *
     * @return
     */
    private Stream<Integer> infiniteStreamByIterate() {
        return Stream.iterate(100, seed -> seed + 1);
    }

    /**
     * (5)通过NumericStream创建无限元素的Stream
     * 通过相关的NumericStream iterate和generate方法创建Stream。
     * <p>
     * LongStream iterate(final long seed, final LongUnaryOperator f)
     * LongStream generate(LongSupplier s)
     * DoubleStream generate(DoubleSupplier s)
     * DoubleStream iterate(final double seed, final DoubleUnaryOperator f)
     * IntStream iterate(final int seed, final IntUnaryOperator f)
     * IntStream generate(IntSupplier s)
     * <p>
     * (6)通过NumericStream创建有限元素的Stream
     * NumericStream除了提供创建无限元素的方法之外，还提供了创建有限元素的静态方法，下面以IntStream为例进行说明。
     *
     * @return
     */
    private IntStream rangeNumericStream() {
        //IntStream的range方法将会创建一个半开半闭的区间{x|1<=x<10}
        return IntStream.range(1, 10);
    }

    private IntStream rangeClosedNumericStream() {
        //IntStream的range方法将会创建一个闭区间{x|1<=x<=10}
        return IntStream.rangeClosed(1, 10);
    }

    /**
     * (7)通过数组创建Stream
     * 自JDK1.8以来，java.util.Arrays提供了stream()静态方法，通过该方法，我们可以创建Stream。
     *
     * @return
     */
    private Stream<Entity> fromArrays() {
        return Arrays.stream(new Entity[]{new Entity(), new Entity()});
    }

    /**
     * (8)通过集合容器创建Stream
     * 自JDK1.8版本开始，Collection接口增加了新的方法stream()用于创建与之关联的Stream对象。
     *
     * @return
     */
    private Stream<String> fromCollection() {
        Collection<String> list = Arrays.asList("Hello", "Stream");
        return list.stream();
    }

    /**
     * (9)通过Map容器创建Stream
     * Map并未提供创建Stream的方法，但是我们可以通过entry set的方式间接创建一个类型为Entry键值对的元素序列，提供对Map的Stream支持。
     *
     * @return
     */
    private Stream<Map.Entry<String, String>> fromMap() {
        return new HashMap<String, String>() {
            {
                put("Hello", "Stream");
                put("Java", "Programming");
            }
        }
                .entrySet()//获取Entry<String, String>的Set
                .stream();//进而创建一个Stream<Map.Entry<String, String>>
    }

    /**
     * (10)通过Files创建Stream
     * java.io和java.nio.file包支持通过Streams对I/O进行操作。比如，你可以读取一个文本文件，并且创建String类型的Stream，该Stream元素序列中的每一个元素就代表了该文件的每一行文本。
     * <p>
     * 以后对Stream的每一个操作事实上是针对每一行文本记录进行的操作。
     *
     * @return
     * @throws IOException
     */
    private Stream<String> fromFile() throws IOException {
        return Files.lines(Paths.get("test.txt"), Charset.forName("UTF-8"));
    }

    /**
     * (11)通过其他方式创建Stream
     * 除了本节所列举的一些创建Stream的方式，还有很多其他的方式，比如，可以通过String创建IntStream。
     * 甚至一些第三方框架或者平台都提供了对Stream操作的支持，比如，Spark、Flink、Storm的Trident、JOOQ等，
     * 除此之外，本章的最后将为大家展示如何自定义一个Stream，以便大家更加深入地理解Stream。
     *
     * @return
     */
    private IntStream fromString() {
        String line = "Hello i am Stream";
        return line.chars();
    }
}