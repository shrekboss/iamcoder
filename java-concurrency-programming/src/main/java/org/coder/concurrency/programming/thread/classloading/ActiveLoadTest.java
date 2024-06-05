package org.coder.concurrency.programming.thread.classloading;

import java.util.Random;

/**
 * 类的【首次】【主动】使用，导致类的加载和初始化
 * 1. 通过 new 关键字会导致类的初始化
 * 2. 访问类的静态变量
 * 3. 访问类的静态方法
 * 4. 类的反射操作
 * 5. 初始化子类会导致父类的初始化
 * 6. 启动类：执行 main 函数所在的类会导致该类的初始化
 * <p>
 * 除了上述 6 种情况，其余的都称为被动使用，不会导致类的加载和初始化
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ActiveLoadTest {

    /**
     * 初始化成功：静态代码块会输出
     */
    static {
        System.out.println("I will be initialized");
    }

    // 静态变量
    public static int x = 10;
    /**
     * 静态常量，在其他类中使用 MAX 不会导致 ActiveLoadTest 的初始化，静态代码块不会输出
     * {@link GlobalConstants}
     */
    public final static int MAX = 100;
    /**
     * 虽然 RANDOM 是静态常量，但是由于计算复杂，只有初始化之后才能得到结果，因此在其他类中使用 RANDOM 会导致 ActiveLoadTest 的初始化
     * {@link GlobalConstants}
     */
    public final static int RANDOM = new Random().nextInt();


    public static void test() {
    }

    // 实验效果不明显！！！直接背
    // 实验效果不明显！！！直接背
    // 实验效果不明显！！！直接背
    // 实验效果不明显！！！直接背

    // 6. 【首次】启动类：执行 main 函数所在的类会导致该类的初始化
    public static void main(String[] args) throws ClassNotFoundException {
        // 1. 【首次】通过 new 关键字会导致类的初始化
//        ActiveLoadTest activeLoadTest = new ActiveLoadTest();
        // 1.1. 构造某个类的数组时并不会导致该类的初始化(验证不一定成功)
//        ActiveLoadTest[] activeLoadTests = new ActiveLoadTest[10];

        // 2. 【首次】访问类的静态变量
        System.out.println(org.coder.concurrency.programming.thread.classloading.ActiveLoadTest.x);
        // 2.1. 在其他类中使用 MAX 不会导致 ActiveLoadTest 的初始化，静态代码块不会输出
//        System.out.println(ActiveLoadTest.MAX);


        // 3. 【首次】访问类的静态方法
        org.coder.concurrency.programming.thread.classloading.ActiveLoadTest.test();
        // 4. 【首次】类的放射操作
        Class.forName("org.coder.concurrency.programming.thread.classloading.ActiveLoadTest");
        // 5. 【首次】初始化子类会导致父类的初始化
        System.out.println(Child.x);
        // 5.1. 【注意】通过子类使用父类的静态变量只会导致父类的初始化，子类则不会初始化
//        System.out.println(Child.y);
    }
}
