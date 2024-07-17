package org.coder.concurrency.programming.juc._6_java_stream;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 6.1.2 Stream之Intermediate操作
 * 在了解了如何通过不同的方式（source）获取Stream之后，我们接下来就来学习Stream的操作。
 * Stream主要分为两种类型，Intermediate和Terminal。
 * filter、sorted、map之类的操作被称为Intermediate操作，这类操作的结果都是一个全新的Stream类型。
 * 多个Intermediate操作构成了一个流水线（pipeline），除此之外，Intermediate操作的执行都是通过lazy的方式，直到遇到最后的Terminal操作。
 * 本节将学习Java Stream中所有的Intermediate操作方法。
 * <p>
 * Java的Stream提供了非常丰富的Intermediate方法，如表6-1所示。
 * 本节将对表中的列举的内容逐一进行讲解。
 * 方法
 * distinct	通俗地将就是去重，distinct操作之后将会返回一个没有相同元素的Stream
 * filter	过滤操作，执行了filter操作之后将会返回一个满足predicate条件判断的Stream
 * limit	对Stream执行截断操作，类似于我们操作MySQL查询时的limit关键字作用。假设针对一个元素序列个数为10的Stream执行了limit(3)操作，那么将会返回一个全新的元素序列并且序列个数为3，其余元素将被截断
 * map		对元素序列中的每一个元素都执行函数运算，并且返回一个运算之后的全新Stream
 * skip		丢弃前n个元素，并且返回一个全新的Stream，该Stream中将不在包含被skip操作丢弃的元素。如果n大于当前Stream中元素个数，则该操作相当于对Stream执行了一次清空操作
 * peek		对Stream中所有的元素都执行consume操作，并且返回一个与原Stream类型、元素数量完全一样的全新Stream，该操作并不会使元素数据、类型发生任何改变，看起来更像是对其他Intermediate操作的debug操作
 * sorted	对Stream执行sorted操作，会返回一个经过自然排序的全新Stream，注意，Stream的元素必须是Comparable的子类，否则将不允许执行该操作
 * flatMap	map操作是对Stream元素一对一的操作，每一个Stream的元素经过map函数计算对应于另一个Stream的元素；
 * flatMap提供了一种一对多的操作模式，它会将元素类型为Stream<R>的Stream<Stream<R>>扁平化为Stream<R>并且产生一个全新的Stream
 */
public class StreamExample3 {

    /**
     * 在学习Stream的Intermediate操作之前，我们首先要明白的是，一旦某个Stream执行了Intermediate操作，或者已经被关闭（执行了Terminal操作），将无法再次被操作。
     * 下面通过一个简单的示例为大家演示一下。
     * <p>
     * 运行下面的程序片段，会看到错误信息（如图6-1所示），根据错误信息我们不难看出Stream不允许对同一个Stream执行一次以上的操作。
     */
    @Test
    public void test() {
        //这里通过Stream的of方法创建了一个Stream，我们将其称为sourceStream
        Stream<Integer> sourceStream = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        //在sourceStream上执行了map操作（该操作为Intermediate操作），并且返回一个新的stream mapStream
        Stream<Integer> mapStream = sourceStream.map(i -> i * 2);
        //再次对source stream执行foreach操作（该操作为Intermediate操作）
        sourceStream.forEach(System.out::println);
    }

    /**
     * 1.distinct 去重操作
     * distinct操作将会去除Stream中重复的元素，经此方法的执行之后将会返回一个没有重复数据的Stream。
     * <p>
     * distinct方法的使用非常简单，运行下面的代码片段，会发现输出中数字1只有一个，相同的其他数字1都被去重。
     * 那么distinct是根据什么进行去重操作的呢？下面通过一个示例来进行分析。
     * <p>
     * 上面的代码中，在Stream的元素序列中很明显存在着3个Entity的对象实例，即使属性value相同，它们彼此也是不同的实例，因此对Stream执行distinct操作后，元素的数量肯定不会发生改变。
     * 但是当我们基于属性value重写Entity的equals()和hashcode()方法之后，相同value的元素将会被去重，下面在Entity中增加如下代码。
     * <p>
     * 再次运行程序，会发现执行了distinct的Entity Stream仅剩下Value为Java和Scala的两个元素，因此我们可以肯定，去重操作的依据为对象的equals()方法逻辑。
     */
    @Test
    public void distinct() {
        Stream<Integer> stream = Stream.of(1, 1, 2, 3, 4);
        //stream.forEach(System.out::println);
        //执行了distinct操作之后，相同的元素将只保留一个
        stream.distinct().forEach(System.out::println);
    }

    @Test
    public void distinct2() {
        Stream<Entity> stream = Stream.of(new Entity("Java"), new Entity("Scala"), new Entity("Java"));
        stream.distinct().forEach(System.out::println);
    }

