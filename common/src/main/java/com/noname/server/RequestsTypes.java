package com.noname.server;

public enum RequestsTypes {
    CREATE_AGT, GET_BALANCE;

    String getTagText() {
        return this.name().replace("_", "-");
    }

}
