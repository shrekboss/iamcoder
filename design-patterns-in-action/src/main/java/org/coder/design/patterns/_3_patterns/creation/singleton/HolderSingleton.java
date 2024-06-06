package org.coder.design.patterns._3_patterns.creation.singleton;

/**
 * 6. Holder 方式的单例模式
 * <p>
 * HolderSingleton 类的初始化过程中并不会创建 HolderSingleton 的实例，Holder 类中定义了 HolderSingleton 的静态变量，并且直接进行了
 * 初始化，当 Holder 被主动引用的时候则会创建 HolderSingleton 的实例，HolderSingleton 实例的创建过程在 Java 程序编译时期收集
 * 至 <clinit>() 方法，该方法又是同步方法，同步方法可以保证内存的可见性、JVM 指令的顺序性和原子性、Holder 单例模式是最好的设计之一，也是目
 * 前使用比较广泛的设计之一，另外一个是 {@link EnumSingleton} 或者 {@link EnumLazySingleton}
 * <p>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// final 不允许被继承
public final class HolderSingleton {

    // 实例变量
    private byte[] data = new byte[1024];

    // 私有构造函数，不允许外部 new
    private HolderSingleton() {
    }

    // 在静态内部类中持有 HolderSingleton 的实例，并且可以直接被初始化
    private static class Holder {
        private static HolderSingleton instance = new HolderSingleton();
    }

    // 调用 getInstance 方法，事实上是获得 Holder 的 instance 静态属性
    public static HolderSingleton getInstance() {
        return Holder.instance;
    }

}
