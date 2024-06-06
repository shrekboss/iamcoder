package org.coder.concurrency.programming.thread.hook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Hook 线程实战 --- 模拟一个防止重复启动的程序
 * 需求：防止某个程序被重复启动，在进程启动时会创建一个 .lock 文件，进程收到中断信息的时候会删除这个 lock 文件
 * <p>
 * 需要注意的事项：
 * </p>
 * <p>
 * 1. Hook 线程只有在收到退出信号的时候会被执行，如果在 kill 的时候使用了参数 -9，那么 Hook 线程不会得到执行，进程会立即退出，因此 .lock
 * 文件得不到清理
 * </p>
 * </p>
 * 2. Hook 线程中也可以执行一些资源释放的工作，比如关闭文件句柄、socket 链接、数据库 connection 等。
 * </p>
 * <p>
 * 3. 尽量不要在 Hook 线程中执行一些非常耗时非常长的操作，因为其会导致程序迟迟不能退出。
 * </p>
 *
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class PreventDuplicated {

    private final static String LOCK_PATH = "/Users/crayzer/workspaces/iamcoder/java-concurrency-programming/src/main/java/org/coder/concurrency/programming/thread/hook";
    private final static String LOCK_FILE = ".lock";
    private final static String PERMISSIONS = "rw-------";

    public static void main(String[] args) throws IOException {
        // 检查是否存在 .lock 文件
        checkRunning();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("The program received kill SIGNAL.");
            getLockPath().toFile().delete();
        }));

        for (; ; ) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkRunning() throws IOException {
        Path path = getLockPath();
        if (path.toFile().exists()) {
            throw new RuntimeException("The program already running.");
        }

        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(PERMISSIONS);
        Files.createFile(path, PosixFilePermissions.asFileAttribute(perms));
    }

    private static Path getLockPath() {
        return Paths.get(LOCK_PATH, LOCK_FILE);
    }
}
