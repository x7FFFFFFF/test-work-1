package com.noname.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    public static final String KEY_FORMAT = "%s %s";
    private HttpRequest request;
    private final StringBuilder buf = new StringBuilder();
    private final Map<String, UriHandlerBased> handlersMap;

    public ServerHandler(List<UriHandlerBased> handlers) {
        final Map<String, UriHandlerBased> tempMap = new HashMap<>();
            try {
                for (UriHandlerBased handler :handlers) {
                    Class<? extends UriHandlerBased> clz = handler.getClass();
                    Annotation annotation = clz.getAnnotation(Mapped.class);
                    if (annotation!=null) {
                        tempMap.put(getKey((Mapped) annotation), clz.getDeclaredConstructor().newInstance());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        handlersMap = Collections.unmodifiableMap(tempMap);
    }

    private String getKey(Mapped annotation) {
        return String.format(KEY_FORMAT,annotation.method().name(),annotation.uri());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        UriHandlerBased handler = null;
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            buf.setLength(0);
            handler = handlersMap.get(getKey(request));
            if (handler!=null) {
                handler.process(request, buf);
            }
        }
        if (msg instanceof LastHttpContent) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    ((LastHttpContent) msg).decoderResult().isSuccess()? OK : BAD_REQUEST,
                    Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8)
            );
            response.headers().set(CONTENT_TYPE, handler!=null ? handler.getContentType() : "text/plain; charset=UTF-8");

            if (isKeepAlive(request)) {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            }
            ctx.write(response);
        }
    }

    private String getKey(HttpRequest context) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        return String.format(KEY_FORMAT, context.method().name(), queryStringDecoder.path());


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
