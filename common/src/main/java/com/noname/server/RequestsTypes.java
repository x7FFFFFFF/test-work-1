package com.noname.server;

public enum RequestsTypes {
    CREATE_AGT, GET_BALANCE;

    String getTagText() {
        return this.name().replace("_", "-");
    }

    static RequestsTypes value(String value){
        return Enum.valueOf(RequestsTypes.class, value.replace("-", "_"));
    }
}
