package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 6.2.2 Collectors用法详解
 * Collectors可以看作是Collector的工厂类，其为我们提供了非常多的内建Collector的方法，前文中使用Stream的collect操作也是直接使用Collectors为我们提供的工厂方法。
 * 本节将逐一学习和掌握Collectors所提供的每一个方法(方法比较多)，笔者根据自己的方式将Collectors提供的方法进行了分类(主要是基于方法名和用途)，这样有助于归纳和总结。
 * <p>
 * 首先，我们来看一下Collector接口的定义，Collector接口提供了5个方法，分别用于发挥不同的作用。
 * public interface Collector<T, A, R> {
 * Supplier<A> supplier();
 * BiConsumer<A, T> accumulator();
 * BinaryOperator<A> combiner();
 * Function<A, R> finisher();
 * Set<Characteristics> characteristics();
 * }
 * <p>
 * Collector是一个泛型接口，有三个泛型参数分别是T、A、R，其所代表的定义分别如下。
 * 1.T代表着Stream元素的数据类型，比如Production、String、Integer等。
 * 2.A代表着累加器的数据类型，在Stream collect方法源码中甚至将其命名为容器，通常情况下，经过了collection操作之后的部分数据会被存放在该累加器中或者容器中。
 * 3.R代表着collect方法最终返回的数据类型。
 * <p>
 * 了解了Collector接口的三个泛型参数之后，我们再来看看在Collector中， 5个接口方法将分别用来做什么？
 * 1.Supplier<A> supplier():该方法将返回一个类型为A的Supplier，该方法会创建一个元素容器，该容器在accumulator()方法中将会被用到，主要用于收集累加器计算的数据结果。
 * 2.BiConsumer<A, T> accumulator():累加器方法是比较关键的方法，该方法会部分（在并行流中）运算或者全部计算（在串行流中）Stream流经的元素，并且将其存入suplier方法构造出来的容器中。
 * 3.BinaryOperator<A> combiner():该方法主要用于在并行流中进行结果的整合操作，请大家思考一下，在并行流中，每一个子线程都在执行部分数据的累加器方法，最后的结果该如何自处呢？
 * 当然是需要将其进行整合（分而治之，Fork Join的思想），那么该方法的泛型参数与supplier()方法一致也就很容易理解了。
 * 4.Function<A, R> finisher():当所有的计算完成之后，该方法将被用于做进一步的transformation操作，比如将int类型转换为long类型，
 * 同时该方法也是整个Collector接口在Stream collect操作中最后一个被调用的方法。
 * 5.Set<Characteristics> characteristics():该方法主要用于定义Collector的特征值，包含了CONCURRENT、UNORDERED和IDENTITY_FINISH三个类型，
 * 在本章的自定义Collector部分会再次为大家详细讲解这部分的内容。
 * <p>
 * 在了解了Collector泛型接口和Collector的5个方法之后，我们大概也清晰了Collector接口在Stream的collect操作中将被如何使用，
 * 由于Stream既可以是并行流也可以是串行流，因此Collector接口方法的使用也包含了两种不一样的方式。Collector接口方法在串行流中的执行过程如图6-8所示。
 * 1).开始
 * 2).A container = collector.supplier().get();//调用supplier方法创建累加器容器
 * 3).Stream中是否有元素？
 * 4).如果有，在Stream中获取新的元素数据
 * 5).调用collector.accumulator()方法的accept方法对Stream中的元素进行计算，并将其存入supplier方法创建的container中
 * 6).继续执行第三步
 * 7).如果没有，调用collector的finisher方法得到最终结果R
 * 8).返回最终结果
 * 9).结束
 * <p>
 * 如图6-8所示的是在串行流Stream中进行collect操作时的Collector接口方法执行过程分解，其中combiner方法将不会被使用到，
 * 因为不存在子线程子任务数据的合作动作，所有的操作将直接由单线程来完成，关于这一点我们在讲述accumulator()方法时已经介绍过了。
 * <p>
 * Collector接口在并行流中的执行过程就显得有点复杂了，毕竟涉及了子任务的拆分、数据结果的合并等操作，
 * 不过幸好Java的Stream为我们屏蔽了这些动作，开发人员在不理解其内部原理的情况下也可以运用自如，这并不是什么大问题。
 * Collector接口方法在并行流中的执行过程如图6-9所示。
 * 1).主线程开始
 * 2).拆分子任务
 * 3).子任务开始
 * 4).A container = collector.supplier().get();//调用supplier方法创建累加器容器
 * 5).Stream中是否有元素？
 * 6).如果有，在Stream中获取新的元素数据
 * 7).调用collector.accumulator()方法的accept方法对Stream中的元素进行计算，并将其存入supplier方法创建的container中
 * 8).继续执行第三步
 * 9).如果没有，调用collector的finisher方法得到最终结果R
 * 10).返回最终结果
 * 11).子任务结束
 * 12).调用collector的combiner方法将子任务结果合并
 * 13).调用collector的finisher方法得到最终结果R
 * 14).返回最终结果
 * 15).主线程结束
 */
