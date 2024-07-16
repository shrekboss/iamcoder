package org.coder.concurrency.programming.juc._5_executorservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 2.RecursiveAction
 * RecursiveAction类型的任务与RecursiveTask比较类似，只不过它更关注于子任务是否运行结束，
 * 下面来看一个将数组中的每一个元素并行增加10倍（每一个数字元素都将乘10）的例子，该实例使用RecursiveAction任务的方式来实现。
 * <p>
 * 运行下面的程序会看到list中的元素都被修改了，而且是以并行的方式进行的修改。
 * <p>
 * 5.3.3 ForkJoinPool总结
 * 在本节中，我们了解了Fork/Join Framework及其在Java中的实现ForkJoinPool，通过两个简单的例子讲述了在ForkJoinPool中如何对一个较大的任何分而治之，然后并行执行。
 * <p>
 * 无论是RecursiveTask还是RecursiveAction，对任务的拆分与合并都是在compute方法中进行的，可见该方法的职责太重，不够单一，且可测试性比较差，
 * 因此在Java 8版本中提供了接口Spliterator，其对任务的拆分有了进一步的高度抽象，第六章会讲解Spliterator相关的内容。
 */
//继承RecursiveAction并且重写compute方法
public class RecursiveActionExample extends RecursiveAction {

    private static final long serialVersionUID = 1358274000208659173L;
    private List<Integer> numbers;
    //每个任务最多进行10个元素的计算
    private static final int THRESHOLD = 10;
    private int start;
    private int end;
    private int factor;

    public RecursiveActionExample(List<Integer> numbers, int start, int end, int factor) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
        this.factor = factor;
    }

    @Override
    protected void compute() {
        int length = end - start;
        //直接计算
        if (length < THRESHOLD) {
            computeDirectly();
        } else {
            //拆分
            int middle = start + length / 2;
            RecursiveActionExample taskOne = new RecursiveActionExample(numbers, start, middle, factor);
            RecursiveActionExample taskTwo = new RecursiveActionExample(numbers, middle, end, factor);
            this.invokeAll(taskOne, taskTwo);
        }
    }

    private void computeDirectly() {
        for (int i = start; i < end; i++) {
            numbers.set(i, numbers.get(i) * factor);
        }

    }

    public static void main(String[] args) {
        //随机生成数字并且存入list中
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(ThreadLocalRandom.current().nextInt(1_000));
        }
        //输出原始数据
        System.out.println(list);
        //定义ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //定义RecursiveAction
        RecursiveActionExample forkJoinTask = new RecursiveActionExample(list, 0, 10, 10);
        //将forkJoinTask提交至ForkJoinPool
        forkJoinPool.invoke(forkJoinTask);
        System.out.println(list);
    }

}