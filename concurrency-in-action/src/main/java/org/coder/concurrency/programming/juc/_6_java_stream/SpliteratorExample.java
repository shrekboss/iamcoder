package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 6.3.2 Spliterator详解
 * Spliterator也是Java8引入的一个新接口，其主要应用于Stream中，尤其是在并行流进行元素块拆分时主要依赖于Spliterator的方法定义，
 * 这与我们在ForkJoinPool中进行子任务拆分是一样的，只不过对Spliterator的引入将任务拆分进行了抽象和提取，本节将学习Spliterator接口方法，
 * 并且自定义一个Spliterator的实现，进而自定义一个Stream。
 * <p>
 * 1.Spliterator接口方法详解
 * 1).boolean tryAdvance(Consumer<? super T> action)接口方法：该接口非常类似于迭代器方法，其主要作用是对Stream中的每一个元素进行迭代，
 * 并且交由Consumer进行处理，若返回布尔值true则代表着当前Stream还有元素，若返回false则表明没有元素。
 * 2).trySplit()接口方法：该接口方法代表着对当前Stream中元素进行分区，派生出另外的Spliterator以供并行操作，若返回值为null，则代表着不再派生出新的分区，
 * 这一点非常类似于Fork Join中的子任务拆分操作。
 * 3).estimateSize()接口方法：该方法主要用于评估当前Stream中还有多少元素未被处理，一般进行子任务划分时会将基于该接口方法的返回值作为主要依据。
 * 4).characteristics()：与Collector的特征值接口类似，该方法主要用于定义当前Spliterator接口的特征值，其包含如下几个值可用于定义。
 * ①SIZED - 能够准确地评估出当前元素的数量。
 * ②SORTED - 数据源是已排序的元素。
 * ③SUBSIZED - 利用trySplit()方法进行子任务拆分后，Spliterator元素可被准确评估。
 * ④CONCURRENT - 数据源可被线程安全地修改。
 * ⑤DISTINCT - 数据源中的数量是不会被修改的，可以根据equalTo方法进行判断。
 * ⑥IMMUTABLE - 数据源元素是不会被修改的，比如add、remove等。
 * ⑦NONNULL - 数据源的每一个元素都非空。
 * ⑧ORDERED - 数据源是有序的元素。
 * <p>
 * 6.3.3 Spliterator 总结
 * 本节非常详细、系统地学习了Spliterator接口以及接口方法，并且通过并行流的加法操作进行了性能的对比，可以发现它的确能够并行化地工作运行，提高程序的运算效率，
 * 但是在使用并行流的过程中一定要清晰地知道Stream中元素的类型，熟练每一个Stream操作方法的原理，否则即使你使用的是并行流，它的效率也很有可能会不尽如人意。
 * <p>
 * 本节还学习了如何自定义Spliterator、自定义Stream，并行流在操作过程中为开发者完全屏蔽掉了线程的管理、子任务的划分，以及最后结果的整合等细节，
 * 自定义Spliterator以及Stream可以帮助我们窥探出并行流的一些底层知识。Spliterator在Stream中所承担的主要任务就是帮助并行计算进行任务拆分，
 * 如果你熟练掌握了Fork Join框架的运行原理，相信理解起来并不是一件多么困难的事情。
 * <p>
 * 现在回顾一下在6.3.1节中使用Stream.iterate所创建的Stream，虽然我们在进行reduce操作之前需要将其转换为并行流，但是效率似乎并未得到有效的提升，反倒是下降 了很多。
 * 元素类型的拆箱封箱开销的确是一个因素，但是另外一个因素是通过该方法创建的Stream压根无法进行并行计算，因为它无法进行子任务的拆分操作。
 * <p>
 * 再来回顾一下6.1.1节中创建无限元素Stream的内容，iterate方法经常被用于创建无限元素的Stream，
 * 试想一下，如果一个Stream所对应的数据源元素是无限多个，那么这种情况下该如何进行剩余元素的评估呢？
 * 结合我们自定义Spliterator的内容是无法进行评估的，那么这种情况下该怎样进行子任务的拆分呢？
 * 答案就是iterate所创建的Stream不会进行子任务的拆分，因此在并行流的计算过程中白白浪费了线程创建、销毁、管理等资源的开销，
 * 好了，下面就来验证一下iterate方法创建的Stream到底会不会进行子任务的拆分。
 * <p>
 * 6.4 本章总结
 * 本章学习了Java8中关于Stream的几乎所有知识，包括如何创建Stream，Stream的两种类型操作方法intermediate和terminal，
 * 且着重介绍了Stream的collect操作中的Collector原理以及Collectors所有的工厂方法和自定义Collector，
 * collect操作功能强大且灵活，使用得当可以减少很多代码的开发，并且发挥较大的威力，因此本章花费较多笔墨对其进行介绍也不为过。
 * <p>
 * 本章的最后还着重学习了并发流的知识，以及在并发流中，子任务是如何拆分的，通过自定义Spliterator和Stream的讲解和学习，相信读者已经可以对其深入掌握了。
 * <p>
 * 虽然Stream属于JDK第八个大版本中的内容，但是本章并没有为读者介绍Java8的其他知识，比如Lambda表达式、静态推导、函数式接口，等待。
 * 如果读者对Java8的内容还不熟悉，那么请自行寻找相关资料进行学习（注：JDK1.8版本官方已经停止升级和维护了，如果还未掌握Java8的新语法特点，
 * 那么后面的Java 9、10、11、12将会更加困难）。在这里，强烈推荐一个学习Java8知识的书，即由Raoul-Gabriel Urma、Mario Fusco、
 * Alan Mycroft三位合著的《Java 8 in action》。
 */
