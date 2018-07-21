package com.noname.options.server;

import com.noname.server.HttpResponse;
import com.noname.server.Methods;

import java.net.URI;
import java.util.Map;

public class HttpResponseImpl implements HttpResponse {
   private Object source;

    @Override
    public Methods getMethod() {
        return null;
    }

    @Override
    public void setMethod(Methods method) {

    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public void setUri(URI uri) {

    }

    @Override
    public String getProtocolVersion() {
        return null;
    }

    @Override
    public void setProtocolVersion(String version) {

    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public void setSource(Object obj) {
        this.source = obj;
    }


}
