package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._template_code;

import java.util.stream.IntStream;

/**
 * 7. 枚举方式的单例模式
 * <p>
 * 枚举方式的单例模式是线程安全且只能被实例化一次，但是枚举类型不能够懒加载
 * 使用枚举的方式实现单例模式是《Effective Java》作者力推的方式，是最好的设计之一，也是目前使用比较广泛的设计之一，另外一个是
 * {@link HolderSingleton}
 * <p>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// 枚举类型本身也是 final的，不允许被继承
public enum EnumSingleton {

    INSTANCE;
    // 实例变量
    private byte[] data = new byte[1024];

    EnumSingleton() {
        System.out.println("INSTANCE will be initialized immediately.");
    }

    /**
     * 这样的方法意义不大：可以直接使用 EnumSingleton.INSTANCE
     */
    public static EnumSingleton getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        IntStream.range(0, 5).mapToObj(i -> new Thread(() -> {
//            System.out.println(Thread.currentThread().getName() + " --> " + (EnumSingleton.getInstance().data[i] = (byte) i));
            System.out.println(Thread.currentThread().getName() + " --> " + (EnumSingleton.INSTANCE.data[i] = (byte) i));
        }, "EnumSingleton-Tread-" + i)).forEach(Thread::start);

        IntStream.range(0, 5).mapToObj(i -> new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " --> " + (EnumLazySingleton.getInstance().getData()[i] = (byte) i));
        }, "EnumLazySingleton-Tread-" + i)).forEach(Thread::start);
    }

}
