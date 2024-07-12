package org.coder.concurrency.programming.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * (what) 线程组共享的 State
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
public class JMHExample08 {

    // 线程组共享
    @State(Scope.Group)
    public static class Test {
        public Test() {
            System.out.println("create instance");
        }

        public void write() {
            System.out.println("write");
        }

        public void read() {
            System.out.println("read");
        }
    }


    @GroupThreads(3)
    @Group("test")
    @Benchmark
    public void testWrite(Test test) {
        test.write();
    }


    @GroupThreads(3)
    @Group("read")
    @Benchmark
    public void testRead(Test test) {
        test.read();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder().include(JMHExample08.class.getSimpleName()).build();
        new Runner(opts).run();
    }
}