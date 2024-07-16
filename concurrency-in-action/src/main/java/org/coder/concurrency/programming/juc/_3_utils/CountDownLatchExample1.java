package org.coder.concurrency.programming.juc._3_utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 第3章 并发包之工具类详解
 * 在日常的开发工作中，多线程高并发程序的开发固然是必不可少的，但是想要对多线程技术应用得当并不是一件容易的事情。
 * 随着Java版本的不断迭代，越来越多的并发工具逐渐被引入，尤其是从JDK1.5版本开始，这方面极大地减轻了开发者的负担，
 * 另一方面又提高了高并发程序执行的效率，这些并发工具都能够很好地完成某些特定场景下的特定功能。
 * <p>
 * 本章将为读者详细讲解Java并发包中包含的所有工具类的用法、使用场景，并且还会为读者介绍Google Guava所提供的一些并发工具类。
 * 相信通过对本章的学习，大家不仅能够开发处优雅高效的高并发程序，还可减少程序运行错误的发生概率。
 * <p>
 * 在笔者的第一本书《Java高并发编程详解：多线程与架构设计》高并发设计模式中详细解读了几十种常用的设计技巧，它们同样也可以应用在一些特定的场景中，
 * 但是笔者还是推荐如果能够直接使用Java并发包中的工具就直接使用，毕竟它们足够稳定，而且会随着JDK版本的升级不断优化和发展。
 * <p>
 * 3.1 CountDownLatch工具详解
 * 在《Java高并发编程详解：多线程与架构设计》一书的第23章“Latch设计模式”中非常清晰地讲解了Latch(门阀)设计模式的相关知识，
 * 当某项工作需要由若干项子任务并行地完成，并且只有在所有的子任务结束之后(正常结束或者异常结束)，当前主任务才能进入下一阶段，
 * CountDownLatch工具将是非常好用的工具，并且其所提供的操作方法还是线程安全的。
 * <p>
 * CountDownLatch(直译为倒计数门阀)，它的作用就与其名字所表达的意思一样，是指有一个门阀在等待着倒计数，直到计数器为0的时候才能打开，
 * 当然我们可以在门阀等待打开的时候指定超时时间。
 * <p>
 * A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
 * 这段文字来自JDK官方：“CountDownLatch是一个同步助手，允许一个或者多个线程等待一系列的其他线程执行结束”。
 * <p>
 * 3.1.1 等待所有子任务结束
 * 考虑一下这样一个场景，我们需要调用某个品类的商品，然后针对活动规则、会员等级、商品套餐等计算出陈列在页面的最终价格
 * （这个计算过程可能会比较复杂、耗时较长，因为可能要调用其他系统的接口，比如ERP、CRM等），最后将计算结果统一返回给调用方，如图3-1所示。
 * <p>
 * 虽然在真实的电商应用中也许不会存在这样的设计，但是笔者就以这个作为案例，演示当接口调用者向服务端传递了某品类ID后，服务端需要进行的一系列复杂的动作。
 * 在图3-1中，假设根据商品品类ID获取到了10件商品，然后分别对这10件商品进行复杂的划价计算，最后统一将结果返回给调用者。
 * 想象一下，即使忽略网络调用的开销时间，整个结果最终将耗时T=M(M为获取品类下商品的时间) + 10xN(N为计算每一件商品价格的平均时间开销)，
 * 整个串行化的过程中，总体的耗时还会随着N的数量增多而持续增长。
 * <p>
 * 那么，如果想要提高接口调用的响应速度应该如何操作呢?很明显，将某些串行化的任务并行化处理是一种非常不错的解决方案
 * （这些串行化任务在整体的运行周期中彼此之间互相独立）。改进之后的设计方案将变成如图3-2所示的样子。
 * <p>
 * 经过改进之后，接口响应的最终耗时T = M(M为获取品类下商品的时间) + Max(N)(N为计算每一件商品价格的开销时间)，
 * 简单开发程序模拟一下这样的一个场景，代码如下，在代码中读者将会看到CountDownLatch的基本使用方法。
 * <p>
 * 代码比较简单，而且在关键的地方笔者都增加了注释，我们将每一个商品的划价运算都交给了一个独立的子线程去执行，主线程等待最后所有子线程的执行全部结束，
 * 在上面的代码中，我们首次接触到了CountDownLatch的使用。
 * 1.注释①处构造CountDownLatch时需要给定一个不能小于0的int类型数字，数字的取值一般是我们给定子任务的数量。
 * 2.注释②处为每一件商品的划价运算开辟了对应的线程，使其能够并行并发运算(这里不太建议直接使用创建线程的方式，可以使用ExecutorService代替，
 * 在本书的第5章会有详细的讲解)。
 * 3.注释③处，执行countDown()方法，使计数器减一，表明子任务执行结束。这里需要注意的是，任务的结束并不一定代表着正常的结束，有可能是在运算的过程中出现错误，
 * 因此为了能够正确地执行countDown()，需要将该方法的调用放在finally代码块中，否则就会出现主线程(任务)await()方法永远不会退出阻塞的问题。
 * 4.注释④处调用await()方法，主(父)线程(main)将会被阻塞，直到所有的子线程完成了工作(计数器变为0)。
 * <p>
 * 3.1.2 CountDownLatch的其他方法及总结
 * CountDownLatch使用起来非常简单，但是就是这个简单的工具类，可以帮助我们很优雅地解决主任务等待所有子任务都执行结束之后再进行下一步工作的场景。
 * 在《Java高并发编程详解：多线程与架构设计》一书中，我们为了开发Latch也是编写了不少代码，现在好了，直接使用CountDownLatch就可以帮助我们完成相关的工作，具体步骤如下。
 * 1).CountDownLatch的构造非常简单，需要给定一个不能小于0的int数字。
 * 2).countDown()方法，该方法的主要作用是使得构造CountDownLatch指定的count计数器减一。
 * 如果此时CountDownLatch中计数器已经是0，这种情况下如果再次调用countDown()方法，则会被忽略，也就是说count的值最小只能为0。
 * 3).await()方法会使得当前的调用线程进入阻塞状态，直到count为0，当然其他线程可以将当前线程中断。同样，当count的值为0的时候，调用await方法将会立即返回，当前线程将不再被阻塞。
 * //定义一个计数器为2的Latch
 * CountDownLatch latch = new CountDownLatch(2);
 * //调用countDown方法，此时count=1
 * latch.countDown();
 * //调用countDown方法，此时count=0
 * latch.countDown();
 * //调用countDown方法，此时count仍然为0
 * latch.countDown();
 * //count已经为0，那么执行await将会被直接返回，不再进入阻塞
 * latch.await();
 * 4).await(long timeout, TimeUnit unit)是一个具备超时能力的阻塞方法，当时间到达给定的值以后，计数器count的值若还大于0，则会当前线程会退出阻塞。
 * //定义一个计数器为2的Latch
 * CountDownLatch latch = new CountDownLatch(2);
 * //调用await超时方法，10秒以后，如果latch的count仍旧大于0，那么当前线程将退出阻塞状态
 * latch.await(10, TimeUnit.SECONDS);
 * 5).getCount()方法，该方法将返回CountDownLatch当前的计数器数值，该返回值的最小值为0。
 */
