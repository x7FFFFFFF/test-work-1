package com.noname.options.server;

import com.noname.server.HttpResponse;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

public class DefaultFullHttpResponseAdapter extends DefaultFullHttpResponse {


    public DefaultFullHttpResponseAdapter(HttpResponse httpResponse, boolean keepAlive) {
        super(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        this.headers().set(CONTENT_TYPE,  "text/plain; charset=UTF-8");

     /*   if (keepAlive) {
            this.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            this.headers().set(CONTENT_LENGTH, httpResponse.content().readableBytes());
        }*/

    }
}
