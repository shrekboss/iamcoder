package org.coder.design.patterns._3_patterns.creation._1_singleton.cases._2_globally_unique;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public final class IdGenerator {
    // AtomicLong是一个Java并发库中提供的一个原子变量类型,
    // 它将一些线程不安全需要加锁的复合操作封装为了线程安全的原子操作，
    // 比如下面会用到的incrementAndGet().
    private AtomicLong id = new AtomicLong(0);
    private static final IdGenerator instance = new IdGenerator();

    private final static CountDownLatch latch = new CountDownLatch(10);

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        return instance;
    }

    public long getId() {
        return id.incrementAndGet();
    }

    @Override
    public String toString() {
        return "IdGenerator{" +
                "id=" + id +
                '}';
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    //等待所有线程就绪
                    countDownLatch.await();
                    System.out.println(Thread.currentThread().getName() + " 开始时间：" + System.currentTimeMillis()
                            + "输出结果：" + IdGenerator.getInstance().getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.setName("线程-" + i);
            thread.start();
            System.out.println(thread.getName() + " 就绪时间：" + System.currentTimeMillis());
            //countDownLatch内部计数器减1
            countDownLatch.countDown();
        }
    }
}

