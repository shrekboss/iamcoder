package org.coder.concurrency.programming.juc._1_jmh;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * (what) {@link OutputTimeUnit} 提供统计结果输出时间的单位
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Measurement(iterations = 5)
@Warmup(iterations = 2)
public class JMHExample05 {

    //在基准方法上设置
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Benchmark
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    public static void main(String[] args) throws RunnerException {
        final Options opts = new OptionsBuilder()
                .include(JMHExample05.class.getSimpleName())
                //在Options上设置
                .timeUnit(TimeUnit.NANOSECONDS)
                .forks(1).build();

        new Runner(opts).run();
    }
}
