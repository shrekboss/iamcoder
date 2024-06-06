package org.coder.concurrency.programming.classload.classloader;

/**
 * 系统类加载器，也是自定义类加载器的默认父加载器
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class ApplicationClassLoader {

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.class.path"));

        // sun.misc.Launcher$AppClassLoader@18b4aac2
        // 系统类加载器的父加载器是扩展类加载器，同时它也是自定义类加载器的默认父加载器
        System.out.println(ApplicationClassLoader.class.getClassLoader());
    }
}
