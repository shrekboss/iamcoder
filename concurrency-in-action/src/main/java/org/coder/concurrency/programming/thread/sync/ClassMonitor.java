package org.coder.concurrency.programming.thread.sync;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ClassMonitor {

    public static synchronized void method1() {
        System.out.println(Thread.currentThread().getName() + " enter to method1");
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public static synchronized void method2() {
//        System.out.println(Thread.currentThread().getName() + " enter to method2");
//        try {
//            TimeUnit.MINUTES.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    // 等价下面方法：
    public static void method2() {
        synchronized (ClassMonitor.class) {
            System.out.println(Thread.currentThread().getName() + " enter to method2");
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        //"T1" #11 prio=5 os_prio=31 tid=0x000000013f8d7000 nid=0x5803 waiting on condition [0x000000016fefa000]
        //   java.lang.Thread.State: TIMED_WAITING (sleeping)
        new Thread(ClassMonitor::method1, "T1").start();

        //"T2" #12 prio=5 os_prio=31 tid=0x000000013f8d7800 nid=0x7903 waiting for monitor entry [0x0000000300206000]
        //   java.lang.Thread.State: BLOCKED (on object monitor)
        new Thread(ClassMonitor::method2, "T2").start();
    }
}
