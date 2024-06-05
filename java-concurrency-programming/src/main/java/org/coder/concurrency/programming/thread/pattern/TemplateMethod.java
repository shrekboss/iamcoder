package org.coder.concurrency.programming.thread.pattern;

/**
 * 模板设计模式在 Thread 中的应用
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class TemplateMethod {

    public static void main(String[] args) {
        TemplateMethod t1 = new TemplateMethod() {
            @Override
            protected void wrapPrint(String message) {
                System.out.println(" * " + message + " * ");
            }
        };
        t1.print("Hello Thread");

        TemplateMethod t2 = new TemplateMethod() {
            @Override
            protected void wrapPrint(String message) {
                System.out.println(" + " + message + " + ");
            }
        };
        t2.print("Hello Thread");
    }

    /**
     * print 方法相当于 Thread 的 start 方法，程序由父类控制，并且是 final 修饰的，不允许被重写
     */
    public final void print(String message) {
        System.out.println("################################");
        wrapPrint(message);
        System.out.println("################################");
    }

    /**
     * wrapPrint 则类似 Thread 的 run 方法，子类只需要实现想要的逻辑任务即可
     * <p>
     * 方法修饰注意：protected
     */
    protected void wrapPrint(String message) {
    }
}
