package com.noname.server;

public enum Extras {
    LOGIN, PASSWORD, BALANCE;

    String getAttrName(){
        return this.name().toLowerCase();
    }
}
