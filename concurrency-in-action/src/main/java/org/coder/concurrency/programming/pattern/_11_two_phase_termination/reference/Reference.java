package org.coder.concurrency.programming.pattern._11_two_phase_termination.reference;

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
public class Reference {

    //1M
    private final byte[] data = new byte[2 << 19];

    @Override
    protected void finalize() throws Throwable {
        System.out.println("the reference will be GC.");
    }
}
