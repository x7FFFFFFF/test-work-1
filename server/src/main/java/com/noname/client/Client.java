package com.noname.client;

import com.noname.server.Methods;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;

public class Client {

    private final URI uri;
    private final int port;
    private final HttpMethod method;

    public Client(URI uri, Methods method) {
        this.uri = uri;
        this.port = uri.getPort();
        this.method = HttpMethod.valueOf(method.name());
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
            Channel ch = b.connect(host, port).sync().channel();
            // Prepare the HTTP request.
            HttpRequest request = new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getRawPath());
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
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
