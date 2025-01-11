package org.coder.concurrency.programming.juc._2_atomic;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 4.性能大PK
 * AtomicReference所提供的非阻塞原子性对象引用读写解决方案，被应用在很多高并发容器中，比如ConcurrentHashMap。
 * 为了让读者更加直观地看到阻塞与非阻塞的性能对比，本节将使用JMH工具对比两者的性能，参赛双方分别是synchronized关键字和AtomicReference。
 * <p>
 * 对于基准测试的代码，此处不做过多解释，第1章已经非常详细地讲解了JMH的使用。
 * 执行上面的基准测试代码，会看到两者之间的性能差异。
 * Benchmark                            Mode  Cnt  Score   Error  Units
 * AtomicReferenceExample4.cas          avgt   20  0.273 ± 0.008  us/op
 * AtomicReferenceExample4.sync         avgt   20  0.357 ± 0.013  us/op
 * 通过基准测试，我们可以看到AtomicReference的性能要高出synchronized关键字30%以上。
 * 下面进一步分析线程堆栈情况。
 * Synchronized关键字的线程堆栈
 * 88.9%         BLOCKED
 * 10.5%         RUNNABLE
 * 0.6%         WAITING
 * AtomicReference的线程堆栈
 * 98.8%         RUNNABLE
 * 1.2%         WAITING
 */
@Measurement(iterations = 20)
@Warmup(iterations = 20)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class AtomicReferenceExample4 {

    @State(Scope.Group)
    public static class MonitorRace {
        public MonitorRace() {
            System.out.println("111111");
        }

        private DebitCard debitCard = new DebitCard("Alex", 0);

        public void syncInc() {
            synchronized (AtomicReferenceExample4.class) {
                final DebitCard dc = debitCard;
                final DebitCard newDC = new DebitCard(dc.getAccount(), dc.getAmount() + 10);

                this.debitCard = newDC;
            }
        }
    }

    @State(Scope.Group)
    public static class AtomicReferenceRace {
        public AtomicReferenceRace() {
            System.out.println("222222");
        }

        private AtomicReference<DebitCard> ref = new AtomicReference<>(new DebitCard("Alex", 0));

        public void casInc() {
            final DebitCard dc = ref.get();
            final DebitCard newDC = new DebitCard(dc.getAccount(), dc.getAmount() + 10);
            ref.compareAndSet(dc, newDC);
        }
    }

    @GroupThreads(10)
    @Group("sync")
    @Benchmark
    public void syncInc(MonitorRace monitor) {
        monitor.syncInc();
    }

    @GroupThreads(10)
    @Group("cas")
    @Benchmark
    public void casInc(AtomicReferenceRace casRace) {
        casRace.casInc();
    }

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(AtomicReferenceExample4.class.getSimpleName())
                .forks(1)
                .timeout(TimeValue.seconds(10))
                .addProfiler(StackProfiler.class)
                .build();
        new Runner(opts).run();
    }

}

