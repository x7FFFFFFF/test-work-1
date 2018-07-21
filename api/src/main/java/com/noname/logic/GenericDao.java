package com.noname.logic;

import com.noname.data.Entity;
import com.noname.data.Field;
import com.noname.data.Identified;

import java.util.List;

public interface GenericDao<T extends Enum<T> & Field> {

    void persist(Entity<T> object) throws Exception;

   /* void update(T object);

    void delete(T object);

    T getByPK(Field field);*/

    List<Entity<T>> find(T field, Object value)  throws Exception;


   /* String getTableName();
*/
    Class<T> getType();

    Class<? extends Entity<T>> getEntityClass();
}
