package com.noname.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

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

    private final Map<String, SimpleImmutableEntry<IRequestHandler, ICodec>> handlersMap;

    ServerHandler(List<IRequestHandler> handlers) {
        List<IRequestHandler> list = new ArrayList<>(handlers);
        final Map<String, SimpleImmutableEntry<IRequestHandler, ICodec>> tempMap = new HashMap<>();//TODO: Map<String, List<IRequestHandler>>
        try {
            for (IRequestHandler handler : list) {
                tempMap.put(getKey(handler), new SimpleImmutableEntry<>(handler, handler.getCodec()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handlersMap = Collections.unmodifiableMap(tempMap);
    }

    private String getKey(IRequestHandler handler) {
        return String.format(KEY_FORMAT, handler.getMethod().name(), handler.getUrl());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        SimpleImmutableEntry<IRequestHandler, ICodec> entry = null;
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;

            IResponse response = new ResponseImpl();
            entry = handlersMap.get(getKey(fullHttpRequest));

            if (entry != null) {
                final ICodec codec = entry.getValue();
                final IRequest request = convert(fullHttpRequest, codec);
                entry.getKey().process(request, response);
                FullHttpResponse fullHttpResponse = convert(response, fullHttpRequest, codec);
                ctx.write(fullHttpResponse);
            }
        }


    }

    private IRequest convert(FullHttpRequest fullHttpRequest, ICodec codec) {
        final ByteBuf buf = fullHttpRequest.content();
        IRequest request = new RequestImpl(fullHttpRequest);
        decode(request, buf, codec);
        return request;
    }

    private FullHttpResponse convert(IResponse response, FullHttpRequest fullHttpRequest, ICodec codec) {
        byte[] bytes = getBytes(response, codec);
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

    private byte[] getBytes(IResponse response, ICodec codec) {
        final Object source = response.getSource();
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

    private void decode(IRequest requestAdapter, ByteBuf buf, ICodec codec) {
        if (codec != null) {
            byte[] buffer = new byte[buf.readableBytes()];
            buf.readBytes(buffer);
            try {
                requestAdapter.setSource(codec.decode(buffer));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


       /* if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            response = new ResponseImpl(); //FIXME
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


    private String getKey(HttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        return String.format(KEY_FORMAT, request.method().name(), queryStringDecoder.path());


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
