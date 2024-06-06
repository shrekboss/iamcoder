package org.coder.concurrency.programming.classload.classloader;

/**
 * 根加载器
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class BootstrapClassLoader {
    public static void main(String[] args) {
        // 根加载器是获取不到引用的
        System.out.println("Bootstrap:" + String.class.getClassLoader());
        // 通过系统属性指定根加载器的路径
        System.out.println(System.getProperty("sun.boot.class.path"));

    }
}
