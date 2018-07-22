package com.noname.h2;

import com.noname.dao.JDBCDao;

import java.sql.Connection;

public class H2Dao implements JDBCDao {

    private final Connection connection;

    public H2Dao(Connection connection) {
        this.connection = connection;
    }

   // private static final MessageFormat INSERT_QUERY = new MessageFormat("INSERT INTO {0} ({1}) VALUES ({2})");
   private static final String INSERT_QUERY ="INSERT INTO %s (%s) VALUES (%s)";


   // private static final MessageFormat SELECT_QUERY_BY_FIELD = new MessageFormat("SELECT {0} FROM {1} WHERE {2} = ?");
    private static final String SELECT_QUERY_BY_FIELD = "SELECT %s FROM %s WHERE %s = ?";

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getSelectByFieldQuery() {
        return SELECT_QUERY_BY_FIELD;
    }

    @Override
    public String getInsertQuery() {
        return INSERT_QUERY;
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
