package org.coder.concurrency.programming.classload.classloader;

/**
 * 扩展类加载器
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ExtClassLoader {
    public static void main(String[] args) {
        // 通过系统属性指定加载器的路径
        System.out.println(System.getProperty("java.ext.dirs"));
    }
}
