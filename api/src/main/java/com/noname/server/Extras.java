package com.noname.server;

public enum Extras {
    LOGIN, PASSWORD, BALANCE;

    public String getAttrName() {
        return this.name().toLowerCase();
    }

    public static Extras value(String str) {
        return Enum.valueOf(Extras.class, str.toUpperCase());

    }
}
