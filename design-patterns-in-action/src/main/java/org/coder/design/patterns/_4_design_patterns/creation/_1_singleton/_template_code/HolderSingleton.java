package org.coder.design.patterns._4_design_patterns.creation._1_singleton._template_code;

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

    /**
     * 防止 反射 攻击！
     */
    private static boolean initialized = false;

    // 私有构造函数，不允许外部 new
    private HolderSingleton() {
        synchronized (HolderSingleton.class) {
            if (!initialized) {
                initialized = true;
            } else {
                throw new RuntimeException("单例已被侵犯");
            }
        }
    }

    /**
     * 在静态内部类中持有 HolderSingleton 的实例，并且可以直接被初始化
     * <p>
     * 这种写法非常巧妙：
     * 对于内部类 Holder，它是一个饿汉式的单例实现，在 Holder 初始化的时候会
     * 由 ClassLoader 来保证同步，使 instance 是一个真·单例。
     * 同时，由于 Holder 是一个内部类，只在外部类的 HolderSingleton 的 getInstance() 中被使用，
     * 所以它被加载的时机也就是在getInstance()方法第一次被调用的时候。
     * <p>
     * 从内部看是一个饿汉式的单例，但是从外部看来，又的确是懒汉式的实现。
     */
    private static class Holder {
        private static HolderSingleton instance = new HolderSingleton();
    }

    // 调用 getInstance 方法，事实上是获得 Holder 的 instance 静态属性
    public static HolderSingleton getInstance() {
        return Holder.instance;
    }

    public static void main(String[] args) {

        System.out.println(HolderSingleton.getInstance());
        System.out.println(HolderSingleton.getInstance());
        System.out.println(HolderSingleton.getInstance());
    }

    // ============= 反射验证时打开 ============================
//    public static void main(String[] args) {
//        try {
//
//            Class<?> clazz = HolderSingleton.class;
//            Constructor c = clazz.getDeclaredConstructor(null);
//            c.setAccessible(true);
//            Object o = c.newInstance();
//            System.out.println(o);
//            Object o1 = c.newInstance();
//            System.out.println(o1);
//            Object o2 = c.newInstance();
//            System.out.println(o2);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    // ============= 反射验证时打开 ============================

}
