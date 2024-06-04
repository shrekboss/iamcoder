package org.coder.concurrency.programming.thread.deadlock;

import java.util.HashMap;

/**
 * 死锁：死循环引起的死锁(系统假死)
 *
 * HashMap 的数据结构不是线程安全的类，如果在多线程同时写操作的情况下不对其进行同步化封装，则很容易出现死循环引起的死锁，程序运行一段时间后 CPU
 * 等资源高居不下，各种诊断工具很难派上用场，因为死锁引起的进程往往会榨干 CPU 等资源，诊断工具由于缺少资源一时间也很难启动
 *
 * 没事别运行，后果自负！！！
 * 没事别运行，后果自负！！！
 * 没事别运行，后果自负！！！
 * 没事别运行，后果自负！！！
 *
 * @author <a href="mailto:yeqi@banniuyun.com">夜骐</a>
 * @since 1.0.0
 */
public class HashMapDeadLock {

    private final HashMap<String, String> map = new HashMap<>();

    public void add(String key, String value) {
        this.map.put(key, value);
    }

    public static void main(String[] args) {
        final HashMapDeadLock hashMapDeadLock = new HashMapDeadLock();
        for (int i = 0; i < 2; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < Integer.MAX_VALUE; j++) {
                    hashMapDeadLock.add(String.valueOf(j), String.valueOf(j));
                }
            });
//            thread.start();
        }
    }
}
