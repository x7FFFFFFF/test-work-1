package com.noname.server;

import java.net.URI;
import java.util.EnumMap;
import java.util.Map;

public interface IHttpRequestResponse {
    Methods  getMethod();
    void setMethod(Methods method);
    URI getUri();
    void setUri(URI uri);
    String getProtocolVersion();
    void setProtocolVersion(String version);
    Map<String, String> getHeaders();
    EnumMap<Extras,String> getExtras();
}
