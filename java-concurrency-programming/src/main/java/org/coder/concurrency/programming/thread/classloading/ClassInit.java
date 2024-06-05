package org.coder.concurrency.programming.thread.classloading;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ClassInit {

    static class Parent {
        static int value = 10;

        static {
            value = 20;
        }
    }

    static class child extends Parent {
        static int i = value;

        public static void main(String[] args) {
            // 20
            // <clinit>() 中所有的类变量都会被赋予正确的值
            // <clinit>() 与类的构造函数有所不同，虚拟机会保证父类的 <clinit>() 最先执行
            System.out.println(child.i);
        }
    }
}
