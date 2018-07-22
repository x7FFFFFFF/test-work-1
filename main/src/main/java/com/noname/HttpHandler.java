package com.noname;

import com.noname.dao.GenericDao;
import com.noname.data.Entity;
import com.noname.entities.User;
import com.noname.entities.UserFields;
import com.noname.misc.Environment;
import com.noname.server.*;

import java.math.BigDecimal;
import java.util.List;

public class HttpHandler implements RequestHandler {
    public static final int ERROR = 2;
    public static final int OK = 0;
    public static final int ALREADY_EXIST = 1;
    public static final int USER_NOT_EXIST = 3;


    private final String url;

    public HttpHandler(String url) {
        this.url = url;
    }

    @Override
    public Methods getMethod() {
        return Methods.POST;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Codec getCodec() {
        return new XmlServerCodec();
    }

    @Override
    public boolean process(HttpRequest httpRequest, HttpResponse httpResponse) {

        System.out.println("httpRequest = " + httpRequest);
        final XmlRequest xmlRequest = (XmlRequest) httpRequest.getSource();
        final XmlResponse xmlResponse = new XmlResponse();
        final RequestsTypes requestsType = xmlRequest.getRequestsType();
        GenericDao dao = Environment.INSTANCE.getResource(GenericDao.class);
        int resCose = 2;
        switch (requestsType) {
            case CREATE_AGT:
                resCose = createUser(xmlRequest, dao);


                break;
            case GET_BALANCE:
                resCose = getBalance(xmlRequest, xmlResponse, dao);
                break;

        }

        xmlResponse.setResultCode(resCose);
        httpResponse.setSource(xmlResponse);


        return true;
    }

    private int getBalance(XmlRequest xmlRequest, XmlResponse xmlResponse, GenericDao dao) {
        //User user = createUser(xmlRequest);
        final String login = xmlRequest.getExtras().get(Extras.LOGIN);
        final List<Entity<UserFields>> users = findUserByLogin(dao, login);
        if (users == null || users.size() == 0) {
            return USER_NOT_EXIST;
        } else if (users.size() > 1) {
            return ERROR;
        } else {
            final Entity<UserFields> user = users.get(0);
            final BigDecimal balance = user.getValue(UserFields.BALANCE, BigDecimal.class);
            xmlResponse.getExtras().put(Extras.BALANCE, balance.toString());
            return OK;
        }
    }

    private User createUser(XmlRequest xmlRequest) {
        User user = new User();
        final String login = xmlRequest.getExtras().get(Extras.LOGIN);
        user.setValue(UserFields.LOGIN, login);
        user.setValue(UserFields.PASSWORD, xmlRequest.getExtras().get(Extras.PASSWORD)); //TODO: hash sha1
        user.setValue(UserFields.BALANCE, new BigDecimal("0.00"));
        return user;
    }

    private int createUser(XmlRequest xmlRequest, GenericDao dao) {
        User user = createUser(xmlRequest);
        final String login = xmlRequest.getExtras().get(Extras.LOGIN);
        try {
            dao.persist(user);
            return OK;
        } catch (Exception e) {
            List<Entity<UserFields>> entities = null;
            final List<Entity<UserFields>> users = findUserByLogin(dao, login);
            if (users != null && users.size() > 0) {
                return ALREADY_EXIST;
            }
            return ERROR;
        }

    }

    private List<Entity<UserFields>> findUserByLogin(GenericDao dao, String login) {
        List<Entity<UserFields>> entities = null;
        try {
            entities = dao.find(UserFields.class, UserFields.LOGIN, login);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entities;
    }
}
