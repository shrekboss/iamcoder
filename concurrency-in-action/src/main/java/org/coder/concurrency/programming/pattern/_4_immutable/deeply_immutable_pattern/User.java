package org.coder.concurrency.programming.pattern._4_immutable.deeply_immutable_pattern;

/**
 * (what) 深度不变模式
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class User {
    private String name;
    private int age;
    private Address addr;

    public User(String name, int age, Address addr) {
        this.name = name;
        this.age = age;
        this.addr = addr;
    }
    // 只有getter方法，无setter方法...
}

