package com.noname.client;

import com.noname.server.Methods;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.nio.charset.Charset;

public class Client {

    private final URI uri;
    private final int port;
    private final HttpMethod method;
    private String payload;

    public Client(URI uri, Methods method, String payload) {
        this.uri = uri;
        this.port = uri.getPort();
        this.method = HttpMethod.valueOf(method.name());
        this.payload = payload;
    }

    public void start() throws InterruptedException {
        final String host = uri.getHost();
        final String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        final boolean ssl = "https".equalsIgnoreCase(scheme);
// Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer(ssl));
            // Make the connection attempt.
            final ChannelFuture channelFuture = b.connect(host, port).sync();
            Channel ch = channelFuture.channel();
            // Prepare the HTTP request.
            FullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, method, uri.getRawPath());
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
           if (method==HttpMethod.POST){
               ByteBuf bb= Unpooled.copiedBuffer(payload,Charset.defaultCharset());

               request.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/x-www-form-urlencoded");
               request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, bb.readableBytes());
               request.content().clear().writeBytes(bb);

           }


            //request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

                /*// Set some example cookies.
                request.headers().set(
                        HttpHeaders.Names.COOKIE,
                        ClientCookieEncoder.encode(
                                new DefaultCookie("my-cookie", "foo"),
                                new DefaultCookie("another-cookie", "bar")));
*/
            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }

    }
}
