package org.coder.concurrency.programming.juc.atomic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 2.多线程下加锁增加账号金额
 * 那么我们该如何解决第1小节中出现的问题呢?相信很多人的第一反应是提出为共享数据加锁的解决方案，没错，通过加锁确实能够保证对DebitCard对象引用的原子性操作。
 * <p>
 * 相比较AtomicReferenceExample1.Java，我们在AtomicReferenceExample2.java中增加了同步代码块，用于确保同一时刻只能由一个线程对全局DebitCard的对象引用进行修改。
 * 运行修改之后的程序，我们会看到Alex的银行账号在以10作为步长逐渐递增。
 */
public class AtomicReferenceExample2 {

    static volatile DebitCard debitCard = new DebitCard("Alex", 0);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread("T-" + i) {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        synchronized (AtomicReferenceExample2.class) {
                            final DebitCard dc = debitCard;
                            DebitCard newDC = new DebitCard(dc.getAccount(), dc.getAmount() + 10);

                            System.out.println(newDC);
                            debitCard = newDC;
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(20));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

    }

}