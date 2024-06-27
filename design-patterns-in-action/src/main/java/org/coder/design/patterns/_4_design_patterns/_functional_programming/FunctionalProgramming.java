package org.coder.design.patterns._4_design_patterns._functional_programming;

/**
 * (what)
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class FunctionalProgramming {

    // 有状态函数: 执行结果依赖b的值是多少，即便入参相同，多次执行函数，函数的返回值有可能不同，因为b值有可能不同。
    int b;
    int increase(int a) {
        return a + b;
    }

    // 无状态函数：执行结果不依赖任何外部变量值，只要入参相同，不管执行多少次，函数的返回值就相同
    int increase(int a, int b) {
        return a + b;
    }
}
