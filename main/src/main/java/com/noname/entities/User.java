package com.noname.entities;

import com.noname.data.Entity;

import java.util.EnumMap;

public class User implements Entity<UserFields> {

    private final EnumMap<UserFields, Object> field = new EnumMap<>(UserFields.class);

    @Override
    public EnumMap<UserFields, Object> getValues() {
        return field;
    }

    @Override
    public Class<UserFields> getFieldsClass() {
        return UserFields.class;
    }
}
