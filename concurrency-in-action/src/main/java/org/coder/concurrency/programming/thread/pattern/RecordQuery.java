package org.coder.concurrency.programming.thread.pattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:crayzer.chen@gmail.com">夜骐</a>
 * @since 1.0.0
 */
public class RecordQuery {

    private final Connection connection;

    public RecordQuery(Connection connection) {
        this.connection = connection;
    }

    public <T> T query(StrategyRowHandler<T> strategyRowHandler, String sql, Object... params) throws SQLException {

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index++, param);
            }

            ResultSet resultSet = stmt.executeQuery();
            // 调用 strategyRowHandler
            return strategyRowHandler.handle(resultSet);
        }
    }
}
