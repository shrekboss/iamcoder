package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._3_problem;

/**
 * 单例不支持有参数的构造函数第二种解决办法
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SingletonHasParamsConstructSolution2 {

    private static SingletonHasParamsConstructSolution2 instance = null;
    private final int paramA;
    private final int paramB;

    private SingletonHasParamsConstructSolution2(int paramA, int paramB) {
        this.paramA = paramA;
        this.paramB = paramB;
    }

    public synchronized static SingletonHasParamsConstructSolution2 getInstance(int paramA, int paramB) {
        if (instance == null) {
            instance = new SingletonHasParamsConstructSolution2(paramA, paramB);
        }
        return instance;
    }

    @Override
    public String toString() {
        return "SingletonSolution2{" +
                "paramA=" + paramA +
                ", paramB=" + paramB +
                '}';
    }

    public static void main(String[] args) {

        /**
         * SingletonSolution2{paramA=10, paramB=50}
         * SingletonSolution2{paramA=10, paramB=50}
         */
        SingletonHasParamsConstructSolution2 singleton1 = SingletonHasParamsConstructSolution2.getInstance(10, 50);
        System.out.println(singleton1);
        SingletonHasParamsConstructSolution2 singleton2 = SingletonHasParamsConstructSolution2.getInstance(20, 30);
        System.out.println(singleton2);
    }
}
