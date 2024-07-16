package org.coder.concurrency.programming.juc._5_executorservice;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 5.1.2 ScheduledExecutorService 详解
 * ScheduledExecutorService继承自ExecutorService，并且提供了任务被定时执行的特性，
 * 我们可以使用ScheduledExecutorService的实现ScheduledThreadPoolExecutor来完成某些特殊的任务执行，
 * 比如使某个任务根据设定的周期来运行，或者在某个指定的时间来执行任务等。
 * <p>
 * 1.定时任务
 * 对于开发者来说，定时任务其实并不陌生。比如Unix/Linux的Cronintab，JDK1.5版本以前的Timer、TimerTask，开源解决方案Cron4j、Quartz等，
 * 专注于定时任务管理的商业软件Control-M，在开始学习ScheduledThreadPoolExecutor之前我们先来了解一下几种不同的定时任务调度解决方案。
 * (1) Crontab
 * 首先编写一个简单的shell脚本，并且将其保存至Linux操作系统的Crontab中。
 * #!/bin/sh
 * now=`date "+%Y-%m-%d %H:%M:%S"`
 * echo "$now" >>/home/crayzer/scripts/cron.log
 * exit 0
 * <p>
 * 执行crontab -e 命令将test.sh的定时执行写入Crontab中，关于Crontab的语法可以通过阅读这篇文章获得更多信息：http://linuxconfig.org/linux-crontab-reference-guide
 * <p>
 * * /1 * * * * /home/crayzer/scripts/test.sh(test.sh会每个间隔1分钟的时间执行一次)
 * <p>
 * (2) Timer/TimerTask
 * 在JDK1.5版本以前，定时任务的执行基本上都会使用Timer和TimerTask来完成，目前在JDK官网这种方式已经不推荐使用了，替代方案就是我们在本节中将要介绍到的ScheduledThreadPoolExecutor。
 * <p>
 * (3) Quartz
 * 执行定时任务或者执行固定周期、特殊日期等任务对于Quartz来说是其所擅长的，该项目目前已经被应用于成千上万个Java项目之中(说它是开源界最好的任务调度框架也不为过)，
 * 并且它比我们在本节中即将要学习到的主角ScheduledThreadPoolExecutor还要强大很多，感兴趣的读者可以通过阅读Quartz的官方文档来获得更多的帮助。
 * <p>
 * (4) Control-M
 * Control-M是一个非常优秀的商业软件(笔者所做的公司就是Control-M产品的商业用户)，其主要也是用于任务调度，官网地址如下，感兴趣的读者可以了解一下。
 * http://www.bmcsoftware.cn/it-solutions/control-m.html
 * <p>
 * 2.ScheduledThreadPoolExecutor
 * ScheduledThreadPoolExecutor继承自ThreadPoolExecutor，同时又实现了定时执行服务ScheduledExecutorService的所有接口方法，如图5-3所示。
 * <p>
 * 因此，ScheduledExecutorService既具有ThreadPoolExecutor的所有方法，同时又具备定时执行任务的方法，
 * 在ScheduledExecutorService中定义了4个与schedule相关的方法，用于定时执行任务，具体如下。
 * <p>
 * 1).<V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)：
 * 该方法是一个one-shot方法(只执行一次)，任务(callable)会在单位(unit)时间(delay)后被执行，并且立即返回ScheduledFuture，在稍后的程序中可以通过Future获取异步任务的执行结果。
 * 2).ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)：
 * 该方法同样是一个one-shot方法(只执行一次)，任务(runnable)会在单位(unit)时间(delay)后被执行，虽然也会返回ScheduledFuture，但是并不会包含任何执行结果，
 * 因为Runnable接口的run方法本身就是无返回值类型的接口方法，不过可以通过该Future判断任务是否执行结束。
 * 3).ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)：
 * 任务(command)会根据固定的速率(period，时间单位为unit)在时间(initialDelay，时间单位为unit)后不断地被执行。
 * 4).ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit)：
 * 该方法与前一个方法比较类似，只不过该方法将以固定延迟单位时间的方式执行任务。
 */
public class ScheduledThreadPoolExecutorTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    @Test
    public void schedule() throws InterruptedException, ExecutionException {
        //定义ScheduledThreadPoolExecutor，指定核心线程数为2，其他参数保持默认
        ScheduledThreadPoolExecutor scheduleExecutor = new ScheduledThreadPoolExecutor(2);
        //延迟执行任务callable
        ScheduledFuture<String> future = scheduleExecutor.schedule(() -> {
            System.out.println("I am running");
            System.out.println(new Date());
            //返回结果
            return "Hello";
        }, 10, TimeUnit.SECONDS);//任务延迟10秒被执行
        System.out.println(new Date());
        System.out.println("result: " + future.get());
    }

    @Test
    public void schedule2() throws InterruptedException, ExecutionException {
        ScheduledThreadPoolExecutor scheduleExecutor = new ScheduledThreadPoolExecutor(2);
        ScheduledFuture<?> future = scheduleExecutor.schedule(() -> {
            System.out.println(new Date());
            System.out.println("I am running");
        }, 10, TimeUnit.SECONDS);
        System.out.println(new Date());
        //返回值为null
        System.out.println("result: " + future.get());
    }

    @Test
    public void scheduleAtFixedRate() throws InterruptedException, ExecutionException {
        ScheduledThreadPoolExecutor scheduleExecutor = new ScheduledThreadPoolExecutor(2);
        //任务延迟10秒后以后每隔60秒的速率被执行
        ScheduledFuture<?> future = scheduleExecutor.scheduleAtFixedRate(() -> {
            System.out.println(new Date());
        }, 10, 60, TimeUnit.SECONDS);
        System.out.println(new Date());
        /** 不要执行scheduleAtFixedRate返回的future.get()方法，否则当前线程会进入阻塞，
         * 因为command任务会根据固定速率一直运行，当然使用future可以取消任务运行，关于这一点我们在5.2节讲述Future时会详细介绍 **/
        System.out.println("result: " + future.get());
    }

    /**
     * scheduleAtFixedRate和scheduleWithFixedDelay比较类似，初学者甚至会混淆两者，下面我们通过一个例子进行演示。
     * 定义一个简单的Runnable，在该Runnable任务中，对时间的关键输出有两次，然后我们分别将其应用于不同的schedule方法中。
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void scheduleWithFixedDelay() throws InterruptedException, ExecutionException {
        Runnable command = () -> {
            //获取当前时间
            long startTimestamp = System.currentTimeMillis();
            //输出当前时间
            System.out.println("current timestamp: " + startTimestamp);
            //随机休眠0~100毫秒
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //输出任务执行耗时毫秒数
            System.out.println("elapsed time:" + (System.currentTimeMillis() - startTimestamp));
        };
        ScheduledThreadPoolExecutor scheduleExecutor = new ScheduledThreadPoolExecutor(2);
        /**
         * 在scheduleAtFixedRate方法中执行该任务，无论执行耗时多长时间，任务始终会以固定的速率来执行。
         */
//		ScheduledFuture<?> future = scheduleExecutor.scheduleAtFixedRate(command, 10, 1000, TimeUnit.MILLISECONDS);
        /**
         * 在scheduleWithFixedDelay方法中执行该任务，会看到无论执行的开销是多少，下一次任务被执行的时间都会延迟固定的时间。
         */
        ScheduledFuture<?> future = scheduleExecutor.scheduleWithFixedDelay(command, 10, 1000, TimeUnit.MILLISECONDS);
        System.out.println("result: " + future.get());
    }
}