public class CollectorExample2 {

    Stream<Production> stream = Stream.of(
            new Production("T-Shirt", 43.34d),
            new Production("cloth", 99.99d),
            new Production("shoe", 123.8d),
            new Production("hat", 26.5d),
            new Production("cloth", 199.99d),
            new Production("shoe", 32.5d)
    );

    /**
     * 1.Collectors.averaging类型方法
     * Collectors提供了三个与averaging有关的操作方法，具体如下。
     * 1).averagingInt(ToIntFunction<? super T> mapper):将Stream的元素T转换为int类型，然后计算其平均值。
     * 2).averagingLong(ToLongFunction<? super T> mapper):将Stream的元素T转换为long类型，然后计算其平均值。
     * 3).averagingDouble(ToDoubleFunction<? super T> mapper):将Stream的元素T替换为double类型，然后计算其平均值。
     */
    @Test
    public void averagingDouble() {
        //获取所有商品价格的平均值，使用averagingDouble方法
        Double averagePrice = stream.collect(Collectors.averagingDouble(Production::getPrice));
        System.out.println(averagePrice);
    }

    @Test
    public void averagingInt() {
        //获取所有商品价格的平均值，使用averagingInt方法
        Double averagePrice = stream.collect(Collectors.averagingInt(p -> (int) p.getPrice()));
        System.out.println(averagePrice);
    }

    @Test
    public void averagingLong() {
        //获取所有商品价格的平均值，使用averagingLong方法
        Double averagePrice = stream.collect(Collectors.averagingLong(p -> (long) p.getPrice()));
        System.out.println(averagePrice);
    }

    /**
     * 2.Collectors.collectingAndThen方法
     * 该方法的主要作用是对当前Stream元素经过一次Collector操作之后再次进行transformation操作，
     * 来看一个示例，假如我们对所有商品的价格进行平均值的聚合操作之后再进行币种的换算，
     * 比如将人民币转换为越南盾（1元人民币=3264.4791越南盾），那么示例代码如下：
     */
    @Test
    public void collectingAndThen() {
        Double averagePriceByVND = stream.collect(
                //collectingAndThen方法需要两个参数，前者是一个Collector，后者是一个Function，下面对downstream的结果进行transformation运算
                Collectors.collectingAndThen(
                        //调用averagingDouble方法
                        Collectors.averagingDouble(Production::getPrice),
                        //lambda表达式
                        p -> p * 32.644791D
                )
        );
        System.out.println(averagePriceByVND);
    }

    /**
     * 3.Collectors.counting方法
     * counting方法所创建的Collector，其主要用于返回Stream中元素的个数，当Stream中没有任何元素时返回0，
     * counting方法在Stream collect操作中的效果实际上是等价于Stream的count方法，但是由于counting方法返回的是一个Collector，
     * 因此它可以应用于其他的Collectors方法中，比如collectingAndThen()方法。
     */
    @Test
    public void counting() {
        //使用Stream的collect操作，通过Collectors的counting方法返回Collector
        //assert stream.collect(Collectors.counting()) == 6;
        //上面的操作事实上等价于
        assert stream.count() == 6;
        //注意：上面的代码不能放到一起运行，原因在前文中已经讲解过了。
    }

