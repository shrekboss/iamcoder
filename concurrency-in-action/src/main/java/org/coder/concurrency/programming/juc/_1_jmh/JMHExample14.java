package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.PI;

/**
 * (what) 使用 Blackhole：可以帮助你再无返回值的基准测试方法中避免 DC(Dead Code)情况的发生。
 *
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
public class JMHExample14 {

    double x1 = PI;
    double x2 = PI * 2;

    @Benchmark
    public double baseline() {
        // 不是 Dead Code，因为对结果进行了返回
        return Math.pow(x1, 2);
    }

    @Benchmark
    public double powButReturnOne() {
        // Dead Code 会被擦除
        Math.pow(x1, 2);
        // 不是 Dead Code，因为对结果进行了返回
        return Math.pow(x2, 2);
    }

    @Benchmark
    public double powThenAdd() {
        return Math.pow(x1, 2) + Math.pow(x2, 2);
    }

    // 使用 blackhole 来拉平 编译优化的开销 （都不进行优化， 但是blackhole 也会消耗资源）确保无返回值时候 DC 的发生不会影响基准测试
    @Benchmark
    public void useBlackHole(Blackhole hole) {
        // 将结果存放至 Blackhole 中，因此两次 pow 操作都会生效
        hole.consume(Math.pow(x1, 2));
        hole.consume(Math.pow(x2, 2));
    }


    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder().include(JMHExample14.class.getSimpleName()).build();
        new Runner(opts).run();
    }
}