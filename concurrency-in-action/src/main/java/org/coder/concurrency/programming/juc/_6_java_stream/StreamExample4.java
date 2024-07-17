package org.coder.concurrency.programming.juc._6_java_stream;

import com.google.common.base.Strings;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 6.1.3 Stream之Terminal操作
 * Stream的Terminal操作会终结Stream的流水线（pipeline）的继续执行，最终返回一个非Stream类型的结果（foreach操作可以理解为返回的是void类型的结果）。
 * 因此在一个Stream的流水线中执行了Terminal方法之后，Stream将被关闭。
 * <p>
 * Stream提供了比较多的Terminal类型的操作，本节将逐个介绍Terminal类型操作的使用方法。
 * <p>
 * 方法		描述
 * match	match类型的操作返回值为布尔类型，主要用于判断是否存在匹配条件（Predicate）的元素，match类型的具体操作如下。
 * allMatch():若所有的元素都匹配条件，则结果为true，否则为false
 * anyMatch():只要有一个元素匹配条件，则结果为true，否则为false
 * noneMatch():若所有的元素都不匹配条件，则结果为true，否则为false
 * find		find类型的操作会返回Stream中的某个元素Optional，一般情况下，我们会在一个包含filter操作的流水线中使用find操作返回过滤后的某个值，find类型的具体操作如下。
 * Optional<T> findFirst(): 返回Stream中的第一个元素
 * Optional<T> findAny(): 返回Stream中的任意一个元素
 * foreach	forEach操作用于对Stream中的每一个元素执行consume函数，Stream提供了两种方式的foreach，具体如下。
 * forEach(Consumer<T> consumer):为每一个元素执行consume函数，但是在并发流中，对source stream或者upstream的执行并不会按顺序来
 * forEachOrdered(Consumer<T> consumer):为每一个元素执行consumer函数，在并发流中将保持对source stream或者upstream的原始顺序
 * count	count操作用于返回Stream中的元素个数，返回值为一个long型的数值
 * max		max类型的操作会根据Comparator接口的定义，返回Stream中最大的那个元素
 * min		min类型的操作会根据Comparator接口的定义，返回Stream中最小的那个元素
 * collect	collect操作可以将Stream中的元素聚合到一个新的集合中，比如map、set、list
 * reduce	reduce操作通过BinaryOperator<T>函数式接口对Stream中的所有元素逐次进行计算，得到一个最终值且返回
 */
public class StreamExample4 {
    /**
     * 1.match 操作
     * match类型的操作其返回值为布尔类型，该操作主要用于判断是否存在匹配条件（Predicate）的元素，match类型的操作主要包含如下3种。
     * 1).allMatch():若所有的元素都匹配条件，则结果为true，否则为false
     * 2).anyMatch():只要有一个元素匹配条件，则结果为true，否则为false
     * 3).noneMatch():若所有的元素都不匹配条件，则结果为true，否则为false
     */
    @Test
    public void match() {
        //所有的元素都大于0
        assert Stream.of(1, 2, 3, 4, 5, 6).allMatch(i -> i > 0);
        //只要有一个元素大于5就满足匹配条件
        assert Stream.of(1, 2, 3, 4, 5, 6).anyMatch(i -> i > 5);
        //所有的元素都不大于10
        assert Stream.of(1, 2, 3, 4, 5, 6).noneMatch(i -> i > 10);
    }

    /**
     * 2.find 操作
     * find类型的操作会返回Stream中的某个元素Optional，一般情况下，我们会在一个包含filter操作的流水线中使用find操作返回过滤后的某个值，find类型的具体操作如下。
     * 1).Optional<T> findFirst(): 返回Stream中的第一个元素
     * 2).Optional<T> findAny(): 返回Stream中的任意一个元素
     */
    @Test
    public void find() {
        Stream.of(1, 2, 3, 4, 5, 6)
                //过滤操作
                .filter(i -> i > 3)
                //获取第一个元素，返回Optional<Integer>
                .findFirst().ifPresent(r -> {
                    assert r == 4;
                });

        assert Stream.of(1, 2, 3, 4, 5, 6).filter(i -> i > 3)
                //获取任意一个元素，返回结果同样为Optional<Integer>
                .findAny()
                //由于返回结果是任意值，因此不能使用具体值进行断言，存在即可
                .isPresent();

        //过滤之后的Stream为空，因此findAny返回的Optional<Integer>将不存在元素
        assert !Stream.of(1, 2, 3, 4, 5, 6).filter(i -> i > 10).findAny().isPresent();
    }

