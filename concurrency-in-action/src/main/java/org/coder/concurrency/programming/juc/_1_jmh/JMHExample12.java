package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.PI;
import static java.lang.Math.log;

@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHExample12 {

    /**
     * 测试jvm编译优化
     */
    @Benchmark
    public void test1() {

    }

    // 禁止 编译器优化 Dead Code
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    @Benchmark
    public void test2() {
        log(PI);
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder().include(JMHExample12.class.getSimpleName()).build();
        new Runner(opts).run();
    }
}