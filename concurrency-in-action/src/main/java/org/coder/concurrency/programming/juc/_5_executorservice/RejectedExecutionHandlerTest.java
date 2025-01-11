package org.coder.concurrency.programming.juc._5_executorservice;

/**
 * 5.拒绝策略RejectedExecutionHandler
 * 我们在5.1.1节中分析过，当线程池中没有空闲的工作线程，并且任务队列已满时，新的任务将被执行拒绝策略(当然，在线程池状态进行二次检查时，如果发现线程池已经被执行了销毁，那么进入任务队列的任务也会被移除并且执行拒绝策略，
 * 我们在5.1.1节的源码注释③处和④处都有过说明)，在ThreadPoolExecutor中提供了4种形式的拒绝策略，当然它还允许开发者自定义拒绝策略。
 * <p>
 * 拒绝策略接口RejectedExecutionHandler的定义也非常简单，仅包含一个接口方法，代码如下所示。
 * package java.util.concurrent;
 * <p>
 * public interface RejectedExecutionHandler {
 * void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
 * }
 * <p>
 * 1).DiscardPolicy：丢弃策略，任务会被直接无视丢弃而等不到执行，因此该策略需要慎重使用。
 * <p>
 * public static class DiscardPolicy implements RejectedExecutionHandler {
 * public DiscardPolicy(){}
 * //空实现，无视提交的任务
 * public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {}
 * }
 * <p>
 * 2).AbortPolicy：中止策略，在线程池中使用该策略，在无法受理任务时会抛出拒绝执行异常RejectedExecutionException(运行时异常)。
 * <p>
 * ...省略
 * public AbortPolicy() {}
 * public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
 * //抛出RejectedExecutionException异常
 * throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
 * }
 * ...省略
 * <p>
 * 3).DiscardOldestPolicy：丢弃任务队列中最老任务的策略(这是笔者通过类名直译过来的，事实上这样直译不够准确，通过4.2节阻塞队列部分的学习，
 * 相信大家都知道并不是所有的阻塞队列都是FIFO，也就是说最早进入任务队列中的任务并不一定是最早最老的，比如，优先级阻塞队列会根据排序规则来决定将哪个任务放在对头)。
 * <p>
 * public DiscardOldestPolicy() {}
 * public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
 * if(!e.isShutdown()) {
 * //从阻塞队列头部移除老的任务
 * e.getQueue().poll();
 * //将最新的任务加入任务队列或者执行
 * e.execute(r);
 * }
 * }
 * <p>
 * 4).CallerRunsPolicy：调用者线程执行策略，前面的三种拒绝策略要么会在执行execute方法时抛出异常，要么会将任务丢弃。该策略不会导致新任务的丢弃，
 * 但是任务会在当前线程中被阻塞地执行，也就是说任务不会由线程池中的工作线程执行。
 * <p>
 * public CallerRunsPolicy(){}
 * public void rejectedExecution(Runnable r, ThreadPoolExecutor e){
 * if(!e.isShutdown()){
 * //在当前线程中同步执行任务
 * r.run();
 * }
 * }
 */
public class RejectedExecutionHandlerTest {

}