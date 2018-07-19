package com.noname.client;

import com.noname.server.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Client implements IClient {

    private static final String LOCALHOST = "localhost";
    private final int port;
    private final String host;
    private static final String HTTP = "http";
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final ICodec codec;
    private final BlockingQueue<IResponse> responseQueue = new LinkedBlockingQueue<>(100);


    private volatile ChannelFuture channelFuture;
    private volatile Status status;


    public Client(Map<String, String> map, ICodec codec) {
        this.port = Integer.parseInt(map.getOrDefault(PORT, PORT_DEFAULT_VALUE));
        this.host = map.getOrDefault(HOST, LOCALHOST);
        this.status = Status.INIT_OK;
        this.codec = codec;
    }


    @Override
    public void start() throws InterruptedException {
        checkStatus(Status.INIT_OK);
        service.submit(new Task());
    }

    private class Task implements Runnable {


        public void run() {

            final boolean ssl = false;
// Configure the client.
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>(){

                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast("log", new LoggingHandler(LogLevel.INFO));
                                p.addLast("codec", new HttpClientCodec());

                                // p.addLast("inflater", new HttpContentDecompressor());

                                p.addLast("aggregator", new HttpObjectAggregator(1048576));
                                p.addLast("handler", new ClientHandler(responseQueue, codec));
                            }
                        });
                // Make the connection attempt.


                channelFuture = b.connect(host, port).sync();

                Channel ch = channelFuture.channel();
                // Prepare the HTTP request.
     /*           FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_1, method, uri.getRawPath());
                request.headers().set(HttpHeaderNames.HOST, host);
                request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                if (method == HttpMethod.POST) {
                    ByteBuf bb = Unpooled.copiedBuffer(payload, Charset.defaultCharset());

                    request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded");
                    request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bb.readableBytes());
                    request.content().clear().writeBytes(bb);

                }*/


                //request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

                /*// Set some example cookies.
                request.headers().set(
                        HttpHeaders.Names.COOKIE,
                        ClientCookieEncoder.encode(
                                new DefaultCookie("my-cookie", "foo"),
                                new DefaultCookie("another-cookie", "bar")));
*/
                // Send the HTTP request.
                // ch.writeAndFlush(request);

                // Wait for the server to close the connection.
                status = Status.RUN;
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace(); //TODO: logging

            } finally {
                // Shut down executor threads to exit.
                group.shutdownGracefully();
                status = Status.STOP_OK;
            }

        }
    }

    @Override
    public void stop() {
        service.shutdownNow();
        status = Status.STOP;
        if (channelFuture != null) {
            channelFuture.channel().close();
            channelFuture.awaitUninterruptibly();
        }
    }

    @Override
    public Status getStatus() {
        return status;
    }
    //Blocking until response!
    @Override
    public IResponse send(IRequest request) throws Exception {
        checkStatus(Status.RUN);
        Channel ch = channelFuture.channel();
        final HttpMethod method = HttpMethod.valueOf(request.getMethod().name());
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, method, request.getUri().getRawPath());
        fullHttpRequest.headers().set(HttpHeaderNames.HOST, host);
        fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        if (method == HttpMethod.POST) {
            final byte[] bytes = codec.encode(request.getSource());
            ByteBuf bb = Unpooled.copiedBuffer(bytes);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded");
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, bb.readableBytes());
            fullHttpRequest.content().clear().writeBytes(bb);

        }


        //fullHttpRequest.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);


              /*  fullHttpRequest.headers().set(
                        HttpHeaders.Names.COOKIE,
                        ClientCookieEncoder.encode(
                                new DefaultCookie("my-cookie", "foo"),
                                new DefaultCookie("another-cookie", "bar")));
*/

        ch.writeAndFlush(fullHttpRequest);
        final IResponse response = responseQueue.take();

        return response;
    }



    @Override
    public void waitForRun() throws InterruptedException {
        checkStatus(Status.INIT_OK);
        while (status==Status.INIT_OK) {
            TimeUnit.SECONDS.sleep(2);
        }
        checkStatus(Status.RUN );
    }

    @Override
    public void waitForStop() throws InterruptedException {
        throw  new UnsupportedOperationException();
    }

    private void checkStatus(Status expected) {
        if (expected != status) {
            //setStatus(Status.ERROR);                //stop();
            throw new IllegalStateException(String.format("Server expected to be  %s, but is %s", expected, status));
        }
    }
}
