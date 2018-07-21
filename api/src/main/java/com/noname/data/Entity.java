package com.noname.data;

import java.util.EnumMap;
import java.util.Set;

public interface Entity<T extends Enum<T> & Field> {


    EnumMap<T, Object> getValues();

    default T getPKField() {
        final Set<T> keySet = getValues().keySet();
        for (T key : keySet) {
            if (key.isPK()) {
                return key;
            }
        }
        throw new IllegalStateException("PK is unfound");
    }

    default <K> K getPKValue(Class<K> clz) {
        final T pkField = getPKField();
        return getValue(pkField, clz);
    }

    default <K> K getValue(T key, Class<K> clz) {
        return clz.cast(getValues().get(key));
    }

    default void setValue(T key, Object value) {
        checkType(key, value);
        getValues().put(key, value);
    }


    default void checkType(T key, Object value) {
        final Class<?> type = key.getType();
        final Class<?> valueClass = value.getClass();
        if (!type.isAssignableFrom(valueClass)) {
            throw new IllegalArgumentException(String.format("Wrong type %s, expected %s", valueClass, type));
        }
    }


    default void checkPK() {
        final Set<T> keySet = getValues().keySet();
        boolean found = false;
        for (T key : keySet) {


            if (!found && key.isPK()) {
                found = true;
            }

            if (found && key.isPK()) {
                throw new IllegalStateException("Found duplicate PK = " + key.getName());
            }
        }
    }


}
