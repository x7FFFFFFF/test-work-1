package com.noname.server;

import java.net.URI;
import java.util.Map;

public interface HttpRequestResponse {
    Methods  getMethod();
    void setMethod(Methods method);
    URI getUri();
    void setUri(URI uri);
    String getProtocolVersion();
    void setProtocolVersion(String version);
    Map<String, String> getHeaders();
    Object getSource();
    void setSource(Object obj);
}
