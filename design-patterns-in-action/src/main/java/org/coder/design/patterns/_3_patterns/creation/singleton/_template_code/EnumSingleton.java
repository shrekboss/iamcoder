package org.coder.design.patterns._3_patterns.creation.singleton._template_code;

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

    //
    public static EnumSingleton getInstance() {
        return INSTANCE;
    }

}
