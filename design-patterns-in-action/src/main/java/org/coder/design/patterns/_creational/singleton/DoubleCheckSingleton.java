package org.coder.design.patterns._creational.singleton;

import java.net.Socket;
import java.sql.Connection;

/**
 * 4. Double-Check的单例模式
 * <p>
 * Double-Check的单例模式支持类的懒加载
 * <p>
 * 缺点：性能相比 {@link LazySyncSingleton } 好一些，但是多线程的情况下有可能会引起空指针异常
 * <p>
 * a.根据 JVM 运行时指令重排序和 Happens-Before规则，conn、socket 和 instance 这三者之间的实例化顺序并无前后关系约束，那么极有可能
 * 是 instance 先被实例化，而 conn 和 socket 未完成实例化，未完成初始化的实例调用其方法将会抛出空指针 NPE 异常
 * <p>
 * b.重排序导致：23 或者 32
 * ====> instance = new DoubleCheckSingleton();
 * 1. 分配对象的内存空间
 * 2. 初始化对象
 * 3. 设置 instance 指向刚分配的内存地址
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// final 不允许被继承
public final class DoubleCheckSingleton {

    // 实例变量
    private byte[] data = new byte[1024];

    private static DoubleCheckSingleton instance = null;

    Connection conn;
    Socket socket;

    // 私有构造函数，不允许外部 new
    private DoubleCheckSingleton() {
        // this.conn 初始化
        // this.socket 初始化
    }

    public static DoubleCheckSingleton getInstance() {
        // 当 instance 为 null 时，进入同步代码块，同时刻判断避免了每次都需要进入同步代码块，可以提高效率
        if (null == instance) {
            // 只有一个线程能够获得 DoubleCheckSingleton.class 关联的 Monitor
            synchronized (DoubleCheckSingleton.class) {
                // 判断如果 instance 为 null 则创建
                if (null == instance) {
                    instance = new DoubleCheckSingleton();
                }
            }
        }
        return instance;
    }
}
