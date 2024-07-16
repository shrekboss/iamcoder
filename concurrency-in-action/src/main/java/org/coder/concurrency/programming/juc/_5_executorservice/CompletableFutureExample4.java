package org.coder.concurrency.programming.juc._5_executorservice;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 5.5.3 异步任务链
 * CompletableFuture还允许将执行的异步任务结果继续交由下一级任务来执行，下一级任务还可以有下一级，以此类推，这样就可以形成一个异步任务链或者任务pipeline。
 * 1.thenApply：以同步的方式继续处理上一个异步任务的结果。
 * 2.thenApplyAsync:已异步的方式继续处理上一个异步任务的结果。
 * 3.thenAccept：以同步的方式消费上一个异步任务的结果。
 * 4.thenAcceptAsync：以异步的方式消费上一个异步任务的结果。
 * <p>
 * 在任务链的末端，如果执行的任务既不想对上一个任务的输出做进一步的处理，又不想消费上一个任务的输出结果，那么我们可以使用thenRun或者thenRunSync方法来执行Runnable任务。
 * 1.thenRun：以同步的方式执行Runnable任务。
 * 2.thenRunAsync:以异步的方式执行Runnable任务。
 * <p>
 * 5.5.4 合并多个Future
 * CompletableFuture还允许将若干个Future合并成为一个Future的使用方式，可以通过thenCompose方法或者thenCombine方法来实现多个Future的合并。
 * 1.thenCompose方法示例。
 * 2.thenCombine方法示例。
 * <p>
 * 5.5.5 多个Future的并行计算
 * 如果想要多个独立的Completable同时并行执行，那么我们还可以借助于allOf()方法来完成，其有点类似于ExecutorService的invokeAll批量提交异步任务。
 * <p>
 * 如果只想运行一批Future中的一个任务，那么我们又该怎么办呢?只需要用anyOf方法替代allOf方法即可（这一点非常类似于ExecutorService的invokeAny方法），
 * 无论是allOf方法还是anyOf方法返回的CompletableFuture类型都是Void类型，如果你试图使用合并后的Future获取异步任务的计算结果，那么这将是不可能的，
 * 必须在每一个单独的Future链中增加上游任务结果的消费或者下游处理任务才可以（详见5.5.3节“异步任务链”）。
 * <p>
 * 5.5.6 错误处理
 * CompletableFuture对于异常的处理方式比普通的Future要优雅合理很多，它提供了handler方法，可用于接受上游任务计算过程中出现的异常错误，
 * 这样一来，我们便可以不用将错误的处理逻辑写在太try...catch...语句块中了，更不需要只能通过Future的get方法调用才能得知异常错误的发生。
 * <p>
 * 5.5.7 JDK 9对CompletableFuture的进一步支持
 * 在JDK 9中，Doug Lea继续操刀Java并发包的开发，为CompletableFuture带来了更多新的改变，比如增加了新的静态工厂方法、实例方法，
 * 提供了任务处理的延迟和超时支持等。已经在使用JDK1.9及其以上版本的读者可以快速体验。
 * 1.新的实例方法
 * 1).Executor defaultExecutor()
 * 2).CompletableFuture<U> newIncompleteFuture()
 * 3).CompletableFuture<T> copy()
 * 4).CompletionStage<T> minimalCompletionStage()
 * 5).CompletableFuture<T> completeAsync(Supplier<? extends T> supplier, Executor executor)
 * 6).CompletableFuture<T> completeAsync(Supplier<? extends T> supplier)
 * 7).CompletableFuture<T> orTimeout(long timeout, TimeUnit unit)
 * 8).CompletableFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit)
 * 2.新的类方法
 * 1).Executor delayedExecutor(long delay, TimeUnit unit, Executor executor)
 * 2).Executor delayedExecutor(long delay, TimeUnit unit)
 * 3).<U> CompletionStage<U> completadStage(U value)
 * 4).<U> CompletionStage<U> failedStage(Throwable ex)
 * 5).<U> CompletionFuture<U> failedFuture(Throwable ex)
 * 3.为了解决超时问题，Java 9还引入了另外两个新功能
 * 1).orTimeout()
 * 2).completableOnTimeout()
 * <p>
 * 5.5.8 CompletableFuture 总结
 * 在5.2节中，不仅讲解了Future的使用，还举例了Future的若干问题，自JDK1.8版本起，CompletableFuture的引入不仅很好地填充了Future的不足之处，
 * 还提供了非常便利的异步编程方式，借助于CompletableFuture，我们可以很容易地开发出异步运行的代码，甚至不用关心其底层线程的维护和管理，只需要关注于代码函数本身即可。
 * <p>
 * CompletableFuture的方法非常多（本节的内容并没有将所有的方法都讲述一遍），但是归纳起来也就如下几类：
 * Future的基本功能、执行异步任务、多CompletionStage的任务链、多Future的整合，以及多Future的并行计算等。
 * <p>
 * 5.6 本章总结
 * 本章详细地介绍了ExecutorService及其家族成员ThreadPoolExecutor和ScheduledExecutorService，
 * 两者都提供了任务异步执行的解决方案，并且在其内部维护了一定数量的、可重复使用的线程，以及针对任务的管理监控等操作方法。
 * <p>
 * 除此之外，ScheduledExecutorService还额外提供了根据某一固定速率执行任务的解决方案，在基本的定时任务场景中使用ScheduledExecutorService就足够了。
 * 如果对任务执行时间策略，比如，对于节假日、周末等休息日这类逻辑较为复杂的定时任务，笔者比较推荐使用Quartz作为解决方案。
 * <p>
 * Future为异步任务的执行提供了一种运行结果可追踪、可在未来时间节点获得结果的解决方案，但是JDK1.5版本推出的Future还包含了诸多的问题和缺陷，
 * 比如，需要启动线程主动进行异步任务计算结果的获取（有可能被阻塞），异步任务执行错误获取异常的方式也是非常别扭（只能通过对异常进行捕获）的，等等。
 * 针对这种不足，Google Guava提供了支持回调的Future解决方法，本章中也对该方案进行了介绍。
 * <p>
 * 在当下的多核PC时代，以并行计算的方式尽可能大地发挥CPU的威力已经成为后端开发者们孜孜不圈的追求，Fork/Join Framework提供了将一个复杂任务以递归的方式进行拆分（Fork），
 * 并分配在不同的CPU内核中并行运行的功能，然后又以Join的方式将任务的最终结果以递归的方式整合返回。本章介绍了Fork/Join Framework在Java中的实现ForkJoinPool，
 * 以及执行在其内部的两种类型的任务RecursiveTask和RecursiveAction。但是在这两种类型的任务中，fork和join的动作都发生在compute方法中，这样会显得该方法的职责过重，
 * 不够单一，并且很难进行单元测试。同样，在JDK1.8版本中提供的Spliterator接口对该过程做了进一步的抽象，使得每个步骤职责单一、可测试性强，第6章将会接触到这一部分的内容。
 * <p>
 * CompletionService通过将已确认完成的任务存入阻塞队列的方式任务的提交者与Future脱耦，在任务被CompletionService提交异步执行之后，
 * 任务的提交者只需要通过其内部的队列就可以获取被执行完成的任务Future，非常适合于批量异步任务提交执行的场景，因为可以获得结束任务最早的返回结果，
 * 以进行进一步的操作，而不是进行不必要的等待。
 * <p>
 * 在本章的最后，还介绍了在JDK1.8版本中才被引入的CompletableFuture，该类不仅很好地完成了Future本该具备的特性，
 * 还提供了诸如直接进行异步任务的执行之类的操作，允许将若干异步任务(Future)组合成一个异步任务链，并且还可以很好地合并多个异步任务的执行结果，
 * 使其成为一个新的Future。同时，CompletableFuture对异步任务执行过程中的错误的处理方式也要合理和直观很多。
 */
