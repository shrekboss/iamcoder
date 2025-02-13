package org.coder.concurrency.programming.pattern._12_worker_thread.ftp;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DbRecordSource implements RecordSource {
    private final ResultSet rs;

    public DbRecordSource(Properties config) throws Exception {
        Connection cnn = getConnection(config);
        this.rs = qryRecords(cnn);
    }

    @Override
    public void close() throws IOException {
        try (Statement stmt = rs.getStatement();
             Connection cnn = stmt.getConnection();) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean hasNext() {
        boolean isNotLast = true;
        try {
            isNotLast = !rs.isLast();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isNotLast;
    }


    @Override
    public RecordDefinition next() {
        RecordDefinition recordDefinition = null;
        try {
            rs.next();
            recordDefinition = makeRecordFrom(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recordDefinition;
    }

    private ResultSet qryRecords(Connection dbConn) throws Exception {
        dbConn.setReadOnly(true);
        PreparedStatement ps = dbConn.prepareStatement(
                "select id,productId,packageId,msisdn,operationTime,operationType,"
                        + "effectiveDate,dueDate from subscriptions order by operationTime",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = ps.executeQuery();
        return rs;
    }

    protected Connection getConnection(Properties props) throws Exception {
        Connection dbConn = null;
        dbConn = DriverManager.getConnection(props.getProperty("jdbc.url"),
                props.getProperty("jdbc.username"),
                props.getProperty("jdbc.password"));
        return dbConn;
    }

    private static RecordDefinition makeRecordFrom(ResultSet rs) throws SQLException {
        RecordDefinition recordDefinition = new RecordDefinition();
        recordDefinition.setId(rs.getInt("id"));
        recordDefinition.setProductId(rs.getString("productId"));
        recordDefinition.setPackageId(rs.getString("packageId"));
        recordDefinition.setMsisdn(rs.getString("msisdn"));
        recordDefinition.setOperationTime(rs.getTimestamp("operationTime"));
        recordDefinition.setOperationType(rs.getInt("operationType"));
        recordDefinition.setEffectiveDate(rs.getTimestamp("effectiveDate"));
        recordDefinition.setDueDate(rs.getTimestamp("dueDate"));
        return recordDefinition;
    }
}