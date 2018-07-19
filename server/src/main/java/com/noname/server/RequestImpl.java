package com.noname.server;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.net.URI;
import java.util.Map;

public class RequestImpl implements IRequest {

    private HttpRequest request;
    private Object source;


    public RequestImpl(FullHttpRequest request) {
        this.request = request;

    }


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
