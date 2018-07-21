package com.noname.options;

import java.nio.file.Path;
import java.nio.file.Paths;

public  interface IOption {
    Class<?> getType();

    String getName();


    default Object convert(String str) {
        final Class<?> type = getType();
        if (type == Integer.class) {
            return Integer.valueOf(str);
        } else if (type == String.class) {
            return str;
        } else if (type == Boolean.class) {
            return Boolean.valueOf(str);
        } else if (type == Path.class) {
            return Paths.get(str);
        }
        throw new IllegalArgumentException(String.format("Wrong type %s for string %s", type, str));
    }

}
