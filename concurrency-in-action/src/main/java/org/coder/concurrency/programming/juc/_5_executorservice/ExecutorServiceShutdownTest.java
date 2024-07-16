package org.coder.concurrency.programming.juc._5_executorservice;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;

/**
 * 5.1.3 关闭ExecutorService
 * 如果ExecutorService在接下来的程序执行中将不再被使用，则需要将其关闭以释放工作线程所占用的系统资源，
 * ExecutorService接口定义了几种不同形式的关闭方式，本节就来看看这几种关闭ExecutorService方式的用法以及不同之处。
 * <p>
 * 1.有序关闭(shutdown)
 * shutdown提供了一种有序关闭ExecutorService的方式，当方法被执行后新的任务提交将会被拒绝，但是工作线程正在执行的任务以及线程池任务队列中已经被提交的任务还是会执行，
 * 当所有的提交任务都完成后线程池中的工作线程才会销毁进而达到ExecutorService最终被关闭的目的。
 * <p>
 * 该方法是立即返回方法，它并不会阻塞等待所有的任务处理结束及ExecutorService最终的关闭，因此如果你想要确保线程池被关闭之后才进行下一步的操作，
 * 那么这里可以配合另一个等待方法awaitTermination使当前线程进入阻塞等待ExecutorService关闭结束后再进行下一步的动作。
 * <p>
 * 2.立即关闭(shutdownNow)
 * shutdownNow方法首先会将线程池状态修改为shutdown状态，然后将未被执行的任务挂起并从任务队列中排干，其次会尝试中断正在进行任务处理的工作线程，最后返回未被执行的任务，
 * 当然，对一个执行了shutdownNow的线程池提交新的任务同样会被拒绝。
 * <p>
 * 3.组合关闭(shutdown&shutdownNow)
 * 通常情况下，为了确保线程池被尽可能安全地关闭，我们会采用两种关闭线程池的组合方式，以尽可能确保正在运行的任务被正常执行的同时又能提高线程池被关闭的成功率。
 * 实际上这种组合式的关闭线程池的方式也是作者Doug Lea 比较推崇的一种方式，并且他在官方文档中将其称之为关闭线程池模式。
 */
public class ExecutorServiceShutdownTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    @Test
    public void shutdown() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        //提交10个任务
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread() + " is running.");
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        //有序关闭
        executor.shutdown();
        //执行shutdown后的断言
        assert executor.isShutdown();//线程池被shutdown
        assert executor.isTerminating();//线程池正在结束中
        //线程池未完全结束，因为任务队列中存在任务
        assert !executor.isTerminated();
        //新提交的任务将不被接收，执行拒绝策略
        executor.execute(() -> System.out.println("new task submit after shutdown"));
        //等待线程池结束，最多等待10分钟
        executor.awaitTermination(10, TimeUnit.MINUTES);
        assert executor.isShutdown();//线程池被shutdown
        assert !executor.isTerminating();//线程池服务已经被终结
        assert executor.isTerminated();//线程池服务被终结
        System.out.println("end");
    }

    /**
     * 运行下面的程序，工作线程会被尝试中断，很明显，我们的任务中存在可被中断的方法调用，因此任务会被成功中断。
     */
    @Test
    public void shutdownNow() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread() + " is running.");
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        //执行立即关闭操作，返回值为未被执行的任务
        List<Runnable> remainingRunnable = executor.shutdownNow();
        System.out.println(remainingRunnable.size());
    }

    @Test
    public void shutdownAndShutdownNow() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                try {
                    System.out.println(Thread.currentThread() + " is running.");
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        shutdownAndAwaitTermination(executor, 100, TimeUnit.SECONDS);
    }

    private void shutdownAndAwaitTermination(ExecutorService executor, long timeout, TimeUnit unit) {
        //首先执行executor的有序关闭方法
        executor.shutdown();
        try {
            //如果在指定时间内线程池仍旧未被关闭
            if (!executor.awaitTermination(timeout, unit)) {
                //则执行立即关闭方法，排干任务队列中的任务
                executor.shutdownNow();
                //如果线程池中的工作线程正在执行一个非常耗时且不可中断的方法，则中断失败
                if (!executor.awaitTermination(timeout, unit)) {
                    //print executor not terminated by normal.
                }
            }
        } catch (InterruptedException e) {
            //如果当前线程被中断，并且捕获了中断信号，则执行立即关闭方法
            executor.shutdownNow();
            //重新抛出中断信号
            Thread.currentThread().interrupt();
        }
    }
}