package org.coder.concurrency.programming.pattern._5_future.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

//模式角色：Promise.Promisor、Promise.Result
public class FTPClientUtil {
    private final FTPClient ftp = new FTPClient();

    private final Map<String, Boolean> dirCreateMap = new HashMap<String, Boolean>();
    /*
     * helperExecutor是个静态变量，这使得newInstance方法在生成不同的FTPClientUtil实例时可以共用同一个线程池。
     * 模式角色：Promise.TaskExecutor
     */
    private volatile static ExecutorService helperExecutor;

    static {

        helperExecutor = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    // 私有构造器
    private FTPClientUtil() {

    }

    // 模式角色：Promise.Promisor.compute
    public static Future<FTPClientUtil> newInstance(final String ftpServer, final String userName, final String password) {

        Callable<FTPClientUtil> callable = () -> {
            FTPClientUtil self = new FTPClientUtil();
            self.init(ftpServer, userName, password);
            return self;
        };

        // task相当于模式角色：Promise.Promise
        final FutureTask<FTPClientUtil> task = new FutureTask<>(callable);
        helperExecutor.execute(task);
        return task;
    }

    private void init(String ftpServer, String userName, String password) throws Exception {

        FTPClientConfig config = new FTPClientConfig();
        ftp.configure(config);

        int reply;
        ftp.connect(ftpServer);

        System.out.print(ftp.getReplyString());

        reply = ftp.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new RuntimeException("FTP server refused connection.");
        }
        boolean isOK = ftp.login(userName, password);
        if (isOK) {
            System.out.println(ftp.getReplyString());

        } else {
            throw new RuntimeException("Failed to login." + ftp.getReplyString());
        }

        reply = ftp.cwd("~/subspsync");
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new RuntimeException("Failed to change working directory.reply:" + reply);
        } else {

            System.out.println(ftp.getReplyString());
        }

        ftp.setFileType(FTP.ASCII_FILE_TYPE);

    }

    public void upload(File file) throws Exception {
        InputStream dataIn = new BufferedInputStream(new FileInputStream(file), 1024 * 8);
        boolean isOK;
        String dirName = file.getParentFile().getName();
        String fileName = dirName + '/' + file.getName();
        ByteArrayInputStream checkFileInputStream = new ByteArrayInputStream("".getBytes());

        try {
            if (!dirCreateMap.containsKey(dirName)) {
                ftp.makeDirectory(dirName);
                dirCreateMap.put(dirName, null);
            }

            try {
                isOK = ftp.storeFile(fileName, dataIn);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload " + file, e);
            }
            if (isOK) {
                ftp.storeFile(fileName + ".c", checkFileInputStream);

            } else {

                throw new RuntimeException("Failed to upload " + file + ",reply:" + "," + ftp.getReplyString());
            }
        } finally {
            dataIn.close();
        }

    }

    public void disconnect() {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                // 什么也不做
            }
        }
    }
}