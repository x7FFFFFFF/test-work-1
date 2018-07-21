package com.noname.options.server;

import com.noname.server.Codec;
import com.noname.server.RequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.util.AbstractMap.SimpleImmutableEntry;
//import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final String KEY_FORMAT = "%s %s";

    private final Map<String, SimpleImmutableEntry<RequestHandler, Codec>> handlersMap;

    ServerHandler(List<RequestHandler> handlers) {
        List<RequestHandler> list = new ArrayList<>(handlers);
        final Map<String, SimpleImmutableEntry<RequestHandler, Codec>> tempMap = new HashMap<>();//TODO: Map<String, List<RequestHandler>>
        try {
            for (RequestHandler handler : list) {
                tempMap.put(getKey(handler), new SimpleImmutableEntry<>(handler, handler.getCodec()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handlersMap = Collections.unmodifiableMap(tempMap);
    }

    private String getKey(RequestHandler handler) {
        return String.format(KEY_FORMAT, handler.getMethod().name(), handler.getUrl());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        SimpleImmutableEntry<RequestHandler, Codec> entry = null;
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

            com.noname.server.HttpResponse httpResponse = new HttpResponseImpl();
            entry = handlersMap.get(getKey(fullHttpRequest));

            if (entry != null) {
                final Codec codec = entry.getValue();
                final com.noname.server.HttpRequest httpRequest = convert(fullHttpRequest, codec);
                entry.getKey().process(httpRequest, httpResponse);
                FullHttpResponse fullHttpResponse = convert(httpResponse, fullHttpRequest, codec);
                ctx.write(fullHttpResponse);
            }
        }


    }

    private com.noname.server.HttpRequest convert(FullHttpRequest fullHttpRequest, Codec codec) {
        final ByteBuf buf = fullHttpRequest.content();
        com.noname.server.HttpRequest httpRequest = new HttpRequestImpl();
        decode(httpRequest, buf, codec);
        return httpRequest;
    }

    private FullHttpResponse convert(com.noname.server.HttpResponse httpResponse, FullHttpRequest fullHttpRequest, Codec codec) {
        byte[] bytes = getBytes(httpResponse, codec);
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
                HTTP_1_1, OK,
                Unpooled.copiedBuffer(bytes));
        fullHttpResponse.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        if (isKeepAlive(fullHttpRequest)) {
            fullHttpResponse.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        }
        final int len = fullHttpResponse.content().readableBytes();
        if (len != 0) {
            fullHttpResponse.headers().set(CONTENT_LENGTH, len);
        }
        return fullHttpResponse;
    }

    private byte[] getBytes(com.noname.server.HttpResponse httpResponse, Codec codec) {
        final Object source = httpResponse.getSource();
        byte[] bytes = new byte[0];
        if (source != null) {
            try {
                bytes = codec.encode(source);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bytes;
    }

    private void decode(com.noname.server.HttpRequest httpRequestAdapter, ByteBuf buf, Codec codec) {
        if (codec != null) {
            byte[] buffer = new byte[buf.readableBytes()];
            buf.readBytes(buffer);
            try {
                httpRequestAdapter.setSource(codec.decode(buffer));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


       /* if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            response = new HttpResponseImpl(); //FIXME
            handler = handlersMap.get(getKey(request));
            if (handler!=null) {
                handler.process(new HttpRequestAdapter(request), response);
            }
        }
        if (msg instanceof LastHttpContent) {
         *//*   FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1,
                    ((LastHttpContent) msg).decoderResult().isSuccess()? OK : BAD_REQUEST,
                    Unpooled.copiedBuffer(response.toString(), CharsetUtil.UTF_8)
            );
            response.headers().set(CONTENT_TYPE,  "text/plain; charset=UTF-8");

            if (isKeepAlive(request)) {
                response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            }*//*
            FullHttpResponse fullResp = new DefaultFullHttpResponseAdapter(response, isKeepAlive(request));
            ctx.write(fullResp);
        }*/


    private String getKey(io.netty.handler.codec.http.HttpRequest httpRequest) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(httpRequest.uri());
        return String.format(KEY_FORMAT, httpRequest.method().name(), queryStringDecoder.path());


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
