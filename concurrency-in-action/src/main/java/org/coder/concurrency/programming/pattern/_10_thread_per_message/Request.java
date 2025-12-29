package org.coder.concurrency.programming.pattern._10_thread_per_message;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class Request {

    private final String business;

    public Request(String business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return business;
    }
}
