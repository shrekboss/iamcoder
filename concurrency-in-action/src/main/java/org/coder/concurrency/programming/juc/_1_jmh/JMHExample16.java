package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * (what) 测试循环展开优化 避免循环展开(Loop Unwinding)
 *
 * 尽可能地避免或减少在基准测试方法中出现循环，因为循环代码在运行阶段(JVM 后期优化)极有可能“痛下杀手”进行相关的优化。
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
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class JMHExample16 {

    private int x = 1;
    private int y = 2;

    @Benchmark
    public int measure(){
        return(x + y);
    }

    private int loopCompute(int times){
        int result = 0;
        for(int i=0; i < times; i++){
            result += (x + y);
        }
        return result;
    }


    @OperationsPerInvocation
    @Benchmark
    public int measureLoop_1(){
        return loopCompute(1);
    }


    @OperationsPerInvocation(10)
    @Benchmark
    public int measureLoop_10(){
        return loopCompute(10);
    }

    @OperationsPerInvocation(100)
    @Benchmark
    public int measureLoop_100(){
        return loopCompute(100);
    }

    @OperationsPerInvocation(1000)
    @Benchmark
    public int measureLoop_1000(){
        return loopCompute(1000);
    }

    // 循环次数越多，折叠次数也越多
    public static void main(String[] args) throws RunnerException {
        final Options ops = new OptionsBuilder().include(JMHExample16.class.getSimpleName()).build();
        new Runner(ops).run();
    }

}