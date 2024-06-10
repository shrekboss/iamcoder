package org.coder.design.patterns._4_programming_specification._1_unit_test;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TestCaseRunner {
    public static void main(String[] args) {
        System.out.println("Run testToNumber()");
        new TextTestCases().testToNumber();

        System.out.println("Run testToNumber_nullorEmpty()");
        new TextTestCases().testToNumber_nullorEmpty();

        System.out.println("Run testToNumber_containsLeadingAndTrailingSpaces()");
        new TextTestCases().testToNumber_containsLeadingAndTrailingSpaces();

        System.out.println("Run testToNumber_containsMultiLeadingAndTrailingSpaces()");
        new TextTestCases().testToNumber_containsMultiLeadingAndTrailingSpaces();

        System.out.println("Run testToNumber_containsInvalidCharaters()");
        new TextTestCases().testToNumber_containsInvalidCharaters();
    }
}

