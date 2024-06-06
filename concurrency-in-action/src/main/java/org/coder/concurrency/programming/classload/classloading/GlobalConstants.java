package org.coder.concurrency.programming.classload.classloading;

import java.util.Random;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class GlobalConstants {
    static {
        System.out.println("The GlobalConstants will be initialized.");
    }

    public final static int MAX = 100;
    public final static int RANDOM = new Random().nextInt();
}
