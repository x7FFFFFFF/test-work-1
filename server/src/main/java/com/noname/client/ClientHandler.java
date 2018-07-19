package com.noname.client;

import com.noname.server.ICodec;
import com.noname.server.IResponse;
import com.noname.server.ResponseImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.concurrent.BlockingQueue;

public class ClientHandler extends SimpleChannelInboundHandler<Object>  {

    private final BlockingQueue<IResponse> responseQueue;
    private final ICodec codec;

    ClientHandler(BlockingQueue<IResponse> responseQueue, ICodec codec) {
        this.responseQueue = responseQueue;
        this.codec = codec;
    }
    private void decode(IResponse request, ByteBuf buf, ICodec codec) {
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
            IResponse response = new ResponseImpl();
            decode(response, fullHttpResponse.content(),  codec);
            responseQueue.offer(response);

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