package org.coder.concurrency.programming.pattern._7_context;

/**
 * 
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ActionContextExample {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
//                ActionContext.get().setConfiguration(new Configuration());
//                System.out.println(ActionContext.get().getConfiguration());
            }).start();
        }
    }
}
