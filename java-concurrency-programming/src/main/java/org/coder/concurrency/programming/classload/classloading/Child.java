package org.coder.concurrency.programming.classload.classloading;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Child extends Parent {
    static {
        System.out.println("The child will be initialized");
    }

    public static int x = 10;

    public int test() {
        return 0;
    }

    public void test(int x) {

    }
}