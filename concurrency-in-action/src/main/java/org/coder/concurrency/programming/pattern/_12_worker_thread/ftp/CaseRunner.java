package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

import org.coder.concurrency.programming.pattern._5_future.ftp.DataSyncTask;
import org.coder.concurrency.programming.util.Tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

/**
 * 运行以下类可以生成该程序所需的数据库记录
 */
public class CaseRunner {

    public static void main(String[] args) throws IOException {
        Properties config = Tools.loadProperties(
                CaseRunner.class.getPackage().getName().replaceAll("\\.", "/") + "/conf.properties");

        DataSyncTask dst;
        // dst = new DataSyncTask(config);
        // 运行本程序前，请根据实际情况修改以下方法中有关数据库连接和FTP账户的信息
        dst = new DataSyncTask(config) {
            {
                System.setProperty("ftp.client.impl",
                        "org.coder.concurrency.programming.pattern._5_future.FakeFTPUploader");
            }

            @Override
            protected RecordSource makeRecordSource(Properties config)
                    throws Exception {
                return new FakeRecordSource();
            }

        };

        dst.run();
    }

    // 模拟从数据库中读取数据
    private static class FakeRecordSource implements RecordSource {
        private final Scanner scanner;
        private final GZIPInputStream gis;

        public FakeRecordSource() throws IOException {
            this.gis = new GZIPInputStream(
                    new BufferedInputStream(Objects.requireNonNull(CaseRunner.class.getResourceAsStream("subscriptions.csv.gz"))));
            this.scanner = new Scanner(gis, "UTF-8");
        }

        @Override
        public void close() throws IOException {
            try {
                scanner.close();
            } finally {
                gis.close();
            }
        }

        @Override
        public boolean hasNext() {
            return scanner.hasNextLine();
        }

        @Override
        public Record next() {
            String line = scanner.nextLine();
            Record record = null;
            try {
                record = Record.parseCsv(line);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return record;
        }
    }

}