package org.coder.concurrency.programming.pattern._5_future.ftp;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

// 模式角色：Promise.Promisor
public class FTPUploaderPromisor {

    // 模式角色：Promise.Promisor.compute
    public static Future<FTPUploader> newFTPUploaderPromise(String ftpServer,
                                                            String ftpUserName, String password, String serverDir) {
        Executor helperExecutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                Thread t = new Thread(command);
                t.start();
            }
        };
        return newFTPUploaderPromise(
                ftpServer, ftpUserName, password, serverDir, helperExecutor);
    }

    // 模式角色：Promise.Promisor.compute
    public static Future<FTPUploader> newFTPUploaderPromise(String ftpServer,
                                                            String ftpUserName, String password, String serverDir,
                                                            Executor helperExecutor) {
        Callable<FTPUploader> callable = () -> {
            String implClazz = System.getProperty("ftp.client.impl");
            // 类 FTPClientUtil 的源码
            if (null == implClazz) {
                implClazz =
                        "org.coder.concurrency.programming.pattern._5_future.ftp.FTPClientUtil"
                                + ".FTPClientUtil";
            }
            FTPUploader ftpUploader;
            ftpUploader = (FTPUploader) Class.forName(implClazz).newInstance();
            ftpUploader.init(ftpServer, ftpUserName, password, serverDir);
            return ftpUploader;
        };

        // task相当于模式角色：Promise.Promise & TaskExecutor 参与者实例
        final FutureTask<FTPUploader> task = new FutureTask<>(callable);
        helperExecutor.execute(task);
        return task;
    }

}