package com.noname;

import com.noname.server.Extras;
import com.noname.server.RequestsTypes;

import java.util.EnumMap;

public class XmlRequest {
    private final EnumMap<Extras, String> extras = new EnumMap<>(Extras.class);
    private RequestsTypes requestsType;

    public XmlRequest() {
    }

    public XmlRequest(RequestsTypes requestsType, EnumMap<Extras, String> extras) {
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
