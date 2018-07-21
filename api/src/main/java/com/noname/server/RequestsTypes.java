package com.noname.server;

public enum RequestsTypes {
    CREATE_AGT, GET_BALANCE;

    public String getTagText() {
        return this.name().replace("_", "-");
    }

    public static RequestsTypes value(String value) {
        return Enum.valueOf(RequestsTypes.class, value.replace("-", "_"));
    }
}
