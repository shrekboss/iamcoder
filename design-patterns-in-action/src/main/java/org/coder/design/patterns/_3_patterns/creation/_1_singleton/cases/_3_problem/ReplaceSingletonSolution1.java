package org.coder.design.patterns._3_patterns.creation._1_singleton.cases._3_problem;

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
