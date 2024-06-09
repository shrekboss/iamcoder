package org.coder.design.patterns._2_design_principle._5_dip.ioc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class JunitApplication {
    private static final List<TestCase> testCases = new ArrayList<>();

    public static void register(TestCase testCase) {
        testCases.add(testCase);
    }

    public static void main(String[] args) {

        for (TestCase testCase : testCases) {
            testCase.run();
        }
    }
}