package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * (what) Fork 用于避免 Profile-guided optimizations
 *
 * @Fork(0): 每个基准测试方法都将会与 JMHExample17 使用同一个 JVM 进程
 * @Fork(1): 每一次基准测试时都会开辟一个全新的 JVM 进程对其进行测试，那么多个基准测试之间将不会再存在干扰
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
public class JMHExample17 {

    interface Inc {
        int inc();
    }

    public static class Inc1 implements Inc {

        private int i = 0;
        @Override
        public int inc() {
            return ++i;
        }
    }

    public static class Inc2 implements Inc {

        private int i = 0;
        @Override
        public int inc() {
            return ++i;
        }
    }

    private Inc inc1 = new Inc1();
    private Inc inc2 = new Inc1();

    private int measure(Inc inc) {
        int result = 0;
        for (int i = 0; i < 10; i++) {
            result += inc.inc();
        }
        return result;
    }

    @Benchmark
    public int measure_inc_1() {
        return this.measure(inc1);
    }

    @Benchmark
    public int measure_inc_2() {
        return this.measure(inc2);
    }

    @Benchmark
    public int measure_inc_3() {
        return this.measure(inc1);
    }

    // 循环次数越多，折叠次数也越多
    public static void main(String[] args) throws RunnerException {
        final Options ops = new OptionsBuilder().include(JMHExample17.class.getSimpleName()).build();
        new Runner(ops).run();
    }
}