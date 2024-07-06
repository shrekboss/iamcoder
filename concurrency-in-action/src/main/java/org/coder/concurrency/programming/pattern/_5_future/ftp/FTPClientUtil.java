package org.coder.concurrency.programming.pattern._5_future.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

//模式角色：Promise.Result
public class FTPClientUtil implements FTPUploader {
    final FTPClient ftp = new FTPClient();
    final Map<String, Boolean> dirCreateMap = new HashMap<>();

    public void init(String ftpServer, String userName, String password,
            String serverDir)
            throws Exception {

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
            throw new RuntimeException(
                    "Failed to login." + ftp.getReplyString());
        }

        reply = ftp.cwd(serverDir);
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new RuntimeException(
                    "Failed to change working directory.reply:"
                            + reply);
        } else {

            System.out.println(ftp.getReplyString());
        }

        ftp.setFileType(FTP.ASCII_FILE_TYPE);

    }

    @Override
    public void upload(File file) throws Exception {
        InputStream dataIn =
                new BufferedInputStream(new FileInputStream(file),
                        1024 * 8);
        boolean isOK;
        String dirName = file.getParentFile().getName();
        String fileName = dirName + '/' + file.getName();
        ByteArrayInputStream checkFileInputStream =
                new ByteArrayInputStream(
                        "".getBytes());

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
                throw new RuntimeException("Failed to upload " + file + ",reply:" + ftp.getReplyString());
            }
        } finally {
            dataIn.close();
        }

    }

    @Override
    public void disconnect() {
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
            } catch (IOException ioe) {
                // 什么也不做
            }
        }
        // 省略与清单6-2中相同的代码
    }
}