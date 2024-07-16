package org.coder.concurrency.programming.juc._3_utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 3.2 CyclicBarrier 工具详解
 * CyclicBarrier(循环屏障)，它也是一个同步助手工具，它允许多个线程在执行完相应的操作之后彼此等待共同到达一个屏障点(barrier point)。
 * CyclicBarrier非常适合用于某个串行化任务被分拆成若干个并行执行的子任务，当所有的子任务都执行结束之后再继续接下来的工作。
 * 从这一点来看，Cyclic Barrier与CountDownLatch非常类似，但是它们之间的运行方式以及原理还是存在着比较大的差异的，
 * 并且CyclicBarrier所能支持的功能CountDownLatch是不具备的。比如，CyclicBarrier可以被重复使用，
 * 而CountDownLatch当计数器为0的时候就无法再次利用。
 * <p>
 * 3.2.1 等待所有子任务结束
 * 同样，我们还是使用3.1.1节中的例子演示如何使用CyclicBarrier，相同的场景下使用不同的工具还可以有助于理解它们之间的相同点和不同之处。
 * <p>
 * 运行上面的代码，其输出结果与3.1.1节中的运行结果完全一致，虽然同样都是进行子任务并行化的执行并且等待所有子任务结束，但是它们的执行方式却存在着很多的差异。
 * 在子任务线程中，当执行结束后调用await方法使当前的子线程进入阻塞状态，直到其他所有的子线程都结束了任务的运行之后，它们才能退出阻塞，下面来解释一下代码注释中几个关键的地方。
 * 1.在注释①处定义了一个CyclicBarrier，虽然要求传入大于0的int数字，但是它所有代表的含义是“分片”而不再是计数器，虽然它的作用与计数器几乎类似。
 * 2.在注释②处定义了一个ThreadList，用于存放已经被启动的线程，其主要作用就是为了后面等待所有任务结束而做准备。
 * 3.在注释③处，子任务线程运行(正常/异常)结束后，调用await方法等待其他子线程也运行结束到达一个共同的barrier point，该await方法还会返回一个int的值，
 * 该值所代表的意思是当前任务到达的次序(说白了就是这个线程是第几个运行完相关逻辑单元的)。
 * 4.在注释④处，逐一调用每一个子线程的join方法，使当前线程进入阻塞状态等待所有的子线程运行结束。
 * <p>
 * 注释④处给出的等待子任务线程运行结束的方案虽然能够达到目的，但是这种方式不太优雅，我们可以通过一个小技巧使代码变得更加简洁。
 * ...省略
 * List<ProductPrice> list = Arrays.stream(products).mapToObj(ProductPrice::new).collect(toList());
 * //在定义CyclicBarrier给定parties时，使用parties的数量多一个
 * final CyclicBarrier barrier = new CyclicBarrier(list.size() + 1);
 * ...
 * //在主线程中调用await方法，等待其他子任务线程也到达barrier point
 * barrier.await();
 * ...省略
 * <p>
 * 通过为barrier的数量多加一个分片的方式，将主线程也当成子任务线程，这个时候，主线程就可以调用await线程，等待其他线程运行结束并且到达barrier point，进而退出阻塞进入下一个运算逻辑中。
 */
public class CyclicBarrierExample1 {

    public static void main(String[] args) {

        // 根据商品品类获取一组商品ID
        final int[] products = getProductsByCategoryId();
        // 通过转换将商品编号转换为ProductPrice
        List<ProductPrice> list = Arrays.stream(products).mapToObj(ProductPrice::new).collect(Collectors.toList());
        //① 定义CyclicBarrier，指定parties为子任务数量
        final CyclicBarrier barrier = new CyclicBarrier(list.size());
        //② 用于存放线程任务的list
        final List<Thread> threadList = new ArrayList<>();

        list.forEach(pp -> {
            Thread thread = new Thread(() -> {
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
                    try {
                        //③在此等待其他子线程达到barrier point
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
            threadList.add(thread);
            thread.start();
        });
        //④ 等待所有子任务线程结束
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
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

        //			public double getPrice() {
//				return price;
//			}
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