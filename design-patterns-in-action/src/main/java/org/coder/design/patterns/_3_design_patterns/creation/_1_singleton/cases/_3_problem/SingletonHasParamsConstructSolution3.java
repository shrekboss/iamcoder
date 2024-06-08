package org.coder.design.patterns._3_design_patterns.creation._1_singleton.cases._3_problem;

import static org.coder.design.patterns._3_design_patterns.creation._1_singleton.cases._3_problem.SingletonHasParamsConstructSolution3.Config.PARAM_A;
import static org.coder.design.patterns._3_design_patterns.creation._1_singleton.cases._3_problem.SingletonHasParamsConstructSolution3.Config.PARAM_B;

/**
 * 单例不支持有参数的构造函数第三种解决办法
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SingletonHasParamsConstructSolution3 {

    static class Config {
        public static final int PARAM_A = 123;
        public static final int PARAM_B = 245;
    }

    private static SingletonHasParamsConstructSolution3 instance = null;
    private final int paramA;
    private final int paramB;

    private SingletonHasParamsConstructSolution3() {
        this.paramA = PARAM_A;
        this.paramB = PARAM_B;
    }

    public synchronized static SingletonHasParamsConstructSolution3 getInstance(int paramA, int paramB) {
        if (instance == null) {
            instance = new SingletonHasParamsConstructSolution3();
        }
        return instance;
    }

    @Override
    public String toString() {
        return "SingletonSolution3{" +
                "paramA=" + paramA +
                ", paramB=" + paramB +
                '}';
    }

    public static void main(String[] args) {

        SingletonHasParamsConstructSolution3 singleton = SingletonHasParamsConstructSolution3.getInstance(10, 50);
        System.out.println(singleton);

    }
}