    /**
     * 4.Collectors.mapping方法
     * mapping方法的方法签名为<T, U, A, R> Collector<T, ?, R> mapping(Function<? super T, ? extends U> mapper, Collector<? super U, A, R downstream)。
     * <p>
     * 结合前文学习的知识和方法的签名，我们可以有个大致的判断，首先Function函数将Stream中的类型为T的元素transformation成U类型，紧接着downstreamcollector将处理元素类型为U的Stream。
     * 下面通过一个例子进行说明，销售人员通常会根据商品的销量获得相应的提成，比如10个点的提成，这里借助于mapping方法来计算一下某销售人员的销售提成。
     * <p>
     * 通过源码分析其实不难得知，Function函数将会被应用于downstream collector的累加器accumulator方法中。
     * //mapping()方法部分源码
     * BiConsumer<A, ? super U> downstreamAccumulator = downstream.accumulator();
     * return new CollectorImpl<>(downstream.supplier(),
     * //function(mapper)将被应用于downstream collector的累加器方法中
     * (r, t) -> downstreamAccumulator.accept(r, mapper.apply(t)),
     * downstream.combiner(),
     * downstream.finisher(),
     * downstream.characteristics()
     * );
     */
    @Test
    public void mapping() {
        double deductInComing = stream.collect(
                Collectors.mapping(
                        //通过Function，计算每一件商品的提成所得
                        p -> p.getPrice() * 0.1,
                        //所有的商品提成所得将被累加在一起
                        Collectors.summingDouble(Double::doubleValue)
                )
        );
        //其实上面的代码也完全可以不通过collect操作计算得到
        double deductInComing2 = stream.map(p -> p.getPrice() * 0.1).mapToDouble(Double::doubleValue).sum();
    }

    /**
     * 5.Collectors.joining方法
     * Collectors的joining方法主要用于将Stream中的元素连接成字符串并且返回，Collectors的joining()方法如有下三种重载形式。
     * 1).joining():将Stream中的元素连接在一起，中间没有任何符号对其进行分隔。
     * 2).joining(CharSequence delimiter):将Stream中的元素连接在一起，元素与元素之间将用delimiter进行分隔。
     * 3).joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix):将Stream中的元素连接在一起，元素与元素之间将用delimiter进行分隔；
     * 除此之外，最后的返回结果还将会被prefix与suffix包裹。
     */
    @Test
    public void joining() {
        //执行Stream的collect操作
        String result = stream.collect(
                //调用mapping方法，创建一个Collector
                Collectors.mapping(p -> p.getName(),
                        //joining方法将返回一个Collector，用于将Stream中的元素连接在一起
                        Collectors.joining())
        );
        assert result.equals("T-Shirtclothshoehatclothshoe");
    }

    @Test
    public void joining2() {
        String result = stream.collect(
                Collectors.mapping(p -> p.getName(),
                        //joining方法将返回一个Collector，用于将Stream中的元素连接在一起，元素之间会被#分隔
                        Collectors.joining("#"))
        );
        //System.out.println(result);
        assert result.equals("T-Shirt#cloth#shoe#hat#cloth#shoe");
    }

    @Test
    public void joining3() {
        String result = stream.collect(Collectors.mapping(p -> p.getName(), Collectors.joining(",", "(", ")")));
        System.out.println(result);
        assert result.equals("(T-Shirt,cloth,shoe,hat,cloth,shoe)");
    }

    /**
     * 6.Collectors.summing方法
     * Collectors提供了三个与summing的有关操作方法，具体如下。
     * 1).summingInt(ToIntFunction<? super T> mapper):将Stream的元素T转换为int类型，然后对所有值求和。
     * 2).summingDouble(ToDoubleFunction<? super T> mapper):将Stream的元素T转换为double类型，然后对所有值求和。
     * 3).summingLong(ToLongFunction<? super T> mapper):将Stream的元素T转换为long类型，然后对所有值求和。
     */
    @Test
    public void summingInt() {
        //获取所有商品价格的总和，使用summingInt方法
        Integer result = stream.collect(Collectors.summingInt(p -> (int) p.getPrice()));
        System.out.println(result);
    }

    @Test
    public void summingDouble() {
        //获取所有商品价格的总和，使用summingDouble方法
        Double result = stream.collect(Collectors.summingDouble(p -> p.getPrice()));
        System.out.println(result);
    }

    @Test
    public void summingLong() {
        //获取所有商品价格的总和，使用summingLong方法
        long result = stream.collect(Collectors.summingLong(p -> (long) p.getPrice()));
        System.out.println(result);
    }

