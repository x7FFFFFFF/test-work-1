package com.noname.dao;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOFactory {
    private DAOFactory() {
    }

    public static GenericDao getInstance(OptionsDAO optionsDAO) {
       // loadDriver(optionsDAO);
        final Connection connection = getConnection(optionsDAO);

        final Class<? extends JDBCDao> daoImplClass = optionsDAO.getDaoImplClass();
        Constructor<? extends JDBCDao> constructor = null;
        JDBCDao jdbcDao = null;
        try {
            constructor = daoImplClass.getConstructor(Connection.class);
            jdbcDao = constructor.newInstance(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jdbcDao;

    }

/*    private static void loadDriver(OptionsDAO optionsDAO) {
        try {
            Class.forName(optionsDAO.getDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }*/

    private static Connection getConnection(OptionsDAO optionsDAO) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(optionsDAO.getUrl(), optionsDAO.getUser(), optionsDAO.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

}
