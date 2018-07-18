package com.noname.server;

import java.util.Map;

public interface IRequest extends IHttpRequestResponse {
    RequestsTypes getType();
    void setType(RequestsTypes type);


}
