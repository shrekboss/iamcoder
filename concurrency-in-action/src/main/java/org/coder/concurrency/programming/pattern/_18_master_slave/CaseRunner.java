package org.coder.concurrency.programming.pattern._18_master_slave;

import org.coder.concurrency.programming.util.Tools;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaseRunner {

    public static void main(String[] args) throws Exception {
        String strLogFileBaseDir = System.getProperty("java.io.tmpdir") + "/tps/";

        // 解压缩程序所需的测试数据
        InputStream dataIn =
                CaseRunner.class.getClassLoader()
                        .getResourceAsStream("ifl.zip");
        Tools.unzip(dataIn, strLogFileBaseDir);

        final Pattern pattern;
        String matchingRegExp;

        // 用于选择要进行统计的接口日志文件的正则表达式，请根据实际情况修改。
        // ESB_interface_20180701
        // 2018070113
        // 20150420131
        // matchingRegExp = "20150420131[0-9]";
        matchingRegExp = "201807011[0-9]*";
        // matchingRegExp = "201807011[2-4]*";

        pattern = Pattern.compile("ESB_interface_" + matchingRegExp + ".log");
        PipedInputStream pipeIn = new PipedInputStream();
        final PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);

        // 创建并启动工作者线程，用于输出待统计的日志文件的文件名列表
        Thread thread = new Thread(() -> {
            new File(strLogFileBaseDir).list((dir, name) -> {
                Matcher matcher = pattern.matcher(name);
                boolean toAccept = matcher.matches();
                if (toAccept) {
                    try {
                        // 向TPSStat输出待统计的接口日志文件名
                        pipeOut.write((name + "\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return toAccept;
            });

            try {
                pipeOut.flush();
                pipeOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        /*
         * 将待统计的日志文件的文件列表通过输入流的形式传递给统计程序TPSStat， 这相当于在Linux下使用管道符“|”所产生的效果。
         */
        System.setIn(pipeIn);
        TPSStat.main(new String[]{strLogFileBaseDir});
    }

}