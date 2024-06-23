package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._cases._3_problem;

/**
 * 单例不支持有参数的构造函数第一种解决办法
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class SingletonHasParamsConstructSolution1 {

    private static SingletonHasParamsConstructSolution1 instance = null;
    private final int paramA;
    private final int paramB;

    private SingletonHasParamsConstructSolution1(int paramA, int paramB) {
        this.paramA = paramA;
        this.paramB = paramB;
    }

    public static SingletonHasParamsConstructSolution1 getInstance() {
        if (instance == null) {
            throw new RuntimeException("Run init() first.");
        }
        return instance;
    }

    public synchronized static SingletonHasParamsConstructSolution1 init(int paramA, int paramB) {
        if (instance != null){
            throw new RuntimeException("Singleton has been created!");
        }
        instance = new SingletonHasParamsConstructSolution1(paramA, paramB);
        return instance;
    }

    public static void main(String[] args) {
        // 先init，再使用，否则抛异常：Exception in thread "main" java.lang.RuntimeException: Run init() first.
        SingletonHasParamsConstructSolution1.init(10, 50);
        SingletonHasParamsConstructSolution1 singleton = SingletonHasParamsConstructSolution1.getInstance();
    }
}
