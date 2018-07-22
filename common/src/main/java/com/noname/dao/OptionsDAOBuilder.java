package com.noname.dao;

public class OptionsDAOBuilder {
    private String user;
    private String password;
    private String url;
    private String driver;
    private Class<? extends JDBCDao> daoImplClass;

    public OptionsDAOBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public OptionsDAOBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public OptionsDAOBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public OptionsDAOBuilder setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    public OptionsDAOBuilder setDaoImplClass(Class<? extends JDBCDao> daoImplClass) {
        this.daoImplClass = daoImplClass;
        return this;
    }

    public OptionsDAO build() {
        return new OptionsDAO(user, password, url, driver, daoImplClass);
    }
}