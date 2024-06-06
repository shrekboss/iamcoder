package org.coder.design.patterns._3_patterns.creation.singleton;

/**
 * 3. 懒汉式 + 同步方法的单例模式
 * <p>
 * 懒汉式 + 同步方法的单例模式可以保证实例的唯一性，同时也能支持类的懒加载
 * <p>
 * 缺点：性能太差，getInstance 方法只能在同一时刻被一个线程所访问
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// final 不允许被继承
public final class LazySyncSingleton {

    // 实例变量
    private byte[] data = new byte[1024];

    private static LazySyncSingleton instance = null;

    // 私有构造函数，不允许外部 new
    private LazySyncSingleton() {
    }

    public static synchronized LazySyncSingleton getInstance() {
        if (null == instance) {
            instance = new LazySyncSingleton();
        }
        return instance;
    }
}
