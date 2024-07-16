package org.coder.concurrency.programming.juc._3_utils;

import com.google.common.util.concurrent.Monitor;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * 3.11.2 RateLimiter的限流操作————漏桶算法
 * 通过上文对RateLimiter的基本使用，我们不难发现可以借助它很好地完成限流的场景，那么在什么情况下，我们需要对访问进行限制呢？
 * 根据木桶原理，板子最短的那根就是储水的极限，同样一个复杂的系统会有若干个子系统构成，并不是所有的子系统都会具备无限水平扩展的能力，
 * 当业务量和并发量超过子系统最大的承受能力时，该子系统将会成为木桶中最短的那块板子，比如，由于业务洪峰的到来，
 * 关系型数据库无力承受拒绝服务、本地磁盘I/O吞吐量骤降、网络带宽被挤爆、远程的第三方接口无法响应等。
 * <p>
 * 因此在一个提供高并发服务的系统中，若系统无法承受更多的请求，则对其进行降权处理(直接拒绝请求或者将请求暂存起来稍后处理等)，这是一种比较常见的做法，
 * 漏桶算法作为一种常见的限流算法应用非常广泛，本节将为大家介绍漏桶算法的原理，并且使用RateLimiter实现一个简单的漏桶算法。
 * <p>
 * 1.无论漏桶进水速率如何，漏桶的出水速率永远都是固定的。
 * 2.如果漏桶中没有水流，则在出水口不会有水流出。
 * 3.漏桶有一定的水容量。
 * 4.如果流入水量超过漏桶容量，则水将会溢出(降权处理)。
 * <p>
 * 漏桶算法虽然很简单，但是没有接触过的朋友一时间很可能很难将上面算法的原理用程序实现出来，为了更加切合我们的实际开发，
 * 下面用程序员能够理解的方式为大家再解释一遍，并且使用RateLimiter实现漏桶算法。
 * <p>
 * RateLimiterBucket类代码虽然有点多，但是非常简单，结合限流降权流程图，我们很容易就能找到几个关键的地方。
 * 1.当漏桶未满时，请求将一如既往地向漏桶中流入，注释①处。
 * 2.当漏桶已满时，可以对请求做降权处理，比如我们可以将请求存入更加容易水平扩展且吞吐量高的MQ中，稍后会有相关组件从MQ中消费请求，然后再次尝试提交，注释②处。
 * 3.在注释③处，漏桶按照固定的速率对数据进行处理，这样将不会冲击到一些稀缺资源由于请求过多而出现崩溃的情况。
 */
public class RateLimiterBucket {
    //一个简单的请求类
    static class Request {
        private final int data;

        public Request(int data) {
            this.data = data;
        }

        public int getData() {
            return data;
        }

        public String toString() {
            return "Request{" + "data=" + data + "}";
        }
    }

    //漏桶采用线程安全的容器，关于这一点在第4章中将为大家讲解
    private final ConcurrentLinkedQueue<Request> bucket = new ConcurrentLinkedQueue<>();
    //定义漏桶的上沿容量
    private final static int BUCKET_CAPACITY = 1000;
    //定义漏桶的下沿水流速率，每秒匀速放行10个request
    private final RateLimiter rateLimiter = RateLimiter.create(10.0D);
    //提交请求时需要用到的Monitor
    private final Monitor requestMonitor = new Monitor();
    //处理请求时需要用到的Monitor
    private final Monitor handleMonitor = new Monitor();

    public void submitRequest(int data) {
        this.submitRequest(new Request(data));
    }

    //该方法主要用于接受来自客户端提交的请求数据
    private void submitRequest(Request request) {
        // 注释① 当漏桶容量未溢出时
        if (requestMonitor.enterIf(requestMonitor.newGuard(() -> bucket.size() < BUCKET_CAPACITY))) {
            try {
                //在漏桶中加入新的request
                boolean result = bucket.offer(request);
                if (result) {
                    System.out.println(Thread.currentThread() + " submit request: " + request.getData() + " successfully.");
                } else {
                    //produce into MQ and will try again later.
                }
            } finally {
                requestMonitor.leave();
            }
        } else {
            System.out.println("The request：" + request.getData() + " will be down-dimensional handle due to bucket is overflow.");
            //produce into MQ and will try again later.
        }
    }

    //该方法主要从漏桶中匀速地处理相关请求
    public void handleRequest(Consumer<Request> consumer) {
        //若漏桶中存在请求，则处理
        if (handleMonitor.enterIf(handleMonitor.newGuard(() -> !bucket.isEmpty()))) {
            try {
                //注释③，匀速处理
                rateLimiter.acquire();
                //处理数据
                consumer.accept(bucket.poll());
            } finally {
                handleMonitor.leave();
            }
        }
    }
}