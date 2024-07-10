package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

import org.coder.concurrency.programming.util.Tools;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Random;

/**
 * 实战案例运行时所需的数据库记录生成器。
 * 数据库的初始化可以使用数据库脚本DBScript.sql手工执行，也可以在执行本程序时在命令行中添加参数“init”。
 */
public class RecordGenerator {


    public static void main(String[] args) throws Exception {
        Connection dbConn = getConnection();

        if (args.length > 0 && "init".equals(args[0])) {
            Statement st = dbConn.createStatement();
            URL scriptURL = RecordGenerator.class
                    .getResource(RecordGenerator.class.getPackage().getName()
                            .replaceAll("\\.", "/" + "DBScript.sql"));

            String script = new String(
                    Files.readAllBytes(Paths.get(scriptURL.toURI())));
            st.execute(script);
            st.close();
        }

        try {
            String[] msisdns = new String[] { "13612345678", "13712345678",
                    "13812345678", "15912345678" };
            String[] operationTimes = new String[] { "2014-08-08 20:08:08",
                    "2014-08-09 10:08:08", "2014-08-10 09:58:08",
                    "2014-08-10 12:58:08" };

            PreparedStatement ps = dbConn
                    .prepareStatement(
                            "Insert into subscriptions(id,productId,packageId,msisdn,operationTime,operationType,effectiveDate,dueDate) "
                                    + "values(?,?,'pkg0000001',?,?,0,'2014-09-01 00:00:00','2014-12-01 23:59:59')");
            DecimalFormat df = new DecimalFormat("0000");

            Random rnd = new Random();
            Random rnd1 = new Random();
            Random rnd2 = new Random();

            int count;

            if (args.length > 0) {
                count = Integer.valueOf(args[0]);
            } else {
                count = 10000;
            }

            for (int i = 0; i < count; i++) {
                ps.setInt(1, i);
                ps.setString(2, "p0000" + df.format(rnd1.nextInt(9999)));
                ps.setString(3, msisdns[rnd.nextInt(msisdns.length)]);
                ps.setString(4,
                        operationTimes[rnd2.nextInt(operationTimes.length)]);
                ps.addBatch();

            }

            ps.executeBatch();
        } finally {

            dbConn.close();
        }

    }

    static Connection getConnection() throws Exception {
        Connection dbConn = null;
        Properties props = Tools.loadProperties(
                RecordGenerator.class.getPackage().getName().replaceAll("\\.",
                        "/")
                        + "/conf.properties");
        Class.forName(props.getProperty("jdbc.driver"));

        dbConn = DriverManager.getConnection(props.getProperty("jdbc.url"),
                props.getProperty("jdbc.username"),
                props.getProperty("jdbc.password", ""));
        return dbConn;
    }

}