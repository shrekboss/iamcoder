package org.coder.design.patterns._4_design_patterns._1_creation._1_singleton._template_code;

import java.util.stream.IntStream;

/**
 * 7.1. 枚举 + Holder 方式的单例模式
 * <p>
 * 枚举 + Holder 方式的单例模式在 {@link EnumSingleton} 的基础上支持懒加载
 * <p>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// 枚举类型本身也是 final的，不允许被继承
public final class EnumLazySingleton {

    // 实例变量
    private byte[] data = new byte[1024];

    private EnumLazySingleton() {

    }

    public byte[] getData() {
        return data;
    }

    // 使用枚举充当 holder
    private enum EnumHolder {

        INSTANCE;
        private EnumLazySingleton instance;

        EnumHolder() {
            this.instance = new EnumLazySingleton();
        }

        private EnumLazySingleton getSingleton() {
            return instance;
        }

        private static EnumLazySingleton getInstance() {
            return INSTANCE.getSingleton();
        }
    }

    public static EnumLazySingleton getInstance() {
        return EnumHolder.getInstance();
    }

    public static void main(String[] args) {
        IntStream.range(0, 5).mapToObj(i -> new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " --> " + (EnumLazySingleton.getInstance().data[i] = (byte) i));
        }, "Test-Tread-" + i)).forEach(Thread::start);
    }

}
