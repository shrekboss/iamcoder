package org.coder.concurrency.programming.pattern._8_balking;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class BalkingTest {

    public static void main(String[] args) {
        new DocumentEditThread(
                "/Users/crayzer/workspaces/iamcoder/concurrency-in-action/src/main/java/org/coder/concurrency/programming/pattern/_8_balking",
                "balking.txt").start();
    }
}
