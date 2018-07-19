package com.noname.server;

import java.util.EnumMap;

public class TestRequest {
    private final EnumMap<Extras, String> extras = new EnumMap<>(Extras.class);
    private  RequestsTypes requestsType;

    public TestRequest() {
    }

    public TestRequest(RequestsTypes requestsType, EnumMap<Extras, String> extras) {
        this.requestsType = requestsType;
        this.extras.putAll(extras);
    }

    public EnumMap<Extras, String> getExtras() {
        return extras;
    }

    public RequestsTypes getRequestsType() {
        return requestsType;
    }

    public void setRequestsType(RequestsTypes requestsType) {
        this.requestsType = requestsType;
    }
}