    /**
     * 7.Collectors获取最大值最小值的方法
     * Collectors提供了可以获取Stream中最大元素和最小元素的Collector，具体如下所示。
     * 1).maxBy(Comparator<? super T> comparator):根据Comparator获取Stream中最大的那个元素。
     * 2).minBy(Comparator<? super T> comparator):根据Comparator获取Stream中最小的哪个元素。
     */
    @Test
    public void maxBy() {
        //根据商品价格，获取最贵的商品
        Optional<Production> opt = stream.collect(Collectors.maxBy((o1, o2) -> (int) (o1.getPrice() - o2.getPrice())));
        opt.ifPresent(p -> System.out.println(p.getName()));
    }

    @Test
    public void minBy() {
        //根据商品价格，获取最便宜的商品
        Optional<Production> opt = stream.collect(Collectors.minBy((o1, o2) -> (int) (o1.getPrice() - o2.getPrice())));
        opt.ifPresent(p -> System.out.println(p.getName()));
    }

    /**
     * 8.Collectors.summarizing方法
     * 前文分别学习了Collectors的averaging和summing，如何使用counting方法创建对应用途的Collector。
     * 本节将要学习的summarizing方法创建的Collector则会集averaging、summing、counting于一身，并且提供了更多额外的方法，同样，summarizing也提供了三种汇总方式。
     * 1).summarizingInt(ToIntFunction<? super T> mapper):将Stream元素转换为int类型，并且进行汇总运算，该Collector的返回值为IntSummaryStatistics类型。
     * 2).summarizingLong(ToLongFunction<? super T> mapper):将Stream元素转换为long类型，并且进行汇总运算，该Collector的返回值为LongSummaryStatistics类型。
     * 3).summarizingDouble(ToDoubleFunction<? super T> mapper):将Stream元素转换为double类型，并且进行汇总运算，该Collector的返回值为DoubleSummaryStatistics类型。
     * <p>
     * 无论是IntSummaryStatistics、DoubleSummaryStatistics还是LongSummaryStatistics都提供了比较丰富的汇总内容，上述程序片段运行的结果具体如下。
     * IntSummaryStatistics{count=6, sum=522, min=26, average=87.000000, max=199}
     */
    @Test
    public void summarizingInt() {
        //汇总商品的价格信息，先将production转换为int类型
        IntSummaryStatistics stat = stream.collect(Collectors.summarizingInt(p -> (int) p.getPrice()));
        System.out.println(stat);
    }

    @Test
    public void summarizingDouble() {
        //汇总商品的价格信息，先将production转换为double类型
        DoubleSummaryStatistics stat = stream.collect(Collectors.summarizingDouble(p -> (int) p.getPrice()));
        System.out.println(stat);
    }

    @Test
    public void summarizingLong() {
        //汇总商品的价格信息，先将production转换为long类型
        LongSummaryStatistics stat = stream.collect(Collectors.summarizingLong(p -> (int) p.getPrice()));
        System.out.println(stat);
    }

    /**
     * 9.Collectors输出到其他容器的方法
     * Stream通过若干intermediate操作之后，可以执行collect操作将Stream中的元素输出汇总至其他容器中，比如Set、List、Map。
     * (1)toSet()：将Stream中的元素输出到Set中
     * (2)toList()：将Stream中的元素输出到List中
     * (3)toMap()：将Stream中元素输出到Map中，Collectors提供了toMap的三种重载形式，具体如下。
     * (4)其他容器
     * 1).toCollection(Supplier<C> collectionFactory)
     * 2).toConcurrentMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
     * 3).toConcurrentMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction)
     * 4).toConcurrentMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction, Supplier<M> mapSupplier)
     * <p>
     * 上述几个方法比较简单，与本节中介绍过的方法非常类似，这里就不再赘述了，读者可以结合之前的学习方法自行学习。
     */
    @Test
    public void toSet() {
        Set<String> set = stream.map(Production::getName).collect(Collectors.toSet());
        System.out.println(set);
    }

    @Test
    public void toList() {
        List<String> list = stream.map(Production::getName).collect(Collectors.toList());
        System.out.println(list);
    }

    /**
     * 1).toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper):
     * 该方法需要两个Function参数，前者应用于map key的mapper操作，后者应用于value的mapper操作，下面我们来看一个例子。
     */
    @Test
    public void toMap() {
        //String[] 类型的Stream
        Stream<String[]> stream = Stream.of(new String[][]{{"Java", "Java Programming"}, {"C", "C Programming"}, {"Scala", "Scala Programming"}});
        //collect操作
        Map<String, String> result = stream.collect(
                //toMap静态方法，将元素聚合为Map
                Collectors.toMap(s -> s[0], s -> s[1])
        );
        System.out.println(result);
    }

