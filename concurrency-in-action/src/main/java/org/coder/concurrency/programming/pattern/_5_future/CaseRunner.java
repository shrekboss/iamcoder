package org.coder.concurrency.programming.pattern._5_future;

import org.coder.concurrency.programming.pattern._5_future.ftp.DataSyncTask;

import java.util.HashMap;
import java.util.Map;

public class CaseRunner {

    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();

        // FTP服务器IP地址，运行代码时请根据实际情况修改！
        params.put("server", "10.0.0.5");

        // FTP账户名，运行代码时请根据实际情况修改！
        params.put("userName", "datacenter");

        // FTP账户密码，运行代码时请根据实际情况修改！
        params.put("password", "abc123");

        // FTP服务器基准目录！
        params.put("serverDir", "~/subspsync");

        // 如果要使用真实的FTP服务器，请将下面的这条语句注释掉
        System.setProperty("ftp.client.impl",
                "org.coder.concurrency.programming.pattern._5_future.FakeFTPUploader");

        DataSyncTask dst;
        dst = new DataSyncTask(params);
        Thread t = new Thread(dst);
        t.start();

    }
}