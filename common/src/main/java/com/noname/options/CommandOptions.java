package com.noname.options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class CommandOptions {

    private CommandOptions() {
    }

    private static Object convert(String str, IOption option) {
        final Class<?> type = option.getType();
        if (type == Integer.class) {
            return Integer.valueOf(str);
        } else if (type == String.class) {
            return str;
        } else if (type == Boolean.class) {
            return str!=null?Boolean.TRUE:Boolean.FALSE;
        } else if (type == Path.class) {
            return Paths.get(str);
        }
        throw new IllegalArgumentException(String.format("Wrong type %s for string %s", type, str));
    }
    public static  <T> Optional<T> get(IOption option, Class<T> clz) {
        final String property = System.getProperty(option.getName());
        if (property==null){
            return Optional.empty();
        }
        return Optional.of(clz.cast(convert(property, option)));
    }
}
