package org.coder.concurrency.programming.juc.atomic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 1.多线程下增加账号金额
 * 假设有10个人不断地向这个银行账号里打钱，每次都存入10元，因此这个个人账号在每一次被别人存入钱之后都会多10元。
 * 下面用多线程代码实现一下这样的场景。
 * 在下面的代码中，我们声明了一个全局的DebitCard对象的引用，并且用volatile关键字进行了修饰，其目的主要是为了使DebitCard对象引用的变化对其他线程立即可见，
 * 在每个线程中都会基于全局的DebitCard金额创建一个新的DebitCard，并且用新的DebitCard对象引用更新全局DebitCard对象的引用。
 * 运行下面的程序，我们能够看到控制台输出存在的问题。
 * <p>
 * 分明已有3人向这个账号存入了10元钱，为什么账号的金额却少于30元呢？不明白的读者可以参考笔者在《Java高并发编程详解：多线程与架构设计》一书第4章中介绍的方法自行分析，
 * 这里给点小提示：虽然被volatile关键字修饰的变量每次更改都可以立即被其他线程看到，但是我们针对对象引用的修改其实至少包含了如下两个步骤，获取该引用和改变该引用
 * （每一个步骤都是原子性的操作，但组合起来就无法保证原子性了）。
 */
public class AtomicReferenceExample1 {

    //volatile关键字修饰，每次对DebitCard对象引用的写入操作都会被其他线程看到
    //创建初始化DebitCard，账号金额为0元
    static volatile DebitCard debitCard = new DebitCard("Alex", 0);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread("T-" + i) {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        //读取全局DebitCard对象的引用
                        final DebitCard dc = debitCard;
                        //基于全局DebitCard的金额增加10元并且产生一个新的DebitCard
                        DebitCard newDC = new DebitCard(dc.getAccount(), dc.getAmount() + 10);
                        //输出全新的DebitCard
                        System.out.println(newDC);
                        //修改全局DebitCard对象的引用
                        debitCard = newDC;
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