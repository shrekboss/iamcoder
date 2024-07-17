package org.coder.concurrency.programming.juc._6_java_stream;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 当然，即使IntStream不提供如此方便的的方法也并不能影响什么，基于Stream<Integer>实现类似的功能也并不是一件很困难的事情。
 * <p>
 * 再来说说性能和内存占用。Stream<Integer>流中的元素都是Integer类型，也就是int的引用类型，
 * Integer类型数据之所以能够进行与int类型一样的数学计算，是因为其经历了拆箱的过程（unbox）；
 * 相对应的，int类型可以被放置在诸如List<Integer>的容器中也主要得益于封箱（box），这一切都是Java程序编译器帮我们默默处理的。
 * 也就是说，Stream<Integer>中的每一个元素要进行计算首先得经历一次拆箱的性能损耗，
 * 假设一次拆箱的CPU时间为S个单位时间，那么在同等元素数量下，IntStream要比Stream<Integer>节约NS个单位时间。
 * <p>
 * 下面通过一个基准测试实例来对比一下Integer和Stream<Integer>对所有元素进行累加的性能开销，结果将会更加直观和客观。
 * <p>
 * 运行下面的基准测试，我们可以看到IntStream的计算效率至少要高出Stream<Integer>60%。
 * Benchmark                                              Mode  Cnt  Score   Error  Units
 * StreamIntegerVsIntStream.intStreamReduce               avgt   20  0.841 ± 0.066  us/op
 * StreamIntegerVsIntStream.streamIntegerReduce           avgt   20  1.184 ± 0.098  us/op
 * StreamIntegerVsIntStream.streamIntegerUnboxThenReduce  avgt   20  0.848 ± 0.054  us/op
 * <p>
 * 通过本节的讲解，相信读者应该清晰地知道了即便Stream<Integer>可以很好地完成操作，还要提供NumericStream的原因。
 */
@Warmup(iterations = 20)
@Measurement(iterations = 20)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class StreamIntegerVsIntStream {
    //定义Stream<Integer>
    private Stream<Integer> integerStream;
    //定义IntStream
    private IntStream intStream;

    //注意Level必须是Invocation，原因是Stream只能操作一次，前文中已经解释过
    @Setup(Level.Invocation)
    public void init() {
        this.integerStream = IntStream.range(0, 100).boxed();
        this.intStream = IntStream.range(0, 100);
    }

    //Stream<Integer>所有的操作都需要经历拆箱和封箱的过程
    @Benchmark
    public void streamIntegerReduce(Blackhole hole) {
        int result = this.integerStream.map((Integer i) -> i * 10).reduce(0, (Integer a, Integer b) -> a + b);
        hole.consume(result);
    }

    //Stream<Integer>在进行操作之前先主动拆箱，然后再进行其他的操作
    @Benchmark
    public void streamIntegerUnboxThenReduce(Blackhole hole) {
        int result = integerStream.mapToInt(Integer::intValue).map((int i) -> i * 10).reduce(0, (int a, int b) -> a + b);
        hole.consume(result);
    }

    //所有的操作都是基于基本类型int的
    @Benchmark
    public void intStreamReduce(Blackhole hole) {
        int result = intStream.map((int i) -> i * 10).reduce(0, (int a, int b) -> a + b);
        hole.consume(result);
    }

    public static void main(String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder().include(StreamIntegerVsIntStream.class.getSimpleName()).build();
        new Runner(opt).run();
    }
}