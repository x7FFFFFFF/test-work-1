package com.noname.server;



public interface RequestHandler {
    Methods getMethod();
    String getUrl();
    Codec getCodec();
    boolean process(HttpRequest httpRequest, HttpResponse httpResponse);
}