//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 20 iterations, 1 s each
//# Measurement: 20 iterations, 1 s each
//# Timeout: 10 s per iteration
//# Threads: 10 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4.cas
//
//# Run progress: 0.00% complete, ETA 00:01:20
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.280 ±(99.9%) 0.045 us/op
//# Warmup Iteration   2: 0.273 ±(99.9%) 0.023 us/op
//# Warmup Iteration   3: 0.266 ±(99.9%) 0.040 us/op
//# Warmup Iteration   4: 0.265 ±(99.9%) 0.044 us/op
//# Warmup Iteration   5: 0.265 ±(99.9%) 0.037 us/op
//# Warmup Iteration   6: 0.266 ±(99.9%) 0.040 us/op
//# Warmup Iteration   7: 0.267 ±(99.9%) 0.035 us/op
//# Warmup Iteration   8: 0.266 ±(99.9%) 0.044 us/op
//# Warmup Iteration   9: 0.268 ±(99.9%) 0.052 us/op
//# Warmup Iteration  10: 0.265 ±(99.9%) 0.022 us/op
//# Warmup Iteration  11: 0.268 ±(99.9%) 0.035 us/op
//# Warmup Iteration  12: 0.268 ±(99.9%) 0.042 us/op
//# Warmup Iteration  13: 0.268 ±(99.9%) 0.045 us/op
//# Warmup Iteration  14: 0.268 ±(99.9%) 0.032 us/op
//# Warmup Iteration  15: 0.269 ±(99.9%) 0.040 us/op
//# Warmup Iteration  16: 0.266 ±(99.9%) 0.037 us/op
//# Warmup Iteration  17: 0.265 ±(99.9%) 0.032 us/op
//# Warmup Iteration  18: 0.269 ±(99.9%) 0.039 us/op
//# Warmup Iteration  19: 0.269 ±(99.9%) 0.034 us/op
//# Warmup Iteration  20: 0.269 ±(99.9%) 0.046 us/op
//Iteration   1: 0.270 ±(99.9%) 0.052 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   2: 0.268 ±(99.9%) 0.031 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   3: 0.269 ±(99.9%) 0.038 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   4: 0.269 ±(99.9%) 0.050 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   5: 0.267 ±(99.9%) 0.030 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   6: 0.268 ±(99.9%) 0.032 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   7: 0.268 ±(99.9%) 0.037 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   8: 0.269 ±(99.9%) 0.038 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   9: 0.271 ±(99.9%) 0.064 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  10: 0.268 ±(99.9%) 0.048 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  11: 0.269 ±(99.9%) 0.048 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  12: 0.269 ±(99.9%) 0.041 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  13: 0.266 ±(99.9%) 0.024 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  14: 0.268 ±(99.9%) 0.040 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  15: 0.269 ±(99.9%) 0.051 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  16: 0.267 ±(99.9%) 0.022 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  17: 0.283 ±(99.9%) 0.042 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  18: 0.292 ±(99.9%) 0.038 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  19: 0.291 ±(99.9%) 0.060 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  20: 0.291 ±(99.9%) 0.045 us/op
//                 ·stack: <delayed till summary>
//
//
//
//Result "org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4.cas":
//  0.273 ±(99.9%) 0.008 us/op [Average]
//  (min, avg, max) = (0.266, 0.273, 0.292), stdev = 0.009
//  CI (99.9%): [0.265, 0.280] (assumes normal distribution)
//
//Secondary result "org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4.cas:·stack":
//Stack profiler:
//
//....[Thread state distributions]....................................................................
// 98.8%         RUNNABLE
//  1.2%         WAITING
//
//....[Thread state: RUNNABLE]........................................................................
// 92.6%  93.7% org.coder.concurrency.programming.juc._2_atomic.generated.AtomicReferenceExample4_cas_jmhTest.casInc_avgt_jmhStub
//  3.9%   3.9% org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4$AtomicReferenceRace.casInc
//  2.2%   2.2% org.coder.concurrency.programming.juc._2_atomic.generated.AtomicReferenceExample4_cas_jmhTest.cas_AverageTime
//  0.1%   0.1% java.lang.Thread.isInterrupted
//  0.0%   0.0% org.openjdk._1_jmh.infra.Blackhole.evaporate
//  0.0%   0.0% java.util.concurrent.ExecutorCompletionService$QueueingFuture.done
//  0.0%   0.0% sun.misc.Unsafe.unpark
//  0.0%   0.0% java.util.concurrent.ConcurrentHashMap.putVal
//  0.0%   0.0% org.openjdk._1_jmh.runner.InfraControl.preTearDown
//  0.0%   0.0% java.util.concurrent.ThreadPoolExecutor.runWorker
//
//....[Thread state: WAITING].........................................................................
//  1.2% 100.0% sun.misc.Unsafe.park
//
//
//
//# JMH version: 1.19
//# VM version: JDK 1.8.0_202, VM 25.202-b08
//# VM invoker: D:\DevInstall\Java\jdk1.8.0_202\jre\bin\java.exe
//# VM options: -Dfile.encoding=UTF-8
//# Warmup: 20 iterations, 1 s each
//# Measurement: 20 iterations, 1 s each
//# Timeout: 10 s per iteration
//# Threads: 10 threads, will synchronize iterations
//# Benchmark mode: Average time, time/op
//# Benchmark: org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4.sync
//
//# Run progress: 50.00% complete, ETA 00:00:42
//# Fork: 1 of 1
//# Warmup Iteration   1: 0.375 ±(99.9%) 0.017 us/op
//# Warmup Iteration   2: 0.406 ±(99.9%) 0.053 us/op
//# Warmup Iteration   3: 0.428 ±(99.9%) 0.048 us/op
//# Warmup Iteration   4: 0.360 ±(99.9%) 0.108 us/op
//# Warmup Iteration   5: 0.346 ±(99.9%) 0.055 us/op
//# Warmup Iteration   6: 0.332 ±(99.9%) 0.013 us/op
//# Warmup Iteration   7: 0.390 ±(99.9%) 0.176 us/op
//# Warmup Iteration   8: 0.336 ±(99.9%) 0.013 us/op
//# Warmup Iteration   9: 0.334 ±(99.9%) 0.013 us/op
//# Warmup Iteration  10: 0.336 ±(99.9%) 0.012 us/op
//# Warmup Iteration  11: 0.342 ±(99.9%) 0.014 us/op
//# Warmup Iteration  12: 0.343 ±(99.9%) 0.015 us/op
//# Warmup Iteration  13: 0.344 ±(99.9%) 0.010 us/op
//# Warmup Iteration  14: 0.342 ±(99.9%) 0.011 us/op
//# Warmup Iteration  15: 0.341 ±(99.9%) 0.009 us/op
//# Warmup Iteration  16: 0.363 ±(99.9%) 0.108 us/op
//# Warmup Iteration  17: 0.362 ±(99.9%) 0.110 us/op
//# Warmup Iteration  18: 0.362 ±(99.9%) 0.112 us/op
//# Warmup Iteration  19: 0.362 ±(99.9%) 0.113 us/op
//# Warmup Iteration  20: 0.362 ±(99.9%) 0.103 us/op
//Iteration   1: 0.359 ±(99.9%) 0.079 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   2: 0.346 ±(99.9%) 0.029 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   3: 0.339 ±(99.9%) 0.013 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   4: 0.340 ±(99.9%) 0.014 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   5: 0.342 ±(99.9%) 0.009 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   6: 0.342 ±(99.9%) 0.011 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   7: 0.340 ±(99.9%) 0.010 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   8: 0.342 ±(99.9%) 0.008 us/op
//                 ·stack: <delayed till summary>
//
//Iteration   9: 0.342 ±(99.9%) 0.008 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  10: 0.342 ±(99.9%) 0.010 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  11: 0.371 ±(99.9%) 0.140 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  12: 0.373 ±(99.9%) 0.144 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  13: 0.370 ±(99.9%) 0.132 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  14: 0.370 ±(99.9%) 0.133 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  15: 0.373 ±(99.9%) 0.151 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  16: 0.372 ±(99.9%) 0.127 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  17: 0.372 ±(99.9%) 0.137 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  18: 0.370 ±(99.9%) 0.140 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  19: 0.370 ±(99.9%) 0.124 us/op
//                 ·stack: <delayed till summary>
//
//Iteration  20: 0.370 ±(99.9%) 0.133 us/op
//                 ·stack: <delayed till summary>
//
//
//
//Result "org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4.sync":
//  0.357 ±(99.9%) 0.013 us/op [Average]
//  (min, avg, max) = (0.339, 0.357, 0.373), stdev = 0.015
//  CI (99.9%): [0.345, 0.370] (assumes normal distribution)
//
//Secondary result "org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4.sync:·stack":
//Stack profiler:
//
//....[Thread state distributions]....................................................................
// 88.9%         BLOCKED
// 10.5%         RUNNABLE
//  0.6%         WAITING
//
//....[Thread state: BLOCKED].........................................................................
// 88.9% 100.0% org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4$MonitorRace.syncInc
//
//....[Thread state: RUNNABLE]........................................................................
// 10.0%  94.7% org.coder.concurrency.programming.juc._2_atomic.AtomicReferenceExample4$MonitorRace.syncInc
//  0.5%   4.6% org.coder.concurrency.programming.juc._2_atomic.generated.AtomicReferenceExample4_sync_jmhTest.syncInc_avgt_jmhStub
//  0.0%   0.4% sun.misc.Unsafe.unpark
//  0.0%   0.2% org.coder.concurrency.programming.juc._2_atomic.generated.AtomicReferenceExample4_sync_jmhTest.sync_AverageTime
//  0.0%   0.1% java.lang.Thread.currentThread
//  0.0%   0.1% java.lang.Thread.isInterrupted
//
//....[Thread state: WAITING].........................................................................
//  0.6% 100.0% sun.misc.Unsafe.park
//
//
//
//# Run complete. Total time: 00:01:23
//
//Benchmark                            Mode  Cnt  Score   Error  Units
//AtomicReferenceExample4.cas          avgt   20  0.273 ± 0.008  us/op
//AtomicReferenceExample4.cas:·stack   avgt         NaN            ---
//AtomicReferenceExample4.sync         avgt   20  0.357 ± 0.013  us/op
//AtomicReferenceExample4.sync:·stack  avgt         NaN            ---