    /**
     * 3. foreach操作
     * forEach操作用于对Stream中的每一个元素执行consume函数，Stream提供了两种方式的foreach，具体如下。
     * 1).forEach(Consumer<T> consumer):为每一个元素执行consume函数，但是在并发流中，对source stream或者upstream的执行并不会按顺序来
     * 2).forEachOrdered(Consumer<T> consumer):为每一个元素执行consumer函数，在并发流中将保持对source stream或者upstream的原始顺序
     * <p>
     * 执行上面的程序片段，大家会发现，forEach操作在对并行流中的每一个元素执行consume函数时的输出顺序是乱序的，
     * 而forEachOrdered则始终能够保持source stream的原始元素顺序，当然在普通的流中两者并没有任何区别。
     */
    @Test
    public void foreach() {
        IntStream.range(0, 100)
                //转换为并行流
                .parallel()
                //执行foreach操作，输出每一个元素
                .forEach(System.out::println);
        System.out.println("========================================");
        IntStream.range(0, 100)
                //转换为并行流
                .parallel()
                //在并行流中，执行forEachOrdered操作  ，输出每一个元素
                .forEachOrdered(System.out::println);
    }

    /**
     * 4.count操作
     * count操作用于返回Stream中元素的个数，使用起来也是非常简单的。
     */
    @Test
    public void count() {
        long count = Stream.of(1, 2, 3, 4, 5, 6)
                //过滤
                .filter(i -> i % 2 == 0)
                //执行count操作，返回Stream中元素的个数
                .count();
        //断言数量为3
        assert count == 3;
        //与下面的写法完全等价
        long sum = Stream.of(1, 2, 3, 4, 5, 6).filter(i -> i % 2 == 0).mapToLong(i -> 1L).sum();
        assert sum == 3;
    }

    /**
     * 5.max操作
     * 根据Comparator接口的定义，max操作会返回Stream中最大的那个元素，在执行该操作时需要指定Comparator的实现。
     */
    @Test
    public void max() {
        Optional<Integer> max = Stream.of(1, 2, 3, 4, 5, 6).max(Comparator.comparingInt(o -> o));
        assert max.get() == 6;
    }

    /**
     * 6.min操作
     * 根据Comparator接口的定义，min操作会返回Stream中最小的那个元素，在执行该操作时需要指定Comparator的实现。
     */
    @Test
    public void min() {
        Optional<Integer> min = Stream.of(1, 2, 3, 4, 5, 6).min(Comparator.comparingInt(o -> o));
        assert min.get() == 1;
    }

    /**
     * 7.collect操作
     * collect操作可以将Stream中的元素聚合到一个新的集合中，比如map、set、list，甚至integer中，collect操作涉及Collector的使用。
     * 本节只是简单地演示一下如何使用collect方法即可，6.2节中会详细讲解Collector接口的原理，以及在Stream中该如何使用等。
     * <p>
     * 怎么样，collect方法配合Collector使用，其功能非常强大吧。该方法不只是强大而且还非常灵活，因此本书专门有一个独立的小节为读者详细讲解collector接口的使用和原理。
     */
    @Test
    public void collect() {
        List<String> list = Stream.of(1, 2, 3, 4, 5, 6)
                //通过map操作，将i转换成字符串，Strings为Google Guava工具类
                .map(i -> Strings.repeat(String.valueOf(i), i))
                //将结果聚合到一个list容器中
                .collect(Collectors.toList());
        //输出结果
        System.out.println(list);

        /**
         * 如果想将一个list容器中的所有单词进行字数统计，借助于collect操作将是非常容易实现的，代码如下所示。
         */
        //单词list
        List<String> words = Arrays.asList("Scala", "Java", "Stream", "Java", "Alex", "Scala", "Scala");
        //根据words创建一个Stream
        Map<String, Long> count = words.stream()
                //执行collect操作
                .collect(
                        //进行分组操作
                        Collectors.groupingBy(Function.identity(), Collectors.counting()));
        //输出结果
        System.out.println(count);
    }

    /**
     * 8.reduce操作
     * reduce操作通过BinaryOperator<T>函数式接口对Stream中的所有元素逐次进行计算，得到一个最终值并且返回。
     */
    @Test
    public void reduce() {
        //假如我们想计算Stream中元素相加之和，则可以借助于reduce操作很好地完成。
        Integer sum = Stream.of(4, 5, 3, 9).reduce(0, Integer::sum);
        System.out.println(sum);

        //程序的执行结果肯定是Stream所有元素之和21。下面再来看一个例子，假设我们想得到一个String集合中长度最长的字符串又该如何操作呢？
        List<String> words = Arrays.asList("Java", "Scala", "Stream", "JavaStreamAndReduce", "ScalaStream");
        String maxLengthWords = words.stream().reduce((s1, s2) -> s1.length() > s2.length() ? s1 : s2).get();
        assert maxLengthWords.equals("JavaStreamAndReduce");
    }
}