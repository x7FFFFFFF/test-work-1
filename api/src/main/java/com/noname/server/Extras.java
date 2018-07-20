package com.noname.server;

public enum Extras {
    LOGIN, PASSWORD, BALANCE;

    String getAttrName(){
        return this.name().toLowerCase();
    }
    static Extras value(String str){
        return Enum.valueOf(Extras.class,str.toUpperCase() );

    }
}
