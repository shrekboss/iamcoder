package org.coder.concurrency.programming.pattern._18_master_slave._reusable;

import org.coder.concurrency.programming.util.Debug;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ParallelPrimeGenerator {

    public static void main(String[] args) throws Exception {
        PrimeGeneratorService primeGeneratorService = new PrimeGeneratorService();

        Set<BigInteger> result = primeGeneratorService.generatePrime(Integer.parseInt(args[0]));
        System.out.println("Generated " + result.size() + " prime:");
        System.out.println(result);
    }
}

class Range {
    public final int lowerBound;
    public final int upperBound;

    public Range(int lowerBound, int upperBound) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException(
                    "upperBound should not be less than lowerBound!");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public String toString() {
        return "Range [" + lowerBound + ", " + upperBound + "]";
    }

}

/*
 * 质数生成器服务。 模式角色：Master-Slave.Master
 */
class PrimeGeneratorService extends
        AbstractMaster<Range, Set<BigInteger>, Set<BigInteger>> {

    public PrimeGeneratorService() {
        this.init();
    }

    // 创建子任务分解算法实现类
    @Override
    protected TaskDivideStrategy<Range> newTaskDivideStrategy(
            final Object... params) {

        final int numOfSlaves = slaves.size();
        final int originalTaskScale = (Integer) params[0];
        final int subTaskScale = originalTaskScale / numOfSlaves;
        final int subTasksCount = (0 == (originalTaskScale % numOfSlaves)) ? numOfSlaves
                : numOfSlaves + 1;

        TaskDivideStrategy<Range> tds = new TaskDivideStrategy<Range>() {
            private int i = 1;

            @Override
            public Range nextChunk() {
                int upperBound;
                if (i < subTasksCount) {
                    upperBound = i * subTaskScale;
                } else if (i == subTasksCount) {
                    upperBound = originalTaskScale;
                } else {
                    return null;
                }

                int lowerBound = (i - 1) * subTaskScale + 1;
                i++;

                return new Range(lowerBound, upperBound);
            }

        };
        return tds;
    }

    // 创建Slave线程
    @Override
    protected Set<? extends SlaveSpec<Range, Set<BigInteger>>> createSlaves() {
        Set<PrimeGenerator> slaves = new HashSet<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            slaves.add(new PrimeGenerator(new ArrayBlockingQueue<>(2)));
        }
        return slaves;
    }

    // 组合子任务的处理结果
    @Override
    protected Set<BigInteger> combineResults(
            Iterator<Future<Set<BigInteger>>> subResults) {

        Set<BigInteger> result = new TreeSet<>();

        while (subResults.hasNext()) {

            try {
                result.addAll(subResults.next().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (SubTaskFailureException.class.isInstance(cause)) {
                    @SuppressWarnings("rawtypes")
                    RetryInfo retryInfo = ((SubTaskFailureException) cause).retryInfo;
                    Object subTask = retryInfo.subTask;
                    Debug.info("failed subTask:" + subTask);
                    e.printStackTrace();
                }
            }

        }

        return result;

    }

    // 使Master子类对外保留一个含义具体的方法名
    public Set<BigInteger> generatePrime(int upperBound) throws Exception {
        return this.service(upperBound);
    }

    /*
     * 质数生成器。 模式角色：Master-Slave.Slave
     */
    private static class PrimeGenerator extends
            WorkerThreadSlave<Range, Set<BigInteger>> {

        public PrimeGenerator(BlockingQueue<Runnable> taskQueue) {
            super(taskQueue);
        }

        @Override
        protected Set<BigInteger> doProcess(Range range) throws Exception {

            Set<BigInteger> result = new TreeSet<>();
            BigInteger start = BigInteger.valueOf(range.lowerBound);
            BigInteger end = BigInteger.valueOf(range.upperBound);

            while (-1 == (start = start.nextProbablePrime()).compareTo(end)) {
                result.add(start);
            }

            return result;
        }

    }

}