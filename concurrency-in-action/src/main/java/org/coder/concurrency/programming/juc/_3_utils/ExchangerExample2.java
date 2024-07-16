package org.coder.concurrency.programming.juc._3_utils;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 在定义Exchanger类的时候必须指定对应的数据类型(Exchanger是一个泛型类)，同时在调用exchange方法的时候也必须传递对应类型的数据，
 * 如果我们只希望一个线程生成数据，另外一个线程处理数据。也就是说其中A线程会用到B线程交换过来的数据，而B线程压根不会用到(忽略)A线程交换过来的数据，
 * 该怎么做呢？请看下面的示例。
 * <p>
 * 分析上面的代码可以看到在注释①处，Generator线程虽然进行了数据交换，但是她并不关心另外一个Consumer线程所交换过来的数据，
 * 同样在注释②处，Consumer线程直接使用null值作为exchange的数据对象，运行上面的程度会看到Generator线程和Consumer
 * 线程之间的协同工作过程以及关闭的过程。
 */
public class ExchangerExample2 {

    public static void main(String[] args) throws InterruptedException {
        //定义数据类型为String的Exchanger
        final Exchanger<String> exchanger = new Exchanger<>();
        //定义StringGenerator线程，并将该线程命名为Generator
        StringGenerator generator = new StringGenerator(exchanger, "Generator");
        //定义StringConsumer线程，并将该线程命名为Consumer
        StringConsumer consumer = new StringConsumer(exchanger, "Consumer");
        //分别启动线程
        generator.start();
        consumer.start();
        //休眠1分钟后，将Generator和Consumer线程关闭
        TimeUnit.MINUTES.sleep(2);
        consumer.close();
        generator.close();
    }

    //定义Closable接口
    private interface Closable {
        //关闭方法
        void close();

        //判断当前线程是否被关闭
        boolean closed();
    }

    private abstract static class ClosableThread extends Thread implements Closable {
        protected final Exchanger<String> exchanger;
        private volatile boolean closed = false;

        private ClosableThread(Exchanger<String> exchanger, final String name) {
            super(name);
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            //当前线程未关闭时不断执行doExchange()方法
            while (!closed()) {
                this.doExchange();
            }
        }

        //抽象方法
        protected abstract void doExchange();

        //关闭当前线程
        @Override
        public void close() {
            System.out.println(Thread.currentThread() + " will be closed.");
            this.closed = true;
            this.interrupt();
        }

        @Override
        public boolean closed() {
            return this.closed || this.isInterrupted();
        }
    }

    private static class StringGenerator extends ClosableThread {
        private char initialChar = 'A';

        private StringGenerator(Exchanger<String> exchanger, String name) {
            super(exchanger, name);
        }

        @Override
        protected void doExchange() {
            //模拟复杂的数据生成过程
            String str = "";
            for (int i = 0; i < 3; i++) {
                randomSleep();
                str += (initialChar++);
            }
            try {
                //① 如果当前线程未关闭，则执行Exchanger的exchange方法
                if (!this.closed()) {
                    exchanger.exchange(str);
                }
            } catch (InterruptedException e) {
                //如果closed()方法之后执行了close方法，那么执行中断操作时此处会捕获到中断信号
                System.out.println(Thread.currentThread() + "received the close signal.");
            }
        }
    }

    private static class StringConsumer extends ClosableThread {
        private StringConsumer(Exchanger<String> exchanger, String name) {
            super(exchanger, name);
        }

        @Override
        protected void doExchange() {
            //randomSleep();
            try {
                //② 如果当前线程未关闭，则执行Exchanger的exchange方法
                if (!this.closed()) {
                    String data = exchanger.exchange(null);
                    System.out.println("received the data:" + data);
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread() + "received the close signal.");
            }
        }
    }

    //随机休眠
    private static void randomSleep() {
        try {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5));
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }
}