    /**
     * 2).toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction)：
     * 该toMap方法就显得有些复杂了，但是仔细看方法声明可以发现，前两个参数仍旧应用于Map返回值的key和value之中，BinaryOperator主要用于解决当Key值出现冲突时的merge方法，
     * 前文中曾经介绍关于grouping的方法，该方法创建的Collector也能实现类似的功能，代码如下。
     */
    @Test
    public void toMap2() {
        //执行Stream的collect操作
        Map<String, List<Production>> result = stream.collect(
                //Collectors的toMap静态方法
                Collectors.toMap(
                        //用商品名作为Key，将Production存入List之中
                        Production::getName, Arrays::asList,
                        //将结果整合在一个List中
                        (productions, productions2) -> {
                            List<Production> mergeResult = new ArrayList<>();
                            mergeResult.addAll(productions);
                            mergeResult.addAll(productions2);
                            return mergeResult;
                        }
                ));
        System.out.println(result);
    }

    /**
     * 3).toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction, Supplier<M> mapSupplier):
     * 与上一个toMap方法类似，只不过多了一个可以指定创建返回Map类型的Supplier，前面两个toMap方法返回的都是HashMap的Map实现，在这个toMap方法中，开发者可以显式指定Map类型，比如TreeMap、ConcurrentHashMap等。
     */
    @Test
    public void toMap3() {
        //执行Stream的collect操作
        Map<String, List<Production>> result = stream.collect(
                //Collectors的toMap静态方法
                Collectors.toMap(
                        //用商品名作为Key,将Production存入List之中
                        Production::getName, Arrays::asList,
                        //将结果整合在一个List中
                        (productions, productions2) -> {
                            List<Production> mergeResult = new ArrayList<>();
                            mergeResult.addAll(productions);
                            mergeResult.addAll(productions2);
                            return mergeResult;
                        }, TreeMap::new
                ));
        System.out.println(result);
    }

    /**
     * 10.Collectors.partitioningBy方法
     * 该方法会将Stream中元素分为两个部分，以Map<Boolean, ?>的形式作为返回值，Key为True代表一部分，Key为False代表另外一部分。
     * partitioningBy有两个重载方法，具体如下所示。
     * 1).partitioningBy(Predicate<? super T> predicate):根据Predicate的判断，将Stream中的元素分为两个部分，最后的返回值为Map<Boolean, List<?>>，
     * 下面的示例在前文中已经有过介绍了。
     * 2).partitionBy(Predicate<? super T> predicate, Collector<? super T, A, D> downstream):
     * 相较于前一个partitioningBy方法，该重载方法就灵活强大许多了，比如，我们可以对每一个分区的元素再次进行其他Collector的操作运算。
     */
    @Test
    public void partitioningBy() {
        //执行Stream的collect操作
        Map<Boolean, List<Production>> result = stream.collect(
                //根据价格是否大于100将商品一分为二
                Collectors.partitioningBy(p -> p.getPrice() > 100)
        );
        System.out.println(result);
    }

    @Test
    public void partitioningBy2() {
        //①结果是Map<Boolean, Set<Production>>而不是Map<Boolean, List<>>
        Map<Boolean, Set<Production>> result = stream.collect(Collectors.partitioningBy(p -> p.getPrice() > 100, Collectors.toSet()));
        System.out.println(result);
    }

    @Test
    public void partitioningBy3() {
        //②先根据价格高低进行分区，然后计算每个分区中商品的价格总和
        Map<Boolean, Double> result = stream.collect(Collectors.partitioningBy(p -> p.getPrice() > 100, Collectors.summingDouble(Production::getPrice)));
        System.out.println(result);
    }

    @Test
    public void partitioningBy4() {
        //③先根据价格高低进行分区，然后计算每一个分区中商品价格的平均值
        Map<Boolean, Double> result = stream.collect(Collectors.partitioningBy(p -> p.getPrice() > 100, Collectors.averagingDouble(Production::getPrice)));
        System.out.println(result);
    }

