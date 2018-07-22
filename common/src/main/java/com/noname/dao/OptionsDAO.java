package com.noname.dao;

public class OptionsDAO {
    private final String  user;
    private final String  password;
    private final String url;
    private final String driver;
    private final Class<? extends JDBCDao> daoImplClass;

    public OptionsDAO(String user, String password, String url, String driver, Class<? extends JDBCDao> daoImplClass) {
        this.user = user;
        this.password = password;
        this.url = url;
        this.driver = driver;
        this.daoImplClass = daoImplClass;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public Class<? extends JDBCDao> getDaoImplClass() {
        return daoImplClass;
    }
}
