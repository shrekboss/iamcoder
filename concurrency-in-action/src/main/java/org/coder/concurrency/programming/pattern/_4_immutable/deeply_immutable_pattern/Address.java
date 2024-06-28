package org.coder.concurrency.programming.pattern._4_immutable.deeply_immutable_pattern;

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
public class Address {
    private String province;
    private String city;

    public Address(String province, String city) {
        this.province = province;
        this.city = city;
    }
    // 只有getter方法，无setter方法..
}
