package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._3_problem;

import org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._2_globally_unique.IdGenerator;

/**
 * 有何替代解决方案？依赖注入
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ReplaceSingletonSolution2 {

    private final long id;

    // 2. 新的使用方式：依赖注入
    public ReplaceSingletonSolution2(IdGenerator idGenerator) {
        this.id = idGenerator.getId();
    }

    @Override
    public String toString() {
        return "ReplaceSingletonSolution2{}";
    }

    public static void main(String[] args) {
        IdGenerator idGenerator = IdGenerator.getInstance();
        ReplaceSingletonSolution2 replaceSingletonSolution2 = new ReplaceSingletonSolution2(idGenerator);
        System.out.println(replaceSingletonSolution2.id);
    }
}
