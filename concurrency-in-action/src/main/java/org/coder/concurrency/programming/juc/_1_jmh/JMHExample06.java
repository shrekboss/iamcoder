package org.coder.concurrency.programming.juc._1_jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * (what) Thread 独享的 State：每一个运行基准测试方法的线程都会持有一个独立的对象实例。该实例既可能作为基准测试方法参数传入的，
 * 也可能是运行基准方法所在的宿主 class，将 State 设置为 Scope.Thread 一般主要是针对非线程安全的类。
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
@OutputTimeUnit(TimeUnit.MICROSECONDS)
//设置5个线程运行基准测试方法
@Threads(5)
public class JMHExample06 {

    //5个运行线程，每一个线程都会持有一个Test的实例
    @State(Scope.Thread)
    public static class Test {

        public Test() {
            System.out.println("create instance");
        }

        public void method() {
        }
    }

    //通过基准测试将 State 引用传入
    @Benchmark
    public void test(Test test) {
        test.method();
    }

    public static void main(String[] args) throws RunnerException {

        final Options opts = new OptionsBuilder()
                .include(JMHExample06.class.getSimpleName())
                .build();

        new Runner(opts).run();
    }
}
