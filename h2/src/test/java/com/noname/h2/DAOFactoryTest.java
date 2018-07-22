package com.noname.h2;

import com.noname.dao.DAOFactory;
import com.noname.dao.OptionsDAO;
import com.noname.dao.OptionsDAOBuilder;
import com.noname.dao.GenericDao;
import org.junit.Test;

import static com.noname.h2.H2DaoTest.insertAndFind;

public class DAOFactoryTest {


    @Test()
    public void testDAOFactory() throws Exception {
        OptionsDAO options = new OptionsDAOBuilder()
                .setDaoImplClass(H2Dao.class)
                .setUrl("jdbc:h2:mem:sample;INIT=RUNSCRIPT FROM 'classpath:scripts/create.sql'")
                .setUser("sa")
                .setPassword("")
                .build();
        final GenericDao instance = DAOFactory.getInstance(options);
        insertAndFind(instance);


    }
}