    /**
     * 2.filter操作
     * Stream的filter操作需要使用Predicate的接口实现作为入参，不过在Java 8 中，我们更喜欢使用lambda表达式或者静态推导的方式。
     * Stream<T> filter(Predicate<? super T> predicate);
     * Predicate是一个函数式接口（FunctionalInterface），在本书中笔者并不打算讲解函数式接口、lambda表达式、静态推导等Java的新语法，请读者自行参阅其他资料进行学习。
     * <p>
     * 对Stream执行了filter操作之后，将会返回一个全新的Stream，该Stream的元素序列将只包含满足Predicate条件的元素，那些条件不满足的将会被过滤掉，而不会传递到下一个Stream中。
     */
    @Test
    public void filter() {
        Stream.of(1, 2, 3, 4, 5, 6, 7, 8)
                //过滤掉记述，只保留偶数数字
                .filter(i -> i % 2 == 0).forEach(System.out::println);
    }

    /**
     * 3.limit操作
     * limit操作是一个对Stream执行截断的操作，类似于我们操作MySQL查询时的limit关键字作用。
     * 假设针对一个元素序列个数为10的Stream执行了limit(3)操作，那么结果将会返回一个全新的元素序列并且其元素序列个数为3，其余元素将被截断，我们来看一下具体的例子。
     * <p>
     * 假如Stream的元素序列个数为5，那么如果针对该Stream执行limit(10)这样的操作会怎样呢？会不会出现错误呢？
     * 答案是不会。这样的操作不会起到任何截断的效果，仍旧会返回一个全新的Stream，而且并不会引起错误。
     */
    @Test
    public void limit() {
        Stream.of(1, 2, 3, 4, 5, 6, 7, 8)
                //截断Stream，只保留前3个元素
                .limit(3).forEach(System.out::println);
    }

    /**
     * 4.map操作
     * 在Stream中，map操作是使用非常广泛的操作之一，我们可以借助map操作对元素进行增强运算、投影运算，甚至类型转换等操作。
     * 一个Stream在经过了map操作之后将会返回一个全新的Stream，并且每一个元素都将会被map方法所传入的Function执行。
     * 下面是map方法的声明。
     * <R> Stream<R> map(Function<? super T, ? extends R> mapper);
     * 假设在某个Stream的元素序列中有若干个int类型的数字，如果想要得知该int类型的数字是几位数，那么我们可以借助于map操作来获得，代码如下所示。
     * <p>
     * 运行上面的代码，我们将会看到map不仅对每一个元素进行了运算，而且还对数据类型也执行了转换，产生了一个全新的类型Stream。
     */
    @Test
    public void map() {
        Stream.of(2, 4535, 345, 565667, 2424, 565)
                //map运算将返回Stream<int[]>类型的Stream
                .map(i -> new int[]{i, String.valueOf(i).length()}).forEach(entry -> System.out.printf("%d is %d digits.\n", entry[0], entry[1]));
    }

    /**
     * 5.skip操作
     * 对Stream的skip操作与limit类似，但是其作用却是相反的，limit会对Stream执行截断操作，只保留前n个元素，而skip操作会跳过（丢弃）n（指定数量）个元素，并且返回一个全新的Stream。
     * 如果n大于当前Stream元素的个数，那么该操作就相当于是对Stream元素执行了一次清空操作。
     */
    @Test
    public void skip() {
        IntStream.range(0, 10).skip(5).forEach(System.out::println);
        IntStream.range(0, 10).skip(10).forEach(System.out::println);
    }

    /**
     * 6.peek操作
     * 对Stream中所有的元素都执行consume操作，并且返回一个与原Stream类型、元素数量完全一样的全新Stream。
     * 该操作并不会使元素数据、类型产生任何改变，看起来更像是一个对其他intermediate操作的debug操作。
     * 在Storm的Trident中对实时数据流也有类似于debug的操作，其方法名也叫peek。
     * 下面我们来看一个简单的例子。
     * <p>
     * 运行上面的程序片段，Stream在执行了peek后虽然会产生一个全新的Stream，但并不会使元素类型和个数发生任何改变。
     */
    @Test
    public void peek() {
        //创建一个IntStream，半开半闭区间
        int result = IntStream.range(0, 10)
                //执行peek操作，输出Stream的每一个元素，并且创建一个数量和类型完全一样的全新Stream
                .peek(System.out::println)
                //对Stream执行map操作，每一个元素都会被乘以2
                .map(i -> i * 2)
                //执行peek操作，输出Stream的每一个元素，并且创建一个数量和类型完全一样的全新Stream
                .peek(System.out::println)
                //对Stream执行filter操作，过滤掉不满足条件的数据，并且返回一个全新的Stream
                .filter(i -> i > 10)
                //执行peek操作，输出Stream的每一个元素，并且创建一个数量和类型完全一样的全新Stream
                .peek(System.out::println)
                //对Stream中的元素执行sum操作，该操作是一个Terminal操作
                .sum();
        System.out.println("result:" + result);
//		IntStream.range(0,10).peek(System.out::println).forEach(System.out::println);
    }

