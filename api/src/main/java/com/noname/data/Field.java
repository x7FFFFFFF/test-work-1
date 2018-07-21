package com.noname.data;

public interface Field {

    String getName();

   Class<?> getType();

    default boolean isPK() {
        return false;
    }

  

}
