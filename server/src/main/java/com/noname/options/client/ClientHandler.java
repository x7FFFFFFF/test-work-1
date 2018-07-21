package com.noname.options.client;

import com.noname.options.server.HttpResponseImpl;
import com.noname.server.Codec;
import com.noname.server.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.concurrent.BlockingQueue;

public class ClientHandler extends SimpleChannelInboundHandler<Object>  {

    private final BlockingQueue<HttpResponse> httpResponseQueue;
    private final Codec codec;

    ClientHandler(BlockingQueue<HttpResponse> httpResponseQueue, Codec codec) {
        this.httpResponseQueue = httpResponseQueue;
        this.codec = codec;
    }
    private void decode(HttpResponse request, ByteBuf buf, Codec codec) {
        if (codec != null) {
            byte[] buffer = new byte[buf.readableBytes()];
            buf.readBytes(buffer);
            try {
                request.setSource(codec.decode(buffer));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse)  {
            final FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
            HttpResponse httpResponse = new HttpResponseImpl();
            decode(httpResponse, fullHttpResponse.content(),  codec);
            httpResponseQueue.offer(httpResponse);

    }

       /* if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;

            System.out.println("STATUS: " + response.getStatus());
            System.out.println("VERSION: " + response.getProtocolVersion());
            System.out.println();

            if (!response.headers().isEmpty()) {
                for (String name: response.headers().names()) {
                    for (String value: response.headers().getAll(name)) {
                        System.out.println("HEADER: " + name + " = " + value);
                    }
                }
                System.out.println();
            }

            if (HttpHeaders.isTransferEncodingChunked(response)) {
                System.out.println("CHUNKED CONTENT {");
            } else {
                System.out.println("CONTENT {");
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;

            System.out.print(content.content().toString(CharsetUtil.UTF_8));
            System.out.flush();

            if (content instanceof LastHttpContent) {
                System.out.println("} END OF CONTENT");
            }
        }*/
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}