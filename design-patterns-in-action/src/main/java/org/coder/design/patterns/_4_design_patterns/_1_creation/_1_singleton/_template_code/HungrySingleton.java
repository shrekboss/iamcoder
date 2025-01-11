package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._template_code;

import java.io.*;

/**
 * 1. 饿汉式单例模式
 * <p>
 * 饿汉式的单例设计模式可以保证多个线程下的唯一实例，getInstance 方法性能比较高，但是无法进行懒加载
 * <p>
 * 缺点：
 * 1. 如果一个类中的成员变量都是比较重的资源，那么不适用这种方式；
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// final 不允许被继承
public final class HungrySingleton {

    // 实例变量
    private byte[] data = new byte[1024];

    private static HungrySingleton INSTANCE = new HungrySingleton();

    // 私有构造函数，不允许外部 new
    private HungrySingleton() {
    }

    public static HungrySingleton getInstance() {
        return INSTANCE;
    }

    /**
     * 防止序列化和反序列化后，破坏单例模式规则，启用 readResolve() 方法
     **/
    public Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

//    public static void main(String[] args) {
//
//        // 线程安全
//        int count = 1000;
//        CountDownLatch countDownLatch = new CountDownLatch(count);
//        for (int i = 0; i < count; i++) {
//            new Thread(() -> {
//                HungrySingleton instance = HungrySingleton.getInstance();
//                System.out.println(System.currentTimeMillis() + ":" + instance);
//                countDownLatch.countDown();
//            }).start();
//        }
//
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        /** 通过反射 那么这个实例仍然初始化 */
//        // try {
//        //     Class<?> clazz = Class.forName("org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._template_code.HungrySingleton");
//        //     Object instance1 = clazz.newInstance();
//        //     Object instance2 = clazz.newInstance();
//        //
//        //     System.out.println(instance1);
//        //     System.out.println(instance2);
//        // } catch (ClassNotFoundException e) {
//        //     e.printStackTrace();
//        // } catch (IllegalAccessException e) {
//        //     e.printStackTrace();
//        // } catch (InstantiationException e) {
//        //     e.printStackTrace();
//        // }
//    }

    /**
     * 测试点：序列化和反序列化后是否是同一个对象
     */
    public static void main(String[] args) {
        HungrySingleton s1;
        HungrySingleton s2 = HungrySingleton.getInstance();
        FileInputStream fis;
        ObjectInputStream ois;

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("serialize.obj");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(s2);
            oos.flush();
            oos.close();

            fis = new FileInputStream("serialize.obj");
            ois = new ObjectInputStream(fis);
            s1 = (HungrySingleton) ois.readObject();
            ois.close();

            System.out.println(s1);
            System.out.println(s2);
            System.out.println(s1 == s2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
