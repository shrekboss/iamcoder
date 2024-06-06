package org.coder.design.patterns._3_patterns.creation.singleton;

/**
 * 1. 饿汉式单例模式
 * <p>
 * 饿汉式的单例设计模式可以保证多个线程下的唯一实例，getInstance 方法性能比较高，但是无法进行懒加载
 * <p>
 * 缺点：如果一个类中的成员变量都是比较重的资源，那么不适用这种方式
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// final 不允许被继承
public final class HungrySingleton {

    // 实例变量
    private byte[] data = new byte[1024];

    private static HungrySingleton instance = new HungrySingleton();

    // 私有构造函数，不允许外部 new
    private HungrySingleton() {
    }

    public static HungrySingleton getInstance() {
        return instance;
    }
}
