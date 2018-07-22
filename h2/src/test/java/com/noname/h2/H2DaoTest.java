package com.noname.h2;

import com.noname.data.Entity;
import com.noname.dao.GenericDao;
import org.junit.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class H2DaoTest {
    private Connection conn;

    @Before
    public void init() throws SQLException {

        conn = DriverManager.
                getConnection("jdbc:h2:mem:sample;INIT=RUNSCRIPT FROM 'classpath:scripts/create.sql'", "sa", "");
        final Statement statement = conn.createStatement();
        statement.execute("TRUNCATE TABLE User");
    }

    @After
    public void destroy() throws SQLException {
        final Statement statement = conn.createStatement();
        statement.execute("TRUNCATE TABLE User");
        conn.close();
    }



    @Test()
    public void testDAOImpl() throws Exception {
        GenericDao dao = new H2Dao(conn);
        insertAndFind(dao);


    }

    public static void insertAndFind(GenericDao dao) throws Exception {
        final User user = new User();
        final BigDecimal decimal = new BigDecimal("100.00");
        final String login = "login";
        final String pwd = "pwd";
        user.setValue(UserFields.LOGIN, login);
        user.setValue(UserFields.PASSWORD, pwd);
        user.setValue(UserFields.BALANCE, decimal);
        dao.persist(user);
        System.out.println(user);

        final List<Entity<UserFields>> res = dao.find(UserFields.class, UserFields.LOGIN, login);

        Assert.assertEquals(1, res.size());

        final Entity<UserFields> userEntity = res.get(0);
        String login1 = userEntity.getValue(UserFields.LOGIN);
        Assert.assertEquals("login", login1);
    }


    @Test()
    public void testUniqIndex() throws SQLException {

        final BigDecimal decimal = new BigDecimal("100.00");
        final String login = "login";
        final String pwd = "pwd";
        {
            final int count = insert(decimal, login, pwd);
            System.out.println("count = " + count);
        }
        {
            try {
                final int count = insert(decimal, login, pwd);
                System.out.println("count = " + count);
            } catch (SQLException ex) {
                System.out.println(ex);
                Assert.assertTrue(ex.getMessage().toLowerCase().contains("unique index or primary key violation"));

            }
        }



    }

    private int insert(BigDecimal decimal, String login, String pwd) throws SQLException {
        final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO User (login, password, balance) VALUES (?, ?, ?)");


        preparedStatement.setString(1, login);

        preparedStatement.setString(2, pwd);

        preparedStatement.setBigDecimal(3, decimal);

        return preparedStatement.executeUpdate();
    }

}
