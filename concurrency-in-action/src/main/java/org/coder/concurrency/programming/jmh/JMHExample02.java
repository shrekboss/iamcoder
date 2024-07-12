package org.coder.concurrency.programming.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * (what) This class not contains any method that be annotated by @Benchmark
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class JMHExample02 {

    /**
     * normal instance method
     */
    public void normalMethod() {
    }

    public static void main(String[] args) throws RunnerException {

        final Options opts = new OptionsBuilder()
                .include(JMHExample02.class.getSimpleName())
                .forks(1)
                .measurementIterations(10)
                .warmupIterations(10).
                build();

        new Runner(opts).run();
    }
}
