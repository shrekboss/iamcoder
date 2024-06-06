package org.coder.design.patterns._creational.singleton;

import java.net.Socket;
import java.sql.Connection;

/**
 * 5. volatile + Double-Check的单例模式
 * <p>
 * volatile + Double-Check的单例模式可满足多线程下的单例、懒加载
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
// final 不允许被继承
public final class DoubleCheckSingletonWithVolatile {

    // 实例变量
    private byte[] data = new byte[1024];

    // volatile
    private volatile static DoubleCheckSingletonWithVolatile instance = null;

    Connection conn;
    Socket socket;

    // 私有构造函数，不允许外部 new
    private DoubleCheckSingletonWithVolatile() {
        // this.conn 初始化
        // this.socket 初始化
    }

    public static DoubleCheckSingletonWithVolatile getInstance() {
        // 当 instance 为 null 时，进入同步代码块，同时刻判断避免了每次都需要进入同步代码块，可以提高效率
        if (null == instance) {
            // 只有一个线程能够获得 DoubleCheckSingleton.class 关联的 Monitor
            synchronized (DoubleCheckSingletonWithVolatile.class) {
                // 判断如果 instance 为 null 则创建
                if (null == instance) {
                    instance = new DoubleCheckSingletonWithVolatile();
                }
            }
        }
        return instance;
    }
}
