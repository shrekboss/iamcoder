package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static java.lang.Math.PI;

/**
 * (what) DCE(Dead Code Elimination) : 指 JVM 擦去一些上下文无关，甚至经过计算之后确定压根不会用到的代码。
 *
 * eg：
 * public void test() {
 *     int x = 10;
 *     int y = 10;
 *     int z = x + y;
 * }
 *
 * JVM 很有可能会将 test() 方法当作一个空的方法来看待
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
public class JMHExample13 {

    @Benchmark
    public void baseline(){

    }

    @Benchmark
    public void measureLog1(){
        Math.log(PI);
    }

    @Benchmark
    public void measureLog2(){
        double result = Math.log(PI);
        Math.log(result);
    }

    // 不是 Dead Code
    @Benchmark
    public double measureLog3(){
        return Math.log(PI);
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder().include(JMHExample13.class.getSimpleName()).build();
        new Runner(opts).run();
    }
}