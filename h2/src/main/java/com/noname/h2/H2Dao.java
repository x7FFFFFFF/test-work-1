package com.noname.h2;

import com.noname.logic.JDBCDao;

import java.sql.Connection;
import java.text.MessageFormat;

public class H2Dao implements JDBCDao {

    private final Connection connection;

    public H2Dao(Connection connection) {
        this.connection = connection;
    }

    private static final MessageFormat INSERT_QUERY = new MessageFormat("INSERT INTO {0} ({1}) VALUES ({2})");


    private static final MessageFormat SELECT_QUERY_BY_FIELD = new MessageFormat("SELECT {0} FROM {1} WHERE {2} = ?");

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public MessageFormat getSelectByFieldQuery() {
        return SELECT_QUERY_BY_FIELD;
    }

    @Override
    public MessageFormat getInsertQuery() {
        return INSERT_QUERY;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
