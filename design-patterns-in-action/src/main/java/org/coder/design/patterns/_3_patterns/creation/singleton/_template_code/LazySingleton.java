package org.coder.design.patterns._3_patterns.creation.singleton._template_code;

/**
 * 2. 懒汉式的单例模式
 * <p>
 * 懒汉式的单例设计模式不能保证实例的唯一性，但是可以保证实例的懒加载
 * <p>
 * 缺点：无法保证实例的唯一性
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// final 不允许被继承
public final class LazySingleton {

    // 实例变量
    private byte[] data = new byte[1024];

    private static LazySingleton instance = null;

    // 私有构造函数，不允许外部 new
    private LazySingleton() {
    }

    public static LazySingleton getInstance() {
        if (null == instance) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