public class CompletableFutureExample4 {

    @Test
    public void thenApply() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        /**
         * supplyAsync的计算结果为“Java”
         * thenApply继续处理“Java”，返回字符串的长度
         * supplyAsync与thenApply的任务执行是同一个线程
         */
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync:" + Thread.currentThread());
            return "Java";
        }, executor).thenApply(e -> {
            System.out.println("thenApply:" + Thread.currentThread());
            return e.length();
        });
        assert future.get() == 4;
    }

    @Test
    public void thenApplyAsync() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        /**
         * supplyAsync的计算结果为“Java”
         * thenApplyAsync继续处理“Java”，返回字符串的长度
         */
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync:" + Thread.currentThread());
            return "Java";
        }, executor).thenApplyAsync(e -> {
            System.out.println("thenApplyAsync:" + Thread.currentThread());
            return e.length();
        });
        assert future.get() == 4;
    }

    @Test
    public void thenAccept() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        /**
         * supplyAsync的计算结果为“Java”
         * thenAccept消费supplyAsync的结果
         */
        CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync:" + Thread.currentThread());
            return "Java";
        }, executor).thenAccept(v -> {
            System.out.println("thenAccept:" + Thread.currentThread());
            System.out.println(v);
        });
        executor.shutdown();
    }

    @Test
    public void thenAcceptAsync() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        /**
         * supplyAsync的计算结果为“Java”
         * thenAcceptAsync消费supplyAsync的结果
         */
        CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync:" + Thread.currentThread());
            return "Java";
        }, executor).thenAcceptAsync(v -> {
            System.out.println("thenAcceptAsync:" + Thread.currentThread());
            System.out.println(v);
        });
        executor.shutdown();
    }

    @Test
    public void thenRun() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        /**
         * supplyAsync的计算结果为“Java”
         * thenAcceptAsync消费supplyAsync的结果
         * thenRun执行Runnable任务
         */
        CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync:" + Thread.currentThread());
            return "Java";
        }, executor).thenAcceptAsync(v -> {
            System.out.println("thenAcceptAsync:" + Thread.currentThread());
            System.out.println(v);
        }).thenRun(() ->
                System.out.println("All of task completed." + Thread.currentThread())
        );
    }

    @Test
    public void thenRunAsync() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        /**
         * supplyAsync的计算结果为“Java”
         * thenAcceptAsync消费supplyAsync的结果
         * thenRunAsync执行Runnable任务
         */
        CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync:" + Thread.currentThread());
            return "Java";
        }, executor).thenAcceptAsync(v -> {
            System.out.println("thenAcceptAsync:" + Thread.currentThread());
            System.out.println(v);
        }).thenRunAsync(() ->
                        System.out.println("All of task completed." + Thread.currentThread()),
                executor
        );
    }

    @Test
    public void thenCompose() {
        //通过thenCompose将两个Future合并成一个Future
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Java")
                //s为上一个Future的计算结果
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " Scala"));
        //合并后的Future通过thenApply方法组成任务链
        completableFuture.thenApply(String::toUpperCase).thenAccept(System.out::println);
    }

    @Test
    public void thenCombine() {
        //通过thenCombine将两个Future合并成一个Future
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Java")
                .thenCombine(CompletableFuture.supplyAsync(() -> " Scala"),
                        //s1为第一个Future的计算结果，s2为第二个Future的计算结果
                        (s1, s2) -> s1 + s2);
        //合并后的Future通过thenApply方法组成任务链
        completableFuture.thenApply(String::toUpperCase).thenAccept(System.out::println);
    }

    @Test
    public void allOf() throws InterruptedException, ExecutionException {
        //定义三个CompletableFuture
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Java");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Parallel");
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "Future");
        //批量并行执行，返回值是一个void类型的CompletableFuture
        CompletableFuture<Void> future = CompletableFuture.allOf(f1, f2, f3).thenRun(() -> {
            try {
                System.out.println(f1.isDone() + " and result:" + f1.get());
                System.out.println(f2.isDone() + " and result:" + f2.get());
                System.out.println(f3.isDone() + " and result:" + f3.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        //阻塞等待运行结束
        future.get();
    }

    @Test
    public void handle() {
        CompletableFuture.<String>supplyAsync(() -> {
            throw new RuntimeException();
        }).handle((r, e) -> {
            if (e != null) {
                return "ERROR";
            } else {
                return r;
            }
        }).thenAccept(System.out::println);
    }
}