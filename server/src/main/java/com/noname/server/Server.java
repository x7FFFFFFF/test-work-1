package com.noname.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class Server extends AbstractService {
    public final static String HANDLER_WORKER_COUNTS = "server.handlers.workers.count";
    public final static String BOSS_WORKER_COUNTS = "server.boss.workers.count";
    public final static String WORKER_COUNTS = "server.workers.count";
    public final static String PORT = "server.port";
    public final static String PORT_DEFAULT_VALUE = "8888";
    private final int bossCount;
    private final int handlersCount;
    private final int workersCount;
    private final int port;
    private final List<IRequestHandler> handlers;

    private final AtomicReference<ChannelFuture> channelFuture = new AtomicReference<>(null);
    private final AtomicReference<EventLoopGroup> bossGroup = new AtomicReference<>(null);
    private final AtomicReference<EventLoopGroup> workerGroup = new AtomicReference<>(null);
    private final AtomicReference<EventExecutorGroup> handlersExecutorGroup = new AtomicReference<>(null);


    public Server(Map<String, String> map, List<IRequestHandler> handlers) {
        setStatus(null, Status.INIT_BEGIN);
        this.port = Integer.parseInt(map.getOrDefault(PORT, PORT_DEFAULT_VALUE));
        this.handlers = Collections.unmodifiableList(handlers);
        this.bossCount = Integer.parseInt(map.getOrDefault(BOSS_WORKER_COUNTS, "1"));
        this.handlersCount = Integer.parseInt(map.getOrDefault(HANDLER_WORKER_COUNTS, "4"));
        this.workersCount = Integer.parseInt(map.getOrDefault(WORKER_COUNTS, "4"));
        setStatus(Status.INIT_BEGIN, Status.INIT_END);
    }


    private class Task implements Runnable {

        @Override
        public void run() {
            final EventLoopGroup bossGr = new NioEventLoopGroup(bossCount, new ServerThreadFactory("server-boss-pool"));
            final EventLoopGroup workerGr = new NioEventLoopGroup(workersCount, new ServerThreadFactory("server-workers-pool"));
            final EventExecutorGroup handlersExecutorGr = new DefaultEventExecutorGroup(handlersCount, new ServerThreadFactory("server-handlers-pool"));

            try {
                cas(bossGroup, null, bossGr);
                cas(workerGroup, null, workerGr);
                cas(handlersExecutorGroup, null, handlersExecutorGr);

                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGr, workerGr)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                //p.addLast("codec", new HttpServerCodec());   //TODO: XmlDecoder / encoder

                                //p.addLast("decoder1", new HttpRequestDecoder());
                                // p.addLast("decoder2", new XmlDecoder());
                                // p.addLast("encoder", new HttpResponseEncoder());
                                p.addLast("codec", new HttpServerCodec());
                                p.addLast("aggregator", new HttpObjectAggregator(1048576));
                                p.addLast(handlersExecutorGr, "handler", new ServerHandler(handlers));
                            }

                        });


                ChannelFuture channelFtr = b.bind(port).sync();

                cas(channelFuture, null, channelFtr);

                setStatus(Status.START_BEGIN, Status.START_END);
                channelFtr.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e); //TODO:log
            } finally {
                if (!handlersExecutorGr.isShuttingDown()) {
                    handlersExecutorGr.shutdownGracefully();
                }

                if (!workerGr.isShutdown()) {
                    workerGr.shutdownGracefully();
                }

                if (!bossGr.isShutdown()) {
                    bossGr.shutdownGracefully();
                }
            }
        }
    }


    @Override
    protected boolean getFullStopCondidtion() {
        return bossGroup.get().isTerminated() && workerGroup.get().isTerminated() && handlersExecutorGroup.get().isTerminated();
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
        this.channelFuture.get().channel().close();
    }


}
