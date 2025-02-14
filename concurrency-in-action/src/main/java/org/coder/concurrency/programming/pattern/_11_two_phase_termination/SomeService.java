package org.coder.concurrency.programming.pattern._11_two_phase_termination;

import org.coder.concurrency.programming.pattern._11_two_phase_termination.alarm.AbstractTerminatableThread;
import org.coder.concurrency.programming.util.Tools;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SomeService {
    private final BlockingQueue<String> queue =
            new ArrayBlockingQueue<>(100);

    private final Producer producer = new Producer();
    private final Consumer consumer = new Consumer();

    private class Producer extends AbstractTerminatableThread {
        private int i = 0;

        @Override
        protected void doRun() throws Exception {
            queue.put(String.valueOf(i++));
            consumer.terminationToken.reservations.incrementAndGet();
        }
    }

    private class Consumer extends AbstractTerminatableThread {

        @Override
        protected void doRun() throws Exception {
            String product = queue.take();
            System.out.println("Processing product:" + product);

            // 模拟执行真正操作的时间消耗
            try {
                Tools.randomPause(100, 50);
            } finally {
                terminationToken.reservations.decrementAndGet();
            }

        }

    }

    public void shutdown() {
        // 生产者线程停止后再停止消费者线程
        producer.terminate(true);
        consumer.terminate();
    }

    public void init() {
        producer.start();
        consumer.start();
    }

    public static void main(String[] args) throws InterruptedException {
        SomeService ss = new SomeService();
        ss.init();
        Thread.sleep(500);
        ss.shutdown();
    }
}