    /**
     * 11.Collectors.groupingBy方法
     * groupingBy方法类似于关系型数据库中的分组操作，其主要作用是根据classifier（分类器）对Stream中元素进行分组，groupingBy方法在Collectors中提供了如下几种重载形式。
     * <p>
     * 除此之外，Collectors还提供了其他三个groupingByConcurrent的重载形式，返回结果为线程安全的、支持并发的Map实现ConcurrentHashMap，
     * 其具体用法和原理与本节中介绍的三个重载方法类似，这里将不再赘述。
     * 1).groupingBy(Function<? super T, ? extends K> classifier):根据分类器对Stream中的元素进行分组，返回结果类型为：Map<K, List<T>。
     */
    @Test
    public void groupingBy() {
        Map<String, List<Production>> result = stream.collect(Collectors.groupingBy(Production::getName));
        System.out.println(result);
    }

    /**
     * 2).groupingBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream):
     * 首先根据分类器函数对Stream中的元素进行分组，然后将其交由另外一个Collector进行运算操作。
     */
    @Test
    public void groupingBy2() {
        //分组的结果是Map<String, Set<T>>，而不再是Map<String, List<T>>
        Map<String, Set<Production>> result = stream.collect(Collectors.groupingBy(Production::getName, Collectors.toSet()));
        System.out.println(result);
    }

    /**
     * 3).groupingBy(Function<? super T, ? extends K> classifier, Supplier<M> mapFactory, Collector<? super T, A, D> downstream):
     * 该方法与上一个方法类似，只不过多了一个提供构造器返回Map类型的Supplier，在前两个groupingBy方法中返回的Map为HashMap，在该方法中，开发者可以指定Map的其他实现类。
     */
    @Test
    public void groupingBy3() {
        Map<String, Set<Production>> result = stream.collect(
                Collectors.groupingBy(
                        Production::getName,
                        //指定Map的构造Supplier
                        TreeMap::new,
                        Collectors.toSet()
                )
        );
        System.out.println(result);
    }

    /**
     * 12.Collectors.reducing方法
     * 与Stream的reduce操作非常类似，Collectors的reducing方法也将创建一个用于对Stream中元素进行reduce计算的Collector，
     * 该操作在Collectors中提供了三个重载方法，具体如下。
     * 1).reducing(BinaryOperator<T> op):给定一个BinaryOperator函数式接口，对Stream中的每一个元素进行计算，但是该reducing创建的Collector其返回值将是一个类型与Stream中元素类型一致的Optional，
     * 下面来看一个简单的例子，获取在商品Stream中价格最贵的那个商品。
     */
    @Test
    public void reducing() {
        Optional<Production> opt = stream.collect(Collectors.reducing((p1, p2) -> p1.getPrice() > p2.getPrice() ? p1 : p2));
        opt.ifPresent(System.out::println);
    }

    /**
     * 下面的代码与上面的代码等价
     */
    @Test
    public void reducing2() {
        Optional<Production> opt = stream.collect(Collectors.reducing(
                BinaryOperator.maxBy(Comparator.comparingDouble(Production::getPrice))
        ));
        opt.ifPresent(System.out::println);
    }

    /**
     * 2).reducing(T identity, BinaryOperator<T> op):该方法的作用与上面的reducing类似，只不过增加了一个identity的参数，
     * 该参数会纳入BinaryOperator函数的运算之中，除此之外当该Stream为空时，reducing将会直接返回该identity。
     */
    @Test
    public void reducing3() {
        Production book = stream.collect(Collectors.reducing(new Production("Book", 279.9), (p1, p2) -> p1.getPrice() > p2.getPrice() ? p1 : p2));
        //将会输出identity，也就是我们在reducing方法中创建的Production，原因是Stream中Book的价格最高
        //如果Stream是empty的，那么同样会直接返回new Production("Book", 279.9)
        System.out.println(book);
    }

    /**
     * 3).reducing(U identity, Function<? super T, ? extends U> mapper, BinaryOperator<U> op):
     * 前两个reducing方法只能返回与Stream元素类型一样的结果，或者Optional，该重载方法允许开发者返回不同于其他类型的结果，因为有了mapper函数的加持。
     */
    @Test
    public void reducing4() {
        Comparator<Double> comparing = Comparator.comparing(Double::doubleValue);
        Double highestPrice = stream.collect(Collectors.reducing(0.0D, Production::getPrice, BinaryOperator.maxBy(comparing)));
        //输出最贵的商品价格
        System.out.println(highestPrice);
    }
}