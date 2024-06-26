package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._3_problem;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 有何替代解决方案？静态方法实现 IdGenerator
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ReplaceSingletonSolution1 {

    private static final AtomicLong id = new AtomicLong(0);

    public static long getId() {
        return id.incrementAndGet();
    }

    public static void main(String[] args) {
        System.out.println(ReplaceSingletonSolution1.getId());
        System.out.println(ReplaceSingletonSolution1.getId());
        System.out.println(ReplaceSingletonSolution1.getId());
    }
}
