package org.coder.concurrency.programming.juc._6_java_stream;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * 在了解了reduce操作运算在并行流中的执行流程之后，我们很有必要对三者进行基准测试，对比普通的加法运算，在并行流中进行运算，性能会得到怎样的提升。
 * <p>
 * 你可能很难想象，并行流在基准测试对比中，性能表现是最糟糕的（这也是很多人对并行流使用错误的地方，导致资源占用很多，效率确实是最低下的一个）。
 * Benchmark                                           Mode  Cnt       Score       Error  Units
 * StreamVsParallelStream.calculateLongStream          avgt   20    4131.817 ±    10.074  us/op
 * StreamVsParallelStream.calculateNormal              avgt   20    2958.222 ±     2.928  us/op
 * StreamVsParallelStream.calculateParallelLongStream  avgt   20    7229.510 ±   128.612  us/op
 * StreamVsParallelStream.calculateParallelStream      avgt   20  401736.506 ± 81958.118  us/op
 * StreamVsParallelStream.calculateStream              avgt   20   84313.144 ±   931.134  us/op
 * 为了能够更加直观地看到三者在20个批次的基准测试下的性能对比，下面将基准测试的数据汇总成报表，如图6-11所示。
 * <p>
 * 这到底是怎么一回事呢？回顾6.1.4节的分析，类型拆箱封箱也会造成很多不必要的性能开销，因此我们直接使用LongStream进行Stream的创建，然后对其进行运算，并再次进行对比。
 * <p>
 * 再次运行基准测试，你会发现此刻并行流的表现将会更胜一筹。
 * <p>
 * 在Java 8 刚出来的那段时间，网上大量的文章都在批评并行流的效率如何低下，对资源的开销如何的高，
 * 诸如此类的文章不外乎都是开发者自身对并行流使用不得当引起的。在使用并行流的过程中，务必要清晰地了解你所操作的元素以及元素类型，当然也要关注计算本身是否高效。
 * 看到这里很多人不免心中会有疑问：并行流的确帮助我们隐藏了底层的多线程使用细节，可是在我们使用的并行流中到底有多少个线程在为之服务呢？
 * <p>
 * Java 8 的设计者们采用了与CPU核数相同数量的线程作为并行流底层的线程数量，大家可以通过Runtime.getRuntime().availableProcessors()获取Java虚拟机运行宿主机器的CPU核数，
 * 当然，如果你觉得与CPU核数相等数量的线程数量比较少，那么你可以通过修改全局参数进行设置，不过这种设置方式将会影响当前Java应用程序的所有并行流线程数量。
 * 笔者个人觉得这并不是一个很好的设计方式，笔者认为parallel()方法默认保持与CPU核数相等的线程数量，顺便再增加一个重载方法parallel(int n)可以允许开发者指定线程数量，
 * 这样即优雅又可以避免采用设置全局配置的拙劣的方式（当然这只是笔者的一家之言）。
 * <p>
 * System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "16");
 */
@Warmup(iterations = 20)
@Measurement(iterations = 20)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class StreamVsParallelStream {

    @Benchmark
    public void calculateNormal(Blackhole hole) {
        long sum = 0L;
        for (long l = 0; l < 10_000_000; l++) {
            sum += l;
        }
        hole.consume(sum);
    }

    @Benchmark
    public void calculateStream(Blackhole hole) {
        long sum = Stream.iterate(0L, l -> l + 1L).limit(10_000_000).reduce(0L, Long::sum);
        hole.consume(sum);
    }

    @Benchmark
    public void calculateParallelStream(Blackhole hole) {
        long sum = Stream.iterate(0L, l -> l + 1L).limit(10_000_000).parallel().reduce(0L, Long::sum);
        hole.consume(sum);
    }

    @Benchmark
    public void calculateLongStream(Blackhole hole) {
        long sum = LongStream.range(0, 10_000_000).reduce(0L, Long::sum);
        hole.consume(sum);
    }

    @Benchmark
    public void calculateParallelLongStream(Blackhole hole) {
        long sum = LongStream.range(0, 10_000_000).parallel().reduce(0L, Long::sum);
        hole.consume(sum);
    }

    public static void main(String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder().include(StreamVsParallelStream.class.getSimpleName()).build();
        new Runner(opt).run();
    }

}