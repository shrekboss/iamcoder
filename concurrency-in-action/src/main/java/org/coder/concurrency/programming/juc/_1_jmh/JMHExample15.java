package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.log;

/**
 * (what) 避免常量折叠(Constant Folding)
 * eg：
 * private final int x = 10;
 * private final int y = x * 2;
 * <p>
 * 在编译阶段， y 的值呗直接赋予 200；常量折叠式 Java 编译器早期的一种优化 --- 编译优化。
 *
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHExample15 {

    private final double x1 = 124.456;
    private final double x2 = 342.456;

    private double y1 = 124.456;
    private double y2 = 342.456;

    @Benchmark
    public double returnDirect() {
        return 42_620.703936d;
    }

    @Benchmark
    public double returnCaculate_1() {
        return x1 * x2;
    }

    // y1 & y2 没有被 final 修饰
    @Benchmark
    public double returnCaculate_2() {
        return log(y1) * log(y2);
    }

    // x1 & x2 有被 final 修饰
    @Benchmark
    public double returnCaculate_3() {
        return log(x1) * log(x2);
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder().include(JMHExample15.class.getSimpleName()).build();
        new Runner(opts).run();
    }
}