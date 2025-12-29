package org.coder.concurrency.programming.pattern._7_context;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.Thread.currentThread;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ThreadLocalExample {

    public static void main(String[] args) throws InterruptedException {

        // 简单测试
//        testPoint1();

        // 通过重写 initialValue() 方法进行数据的初始化，如下面的代码所示，线程并未对 threadLocal 进行 set 操作，但是还可以通过 get
        // 方法得到一个初始值，通过输出信息也不难看出，每个线程通过 get 方法获取的值都是不一样的(线程私有的数据拷贝)。
        testPoint2();
        //
//        testPoint3();
    }

    public static void testPoint1() {
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        IntStream.range(0, 10)
                .forEach(i -> new Thread(() ->
                        {
                            try {

                                threadLocal.set(i);
                                System.out.println(currentThread() + " set i " + threadLocal.get());
                                TimeUnit.SECONDS.sleep(1);
                                System.out.println(currentThread() + " get i " + threadLocal.get());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start()
                );
    }

    public static void testPoint2() {
        ThreadLocal<Object> threadLocal = ThreadLocal.withInitial(Object::new);

        new Thread(() ->
                System.out.println(threadLocal.get())
        ).start();
        System.out.println(threadLocal.get());
    }

    public static void testPoint3() throws InterruptedException {
        ThreadLocal<byte[]> threadLocal = new ThreadLocal<>();
        TimeUnit.SECONDS.sleep(30);

        threadLocal.set(new byte[1024 * 1024 * 100]);
        threadLocal.set(new byte[1024 * 1024 * 100]);
        threadLocal.set(new byte[1024 * 1024 * 100]);

        // 当 ThreadLocal 被显示地指定为 null 之后，执行 GC操作，此时堆内存中的 ThreadLocal 被回收，同时 ThreadLocalMap 中的
        // Entry.key 也称为 null，但是 value 将不会被释放，除非当前线程已经结束了生命周期的 Thread 引用被垃圾回收器回收。
        threadLocal = null;

        currentThread().join();
    }
}
