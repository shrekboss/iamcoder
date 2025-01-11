package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * 5.3 ForkJoinPool详解
 * 5.3.1 Fork/Join Framework 介绍
 * Fork/Join框架是在JDK1.7版本中被Doug Lea引入的，Fork/Join计算模型旨在充分利用多核CPU的并行运算能力，
 * 将一个复杂的任务拆分（fork）成若干个并行计算，然后将结果合并（join），以下是有关Fork Join算法计算模型的伪代码。
 * Result solve(Problem problem){
 * if(problem is small)
 * directly solve problem
 * else {
 * split problem into independent parts
 * fork new subtasks to solve each part
 * join all subtasks
 * compose result from subresults
 * }
 * }
 * <p>
 * 上述伪代码摘自Doug Lea发表的同名论文http://gee.cs.oswego.edu/dl/papers/fj.pdf。
 * <p>
 * 在JDK中，Fork/Join框架的实现为ForkJoinPool及ForkJoinTask等，虽然这些API在日常工作中的使用并不是非常频繁，
 * 但是在很多更高级的JVM开发语言（比如，Scala、Clojure等函数式开发语言）底层都有ForkJoinPool的身影，
 * 在Java1.8中引入的Parallel Stream其底层的并行计算也是由ForkJoinPool来完成的。
 * <p>
 * “分而治之”（divide and conquer）是Fork/Join框架的核心思想，图5-4很好地诠释了这一个工作过程。
 * Forks通过递归的形式将任务拆分成较小的独立的子任务，直到它足够简单以至于可以在一个异步任务中完成为止；
 * Join则通过递归的方式将所有子任务的若干结果合并成一个结果，或者在子任务不关心结果是否返回的情况下，
 * Join将等待所有的子任务完成各自的异步任务后“合并计算结果”，然后逐层向上汇总，直到将最终结果返回给执行线程。
 * <p>
 * 5.3.2 ForkJoinTask详解
 * 前面提到过ForkJoinPool是Fork/Join Framework在Java中的实现，同时它也是该框架最核心的类之一，
 * ForkJoinPool是ExecutorService的一个具体实现，用于管理工作线程并为我们提供工具以及获取有关线程池状态和性能的信息等。
 * ForkJoinTask是在ForkJoinPool内部执行的任务的基本类型，在ForkJoinPool中运行着的任务无论是RecursiveTask还是RecursiveAction都是ForkJoinTask的子类，
 * 前者在子任务运行结束后返回计算结果，后者则不会有任何返回，而只是专注于子任务的运行本身。
 * <p>
 * 1.RecursiveTask
 * RecursiveTask任务类型除了进入子任务的运算之外，还会将最终子任务的计算结果返回，下面通过一个简单的实例来认识一下RecursiveTask。
 * 该示例通过高并发多线程的方式计算一个数组中所有元素之和，数组会被拆分成若干分片，每一个异步任务都会计算对应分片元素之和，
 * 最后所有的子任务结果会被join在一起作为最终的结果返回。示例代码如下：
 * <p>
 * 下面示例代码的重点在于compute方法中如何进行任务的拆分。ForkJoinPool在运算的过程中首先会以递归的方式将任务拆分成2个子任务，
 * 子任务还会继续拆分，直到每一个子任务处理的数据量是10000个为止，然后在不同的线程中直接计算，最后将所有子任务的计算结果进行join并返回。
 */
public class RecursiveTaskSum extends RecursiveTask<Long> {

    private static final long serialVersionUID = -678252632937542771L;
    private final long[] numbers;
    private final int startIndex;
    private final int endIndex;
    //每个子任务运算的最多元素数量
    private static final long THRESHOLD = 10_000L;

    public RecursiveTaskSum(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    public RecursiveTaskSum(long[] numbers, int startIndex, int endIndex) {
        this.numbers = numbers;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }


    @Override
    protected Long compute() {
        int length = endIndex - startIndex;
        //当元素数量少于等于THRESHOLD时，任务将不必再拆分
        if (length <= THRESHOLD) {
            //直接计算
            long result = 0L;
            for (int i = startIndex; i < endIndex; i++) {
                result += numbers[i];
            }
            return result;
        }
        //拆分任务（一分为二，被拆分后的任务有可能还会被拆分：递归）
        int tempEndIndex = startIndex + length / 2;
        //第一个子任务
        RecursiveTaskSum firstTask = new RecursiveTaskSum(numbers, startIndex, tempEndIndex);
        //异步执行第一个被拆分的子任务（子任务有可能还会被拆，这将取决于元素数量）
        firstTask.fork();
        //拆分第二个子任务
        RecursiveTaskSum secondTask = new RecursiveTaskSum(numbers, tempEndIndex, endIndex);
        //异步执行第二个被拆分的子任务（子任务有可能还会被拆，这将取决于元素数量）
        secondTask.fork();
        //join等待子任务的运算结果
        Long secondTaskResult = secondTask.join();
        Long firstTaskResult = firstTask.join();
        //将子任务的结果相加然后返回
        return (secondTaskResult + firstTaskResult);
    }

    public static void main(String[] args) {
        //创建一个数组
        long[] numbers = LongStream.rangeClosed(1, 9_000_000).toArray();
        //定义RecursiveTask
        RecursiveTaskSum forkJoinSum = new RecursiveTaskSum(numbers);
        //创建ForkJoinPool并提交执行RecursiveTask
        Long sum = ForkJoinPool.commonPool().invoke(forkJoinSum);
        //输出结果
        System.out.println(sum);
        //validation result验证结果的正确性
        assert sum == LongStream.rangeClosed(1, 9_000_000).sum();
    }
}