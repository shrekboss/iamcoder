package org.coder.concurrency.programming.thread.sync;

import java.util.concurrent.TimeUnit;

/**
 * 反编译：
 * cd /Users/crayzer/workspaces/iamcoder/java-concurrency-programming/src/main/java/org/coder/concurrency/programming/thread/sync
 * javac Mutex.java ---> 需要生成字节码文件 Mutex.class
 * javap -c Mutex
 * <p>
 * astore_<n>: 存储引用至本地变量表
 * aload_<n>: 从本地变量表加载引用
 * getstatic: 从 class 中获取静态属性
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Mutex {

    private final static Object MUTEX = new Object();

    public static void main(String[] args) {
        final Mutex mutex = new Mutex();

        for (int i = 0; i < 5; i++) {
            new Thread(mutex::accessResource).start();
        }
    }

    public void accessResource() {
        try {
            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
