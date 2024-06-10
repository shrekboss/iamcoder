package org.coder.design.patterns._4_programming_specification._1_unit_test;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Assert {

    public static void assertEquals(Integer expectedValue, Integer actualValue) {

        if (expectedValue != actualValue) {
            String message = String.format("Test failed, expected: %d, actual: %d.", expectedValue, actualValue);
            System.out.println(message);
        } else {
            System.out.println("Test succeeded.");
        }
    }

    public static boolean assertNull(Integer actualValue) {

        boolean isNull = actualValue == null;
        if (isNull) {
            System.out.println("Test succeeded.");
        } else {
            System.out.println("Test failed, the value is not null:" + actualValue);
        }
        return isNull;
    }

}
