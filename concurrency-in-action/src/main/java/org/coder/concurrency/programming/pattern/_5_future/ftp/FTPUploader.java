package org.coder.concurrency.programming.pattern._5_future.ftp;

import java.io.File;

public interface FTPUploader {

    void init(String ftpServer, String ftpUserName, String password,
              String serverDir) throws Exception;

    void upload(File file) throws Exception;

    void disconnect();
}