package com.noname.server;

import io.netty.handler.codec.http.HttpRequest;
@Mapped(uri ="/h1", method = Methods.GET)
class TestHandler implements UriHandlerBased{
    @Override
    public void process(HttpRequest request, StringBuilder buff) {
        buff.append("SSSS");

    }
}
