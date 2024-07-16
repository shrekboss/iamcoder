package org.coder.concurrency.programming.juc._5_executorservice;

/**
 * 6.ThreadPoolExecutor的其他方法
 * ThreadPoolExecutor不仅提供了可重复使用的工作线程，还使得任务的异步执行变得高效，同时它还提供了很多统计信息和查询监控线程池中的工作线程、任务等方法，如表5-1所示。
 * 1).allowsCoreThreadTimeOut()：设置是否允许核心线程超时
 * 2).getActiveCount()：返回当前线程池中活跃的工作线程数
 * 3).getCompletedTaskCount()：返回线程池总共完成的任务数量
 * 4).getCorePoolSize()：返回线程池中的核心线程数
 * 5).getKeepAliveTime(TimeUnit unit)：返回KeepAliveTime的单位时长
 * 6).getLargestPoolSize()：返回线程池中的线程截至目前最大的线程数量，注意LargestPoolSize不等同于maximumPoolSize
 * 7).getPoolSize()：返回当前线程池中的工作线程数量(活跃的和空闲的)
 * 8).getQueue()：返回任务(阻塞)队列
 * 9).getRejectedExecutionHandler()：返回任务拒绝策略
 * 10).getTaskCount()：线程池已经执行完成的任务与任务队列中的任务之和
 * 11).getThreadFactory()：返回ThreadFactory
 * 12).void prestartAllCoreThreads()：在线程池构造完成后，可以执行该方法启动核心线程数量个工作线程，轮询任务队列获取任务并执行
 * 13).boolean prestartCoreThread()：启动一个核心线程，该线程将会轮询任务队列以获取任务并执行
 * 14).purge()：清空任务队列中的任务
 * 15).boolean remove(Runnable task)：从任务队列中移除指定的任务
 * 16).setCorePoolSize(int corePoolSize)：设置核心线程数
 * 17).setKeepAliveTime(long time, TimeUnit unit)：设置KeepAliveTime及时间单位
 * 18).setMaximumPoolSize(int maximumPoolSize)：设置线程池最大线程数
 * 19).setRejectedExecutionHandler(RejectedExecutionHandler handler)：设置拒绝策略
 * 20).setThreadFactory(ThreadFactory threadFactory)：设置ThreadFactory
 * <p>
 * 当然，有关线程池停止销毁的方法也是非常重要的，但是表5-1中并未提及，关于线程池停止销毁的方法会在5.1.3节中详细讲述。
 */
public class ThreadPoolExecutorOtherMethodTest {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}