package org.coder.concurrency.programming.pattern._12_worker_thread._reusable;

public class PipeException extends Exception {
    private static final long serialVersionUID = -8818161637315267758L;
    /**
     * 抛出异常的Pipe实例。
     */
    public final Pipe<?, ?> sourcePipe;

    /**
     * 抛出异常的Pipe实例在抛出异常时所处理的输入元素。
     */
    public final Object input;

    public PipeException(Pipe<?, ?> sourcePipe, Object input, String message) {
        super(message);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }

    public PipeException(Pipe<?, ?> sourcePipe, Object input, String message, Throwable cause) {
        super(message, cause);
        this.sourcePipe = sourcePipe;
        this.input = input;
    }
}