    /**
     * 7.sorted操作
     * 对Stream执行sorted操作，会返回一个经过自然排序的全新Stream，下面来看一段示例代码片段。
     * <p>
     * 在使用排序操作时，需要注意的是，我们无法针对一个非Comparable子类进行排序，如果对一个非Comparable子类进行排序则会引起错误。
     * 运行上面的代码片段将会出现Entity无法转换为Comparable类型的转换异常。
     * <p>
     * 解决上面这样的问题相信并不难，在这里笔者将不再赘述。除此之外，sorted操作还提供了一个重载方法。
     * 如果你不想让Entity实现Comparable接口，则可以借助这种传入Comparator接口的方式来实现排序功能。
     */
    @Test
    public void sorted() {
        Stream.of(2, 4535, 345, 565667, 2424, 565).sorted().forEach(System.out::println);
        Stream.of(new Entity("Java"), new Entity("Scala"), new Entity("Java"))
                //执行sorted操作，并且传入Comparator的实现
                .sorted(Comparator.comparing(o -> o.getValue())).forEach(System.out::println);
    }

    /**
     * 8.flatMap操作
     * 在Stream序列元素中，数据类型可以是任意类型，那么Stream的数据元素类型是否可以是其他的Stream呢？
     * 这当然是可行的，map操作是对Stream元素进行一对一的操作，每一个Stream的元素经过map函数计算之后对应于另一个Stream的元素；
     * flatMap提供了一种一对多的操作模式，它会将类型为Stream<R>的Stream<Stream<R>>扁平化为Stream<R>并且产生一个全新的Stream。
     * <p>
     * 那么，此刻如果想让Stream<Stream<Integer>>的元素数据都增大10倍又该如何操作呢？
     * 我们进一步对其进行操作。
     * <p>
     * 运行上面的程序，你会发现控制台的输出结果仍然是针对Stream的输出，而不是针对数据的输出。
     * <p>
     * 如何修改才能将我们期望的数据结果全部输出到控制台呢？其实在foreach操作中同样要执行foreach操作才可以，修改后的程序代码如下所示。
     * <p>
     * 这种嵌套的操作方式非常不优雅，代码的可读性也比较差，下面我们借助于flatMap的方式将Stream<Stream<Integer>>扁平化为Stream<Integer>，然后再进行其他的操作。
     */
    @Test
    public void flatMap() {
        //Stream.of(1,2,3,4,5,6)产生一个Stream<Integer>的Stream
        Stream<Stream<Integer>> newStream = Stream.of(1, 2, 3, 4, 5, 6)
                //经过map操作之后产生一个全新的Stream<Stream<Integer>>
                .map(i -> Stream.of(i, i * 2, i * i));
//		newStream.forEach(System.out::println);
//		newStream.map(m->m*10);这显然是行不通的，因为m此刻是一个Stream
        //我们需要在newStream的map操作中再次执行元素的map操作才能满足我们的要求
//		newStream.map(m->m.map(i->i*10)).forEach(System.out::println);
        newStream.map(m -> m.map(i -> i * 10)).forEach(m -> m.forEach(System.out::println));

        Stream<Integer> newStream2 = Stream.of(1, 2, 3, 4, 5, 6)
                //执行flatMap操作，返回一个Stream<Integer>
                .flatMap(i -> Stream.of(i, i * 2, i * i));
        newStream2.map(i -> i * 10).forEach(System.out::println);
    }

    /**
     * 在什么情况下我们才会创建一个Stream<Stream<R>>类型的Stream呢？
     * 比如，我们通过Files创建了Stream<String>（每一个元素为文本的一行记录），
     * 然后根据空格将每一行切割成每个单词的Stream<String>（每一个元素为一个单词），
     * 这个时候就可以使用flatMap操作一步到位地将每一个元素变为一个单词的Stream<String>而不是Stream<Stream<String>>。
     *
     * @throws IOException
     */
    @Test
    public void flatMap2() throws IOException {
        //创建path
        Path path = Paths.get("文件路径");
        //lines会返回一个元素为每一行文本内容的Stream<String>
        Files.lines(path, Charset.forName("UTF-8"))
                //flatMap操作会将每一行单词的Stream<String>扁平化为所有单词的Stream<String>
                .flatMap(line -> Arrays.stream(line.split("\\s+"))).forEach(System.out::println);
    }
}