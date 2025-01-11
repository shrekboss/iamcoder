package org.coder.concurrency.programming.pattern._10_thread_per_message.ftp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * 实现FTP文件下载。
 * 模式角色：SerialThreadConfinement.Serializer
 */
public class MessageFileDownloader {

    // 模式角色：SerialThreadConfinement.WorkerThread
    private final WorkerThread workerThread;

    public MessageFileDownloader(String outputDir, final String ftpServer,
                                 final String userName, final String password,
                                 final String servWorkingDir) throws Exception {
        Path path = Paths.get(outputDir);
        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }
        // workerThread = new WorkerThread(outputDir, ftpServer, userName,
        // password, servWorkingDir);
        workerThread = new FakeWorkerThread(outputDir, ftpServer, userName,
                password, servWorkingDir);
    }

    public void init() {
        workerThread.start();
    }

    public void shutdown() {
        workerThread.terminate();
    }

    public void downloadFile(String file) {
        workerThread.download(file);
    }
}