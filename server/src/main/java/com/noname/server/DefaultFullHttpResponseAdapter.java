package com.noname.server;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class DefaultFullHttpResponseAdapter extends DefaultFullHttpResponse {


    public DefaultFullHttpResponseAdapter(IResponse response, boolean keepAlive) {
        super(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        this.headers().set(CONTENT_TYPE,  "text/plain; charset=UTF-8");

     /*   if (keepAlive) {
            this.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            this.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        }*/

    }
}
