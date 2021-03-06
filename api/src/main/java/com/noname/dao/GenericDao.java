package com.noname.dao;

import com.noname.data.Entity;
import com.noname.data.Field;
import com.noname.data.Identified;

import java.util.List;

public interface GenericDao extends AutoCloseable {

    <T extends Enum<T> & Field> void persist(Entity<T> object) throws Exception;

   /* void update(T object);

    void delete(T object);

    T getByPK(Field field);*/

    <T extends Enum<T> & Field> List<Entity<T>> find(Class<T> fieldsClass, T field, Object value)  throws Exception;



}