public class SpliteratorExample {
    /**
     * 在Java 8 中，所有的容器类都增加了对Spliterator的支持，我们可以通过方法直接获取Spliterator。
     */
    @Test
    public void test() {
        List<String> list = new ArrayList<>();
        //获取Spliterator
        Spliterator<String> spliterator = list.spliterator();

        int expected = Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        //断言该Spliterator的特征值
        assert expected == spliterator.characteristics();
    }

    /**
     * 我们自定义的Spliterator已经完成了代码的编写，在对其进行使用之前，首先需要验证一下子任务的拆分是否合理正确。
     * <p>
     * 运行上面的程序，将会得到如下的输出，输出结果与我们在代码注释中的分析完全一致。
     */
    @Test
    public void test2() {
        //定义一个数组，有30个元素。
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};

        //定义我们自定义的Spliterator并且传入数组
        MySpliterator<Integer> mySpliterator = new MySpliterator<>(ints);

        //调用拆分方法，拆分后s1将被分配1~15之间的元素
        Spliterator s1 = mySpliterator.trySplit();

        //此刻mySpliterator的元素为16~30之间的元素
        //再次调用拆分方法，s2将被分配16~22之间的元素，与此同时，mySpliterator将保留其余的元素
        Spliterator s2 = mySpliterator.trySplit();

        //输出s1中的元素
        s1.forEachRemaining(System.out::println);
        System.out.println("====================================");
        //输出s2中的元素
        s2.forEachRemaining(System.out::println);
        System.out.println("====================================");

        //输出mySpliterator中的元素
        mySpliterator.forEachRemaining(System.out::println);
    }

    /**
     * Spliterator已经创建完成，想要使其能够应用于Stream之中，还需要基于该Spliterator创建一个全新的Stream，创建方式很简单，使用StreamSupport提供的方法即可。
     */
    @Test
    public void test3() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
        MySpliterator<Integer> mySpliterator = new MySpliterator<>(ints);
        //false代表串行流
        Stream<Integer> stream = StreamSupport.stream(mySpliterator, false);
        //通过reduce操作对Stream中的元素进行求和
        int sum = stream.reduce(0, Integer::sum);
        //断言，与ints之和进行对比，检验自定义的Stream及Spliterator是否存在问题
        assert sum == Stream.of(ints).reduce(0, Integer::sum);
    }

    /**
     * 如上面的代码片段所示，通过StreamSupport.stream()方法创建了一个Stream，该Stream完全依赖于我们自定义的Spliterator而得到，串行计算通过验证并没有什么问题，
     * 下面我们来看一下它在并行流中是否可以正常运行呢？
     * <p>
     * 运行下面的代码，一切顺利，至此，关于Spliterator接口、接口方法，以及创建Stream的相关知识，相信大家已经有了一个比较清晰的认识了。
     */
    @Test
    public void test4() {
        Integer[] ints = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
        MySpliterator<Integer> mySpliterator = new MySpliterator<>(ints);
        //true代表并行
        Stream<Integer> stream = StreamSupport.stream(mySpliterator, true);
        //通过reduce操作对Stream中的元素进行求和运算
        int sum = stream.reduce(0, Integer::sum);
        //断言，与ints之和进行对比，检验自定义的Stream及Spliterator是否存在问题
        assert sum == Stream.of(ints).reduce(0, Integer::sum);
    }

    /**
     * 好了，下面就来验证一下iterate方法创建的Stream到底会不会进行子任务的拆分。
     */
    @Test
    public void test5() {
        //使用iterate创建Stream
        Stream<Long> stream = Stream.iterate(0L, l -> l + 1L).limit(1_000_000);
        //获取该Stream的Spliterator
        Spliterator<Long> spliterator = stream.spliterator();
        //尝试对其进行拆分
        Spliterator<Long> s1 = spliterator.trySplit();
        //尝试失败，拆分未遂，s1 == null
        assert s1 == null;
    }
}