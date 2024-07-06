package org.coder.concurrency.programming.pattern._5_future.ftp;

import org.coder.concurrency.programming.util.Debug;
import org.coder.concurrency.programming.util.Tools;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DataSyncTask implements Runnable {
    private final Map<String, String> taskParameters;

    public DataSyncTask(Map<String, String> taskParameters) {
        this.taskParameters = taskParameters;
    }

    @Override
    public void run() {
        String ftpServer = taskParameters.get("server");
        String ftpUserName = taskParameters.get("userName");
        String password = taskParameters.get("password");
        String serverDir = taskParameters.get("serverDir");

        /**
         * 接口 FTPUploader 用于对FTP客户端进行抽象。
         *
         * 初始化FTP客户端实例。
         */
        Future<FTPUploader> ftpClientUtilPromise =
                FTPUploaderPromisor.newFTPUploaderPromise(ftpServer,
                        ftpUserName, password, serverDir);

        // 查询数据库生成本地文件
        generateFilesFromDB();

        FTPUploader ftpClientUtil = null;
        try {
            // 获取初始化完毕的FTP客户端实例
            ftpClientUtil = ftpClientUtilPromise.get();
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        // 上传文件
        uploadFiles(ftpClientUtil);
        // 省略其他代码
    }

    private void generateFilesFromDB() {
        Debug.info("generating files from database...");

        // 模拟实际操作所需的耗时
        Tools.randomPause(1000, 500);

        // 省略其他代码
    }

    private void uploadFiles(FTPUploader ftpClientUtil) {
        Set<File> files = retrieveGeneratedFiles();
        for (File file : files) {
            try {
                ftpClientUtil.upload(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected Set<File> retrieveGeneratedFiles() {
        Set<File> files = new HashSet<File>();

        // 模拟生成本地文件
        File currDir = new File(Tools.getWorkingDir(
                "../target/classes/org/coder/concurrency/programming/pattern/_5_future/ftp"));
        Collections.addAll(files, Objects.requireNonNull(currDir.listFiles(
                (dir, name) -> new File(dir, name).isFile()
                        && name.endsWith(".class"))));

        // 省略其他代码
        return files;
    }

}