package org.coder.concurrency.programming.pattern._13_active_objects;

/**
 * (what) 若方法不符合则其被转换为 Active 方法时会抛出异常
 * <p>
 * (why)
 * <p>
 * (how)
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class IllegalActiveMethod extends Exception {
    public IllegalActiveMethod(String message) {
        super(message);
    }
}