public class CountDownLatchExample1 {

    public static void main(String[] args) throws InterruptedException {
        //首先获取商品编号的列表
        final int[] products = getProductsByCategoryId();
        //通过stream的map运算将商品编号转换为ProductPrice
        List<ProductPrice> list = Arrays.stream(products).mapToObj(ProductPrice::new).collect(Collectors.toList());
        //①定义CountDownLatch，计数器数量为子任务的个数
        final CountDownLatch latch = new CountDownLatch(products.length);
        //②为每一件商品的计算都开辟对应的线程
        list.forEach(pp -> new Thread(() -> {
            System.out.println(pp.getProdID() + "-> start calculate price.");
            try {
                //模拟其他的系统调用，比较耗时，这里休眠替代
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                //计算商品价格
                if (pp.prodID % 2 == 0) {
                    pp.setPrice(pp.prodID * 0.9D);
                } else {
                    pp.setPrice(pp.prodID * 0.71D);
                }
                System.out.println(pp.getProdID() + "-> price calculate completed.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //③计数器count down，子任务执行完成
                latch.countDown();
            }
        }).start());
        //④主线程阻塞等待所有子任务结束，如果有一个子任务没有完成则会一直等待
        latch.await();
        System.out.println("all of prices calculate finished.");
        list.forEach(System.out::println);
    }

    //根据品类ID获取商品列表
    private static int[] getProductsByCategoryId() {
        //商品列表编号为从1~10的数字
        return IntStream.rangeClosed(1, 10).toArray();
    }

    //商品编号与所对应的价格，当然真实的电商系统中不可能仅存在这两个字段
    private static class ProductPrice {
        private final int prodID;
        private double price;

        public ProductPrice(int prodID) {
            this(prodID, -1);
        }

        public ProductPrice(int prodID, double price) {
            this.prodID = prodID;
            this.price = price;
        }

//        public double getPrice() {
//            return price;
//        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getProdID() {
            return prodID;
        }

        @Override
        public String toString() {
            return "ProductPrice {prodID=" + prodID + ", price=" + price + "}";
        }
    }
}