package com.noname.options.client;

import com.noname.server.Codec;
import com.noname.server.HttpRequest;
import com.noname.server.HttpResponse;

import com.noname.threads.AbstractService;
import com.noname.threads.ServerThreadFactory;
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
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Client extends AbstractService implements com.noname.server.Client {

    public static final String LOCALHOST = "localhost";
    public static final String HTTP = "http";
    public static final String HOST = "server.host";
    public static final String PORT = "server.port";
    public static final String PORT_DEFAULT_VALUE = "8888";
    public final static String HANDLER_WORKER_COUNTS = "client.handlers.workers.count";
    public final static String BOSS_WORKER_COUNTS = "client.boss.workers.count";

    private final int port;
    private final String host;
    private final Codec codec;
    private final BlockingQueue<HttpResponse> httpResponseQueue = new LinkedBlockingQueue<>(100);
    private final int bossCount;
    private final int handlersCount;


    private final AtomicReference<ChannelFuture> channelFuture = new AtomicReference<>(null);

    private final AtomicReference<EventLoopGroup> bossGroup = new AtomicReference<>(null);
    private final AtomicReference<EventExecutorGroup> handlersExecutorGroup = new AtomicReference<>(null);


    public Client(Map<String, String> map, Codec codec) {
        setStatus(null, Status.INIT_BEGIN);
        this.port = Integer.parseInt(map.getOrDefault(PORT, PORT_DEFAULT_VALUE));
        this.host = map.getOrDefault(HOST, LOCALHOST);
        this.codec = codec;
        this.bossCount = Integer.parseInt(map.getOrDefault(BOSS_WORKER_COUNTS, "1"));
        this.handlersCount = Integer.parseInt(map.getOrDefault(HANDLER_WORKER_COUNTS, "4"));
        setStatus(Status.INIT_BEGIN, Status.INIT_END);
    }


    private class Task implements Runnable {


        public void run() {
            final EventLoopGroup group = new NioEventLoopGroup(bossCount, new ServerThreadFactory("client-boss-pool"));
            final EventExecutorGroup handlersExecutorGr = new DefaultEventExecutorGroup(handlersCount, new ServerThreadFactory("client-handlers-pool"));
            try {
                cas(bossGroup, null, group);
                cas(handlersExecutorGroup, null, handlersExecutorGr);
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast("log", new LoggingHandler(LogLevel.INFO));
                                p.addLast("codec", new HttpClientCodec());
                                // p.addLast("inflater", new HttpContentDecompressor());
                                p.addLast("aggregator", new HttpObjectAggregator(1048576));
                                p.addLast(handlersExecutorGr, "handler", new ClientHandler(httpResponseQueue, codec));
                            }
                        });
                // Make the connection attempt.


                ChannelFuture channelFtr = b.connect(host, port).sync();
                cas(channelFuture, null, channelFtr);
                Channel ch = channelFtr.channel();
                setStatus(Status.START_BEGIN, Status.START_END);
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e); //TODO: logging

            } finally {

                if (!handlersExecutorGr.isShutdown()) {
                    handlersExecutorGr.shutdownGracefully();
                }

                if (!group.isShutdown()) {
                    group.shutdownGracefully();
                }
            }

        }
    }


    //Blocking until response!
    @Override
    public HttpResponse send(HttpRequest httpRequest) throws Exception {
        checkStatus(Status.START_END);
        Channel ch = channelFuture.get().channel();
        final HttpMethod method = HttpMethod.valueOf(httpRequest.getMethod().name());
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, method, httpRequest.getUri().getRawPath());
        fullHttpRequest.headers().set(HttpHeaderNames.HOST, host);
        fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        if (method == HttpMethod.POST) {
            final byte[] bytes = codec.encode(httpRequest.getSource());
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

        return httpResponseQueue.take();
    }


    @Override
    protected boolean getFullStopCondidtion() {
        return bossGroup.get().isTerminated() && handlersExecutorGroup.get().isTerminated();
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected Runnable getTask() {
        return new Task();
    }

    @Override
    protected void doStop() {
        channelFuture.get().channel().close();
    }
}
