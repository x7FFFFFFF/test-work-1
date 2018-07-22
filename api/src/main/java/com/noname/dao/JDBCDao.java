package com.noname.dao;

import com.noname.data.Entity;
import com.noname.data.Field;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public interface JDBCDao extends GenericDao {


    Connection getConnection(); //TODO: Connections pool

    //PreparedStatement getStatementCache(String str);


    String getSelectByFieldQuery();

    String getInsertQuery();


    @Override
    default <T extends Enum<T> & Field> void persist(Entity<T> object) throws Exception {
        final AbstractMap.SimpleImmutableEntry<String, String> fieldsAndValues = getFieldsAndValues(object, true);
        String query = String.format(getInsertQuery(), getTableName(object), fieldsAndValues.getKey(), fieldsAndValues.getValue());
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            prepareStatementForInsert(statement, object);
            int count = statement.executeUpdate();
            if (count != 1) {
                throw new Exception("On persist modify more then 1 record: " + count);
            }

        }
        //TODO: get last insert ID


    }

    default <T extends Enum<T> & Field> String getTableName(Entity<T> object) {
        return object.getClass().getSimpleName();
    }


    default <T extends Enum<T> & Field> void prepareStatementForInsert(PreparedStatement statement, Entity<T> object) throws SQLException {
        final Set<Map.Entry<T, Object>> entries = object.getValues().entrySet();
        int counter = 1;
        for (Map.Entry<T, Object> entry : entries) {
            final T key = entry.getKey();
            final Object value = entry.getValue();
            final Class<?> type = key.getType();
            prepareStatement(statement, value, type, counter);
            counter++;
        }
    }

   /* default String getFields(Entity<T> object){
        final Set<T> keySet = object.getValues().keySet();
        StringJoiner joiner = new StringJoiner(", ");
        for (T key : keySet) {
            joiner.add(key.getName());
        }
        return joiner.toString();
    }*/

    default <T extends Enum<T> & Field> AbstractMap.SimpleImmutableEntry<String, String> getFieldsAndValues(Entity<T> object, boolean excludePK) {
        final Set<Map.Entry<T, Object>> entries = object.getValues().entrySet();
        StringJoiner fields = new StringJoiner(", ");
        StringJoiner vals = new StringJoiner(", ");
        for (Map.Entry<T, Object> entry : entries) {
            final T key = entry.getKey();
            if (key.isPK() && excludePK) {
                continue;
            }
            fields.add(key.getName());
            vals.add("?");
        }
        return new AbstractMap.SimpleImmutableEntry<>(fields.toString(), vals.toString());
    }

    //
    @Override
    default <T extends Enum<T> & Field> List<Entity<T>> find(Class<T> fieldsClass, T field, Object object) throws Exception {

        final String fields = getFields(fieldsClass, false);
        final Class<? extends Entity> entityClass = getEntityClass(fieldsClass);


        String query = String.format(getSelectByFieldQuery(), fields, entityClass.getSimpleName(), field.getName());
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            prepareStatementForSelect(statement, field, object);
            ResultSet rs = statement.executeQuery();
            return parseResultSet(rs, fieldsClass);

        }

    }

    default <T extends Enum<T> & Field> Class<? extends Entity> getEntityClass(Class<T> fieldsClass) {
        final T[] enumConstants = fieldsClass.getEnumConstants();
        final T constant = enumConstants[0];
        return constant.getEntityClass();
    }

    @SuppressWarnings("unchecked") //FIXME:
    default <T extends Enum<T> & Field> List<Entity<T>> parseResultSet(ResultSet rs, Class<T> type) throws Exception {
        List<Entity<T>> result = new ArrayList<>();

        while (rs.next()) { //TODO: autoboxing
            final Class<? extends Entity> entityClass = getEntityClass(type);
            final Entity<T> entity = entityClass.newInstance();
            final T[] enumConstants = type.getEnumConstants();
            int counter = 1;
            for (T key : enumConstants) {
                final Class<?> keyType = key.getType();
                final String name = key.getName();
                if (keyType == String.class) {
                    entity.setValue(key, rs.getString(counter));
                } else if (keyType == Long.class) {
                    entity.setValue(key, rs.getLong(counter));
                } else if (keyType == BigDecimal.class) {

                    final BigDecimal value = rs.getBigDecimal(counter);

                    entity.setValue(key, (value==null)?BigDecimal.ZERO:value);
                } else {
                    throw new IllegalStateException("Unknown type = " + keyType);
                }
                counter++;
            }
            result.add(entity);
        }

        return result;

    }

    default <T extends Enum<T> & Field> void prepareStatementForSelect(PreparedStatement statement, T field, Object value) throws SQLException {
        prepareStatement(statement, value, field.getType(), 1);
    }

    default void prepareStatement(PreparedStatement statement, Object value, Class<?> type, int counter) throws SQLException {
        if (type == String.class) {
            statement.setString(counter, (String) value);
        } else if (type == Long.class) {
            statement.setLong(counter, (Long) value);
        } else if (type == BigDecimal.class) {
            statement.setBigDecimal(counter, (BigDecimal) value);
        } else {
            throw new IllegalStateException("Unknown type = " + type);
        }
    }


    //INSERT INTO [Table] ([column, column, ...]) VALUES (?, ?, ...);

    /*  @Override
      default T persist(T object) {
          String query = INSERT_QUERY.format(getTableName(), getFields(object), getValues(object));
          return null;
      }

      default String getValues(T object) {
          object.

      }
  */
    default <T extends Enum<T> & Field> String getFields(Class<T> type, boolean excludePK) {
        final T[] enumConstants = type.getEnumConstants();
        StringJoiner joiner = new StringJoiner(", ");
        for (T key : enumConstants) {
            if (key.isPK() && excludePK) {
                continue;
            }
            joiner.add(key.getName());
        }
        return joiner.toString();
    }

   /* @Override
    default List<T> find(Field field) {
        return null;
    }*/
}
