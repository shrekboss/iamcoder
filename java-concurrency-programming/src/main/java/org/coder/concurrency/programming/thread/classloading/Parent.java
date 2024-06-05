package org.coder.concurrency.programming.thread.classloading;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Parent {
    static {
        System.out.println("The parent is initialized");
    }

    public static int y = 100;
}