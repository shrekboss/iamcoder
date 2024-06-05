package org.coder.concurrency.programming.thread.classloading;

/**
 * 1. 输出 1，1
 * 2. 如果将【注释2】的代码移动到【注释1】，输出结果是：0， 1，由于 x 显示赋值，因此 0 才是所期望的正确的复制
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Singleton {


    //【注释1】
    private static int x = 0;

    private static int y;

    //【注释2】
    private static Singleton instance = new Singleton();


    private Singleton() {
        x++;
        y++;
    }

    public static Singleton getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Singleton singleton = Singleton.getInstance();
        System.out.println(singleton);
        System.out.println(singleton.x);
        System.out.println(singleton.y);
    }
}
