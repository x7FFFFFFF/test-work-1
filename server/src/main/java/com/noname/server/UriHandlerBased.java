package com.noname.server;

import io.netty.handler.codec.http.HttpRequest;

public interface  UriHandlerBased{

   void process(HttpRequest request, StringBuilder buff);

    default String getContentType() {
        return "text/plain; charset=UTF-8";
    }
}
