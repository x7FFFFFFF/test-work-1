package com.noname.options.server;

import com.noname.server.HttpRequest;
import com.noname.server.Methods;

import java.net.URI;
import java.util.Map;

public class HttpRequestImpl implements HttpRequest {

   // private HttpRequest request;
    private Object source;
    private Methods method;
    private URI uri;


    public HttpRequestImpl() {
       // this.request = request;

    }


    @Override
    public Methods getMethod() {
        return method;
    }

    @Override
    public void setMethod(Methods method) {
        this.method = method;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public void setUri(URI uri) {
        this.uri = uri;
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
