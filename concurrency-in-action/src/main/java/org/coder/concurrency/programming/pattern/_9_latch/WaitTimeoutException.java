package org.coder.concurrency.programming.pattern._9_latch;

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
public class WaitTimeoutException extends Exception {
    public WaitTimeoutException(String message) {
        super(message);
    }
}
