package com.noname.server;



import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.xml.XmlDecoder;
import io.netty.handler.codec.xml.XmlFrameDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class Server implements IServer {


    private  Status status;
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    private  ChannelFuture channelFuture;
    private   int port;
    private   List<IRequestHandler> handlers;

    private  final Lock lock = new ReentrantLock();
    private  final Condition startWaiting = lock.newCondition();
    public Server() {
        lock.lock();
        try {
            this.status = Status.INIT;
        } finally {
            lock.unlock();
        }

    }

    public void waitForStop() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            if (status==Status.STOP_OK) {
                return;
            }
            checkStatus(Status.STOP);
            while (status==Status.STOP) {
                startWaiting.await();
            }
            checkStatus(Status.STOP_OK );
        } finally {
            lock.unlock();
        }
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            EventLoopGroup bossGroup = null;
            EventLoopGroup workerGroup = null;
            try {
                bossGroup = new NioEventLoopGroup();
                workerGroup = new NioEventLoopGroup();
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
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
                                p.addLast("aggregator", new HttpObjectAggregator(Integer.MAX_VALUE));
                                p.addLast("handler", new ServerHandler(handlers));
                            }

                        });


               ChannelFuture channelFuture = b.bind(port).sync();


                setStatus(Status.RUN);
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();//TODO: logging

            } finally {
                if (bossGroup != null) {
                    bossGroup.shutdownGracefully();
                }
                if (workerGroup != null) {
                    workerGroup.shutdownGracefully();
                }
                setStatus( Status.STOP_OK);



            }
        }
    }
    private void setStatus(Status status) {
        lock.lock();
        try {
            this.status = status;
            this.startWaiting.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void checkStatus(Status expected){
            if (expected!=status){
                //setStatus(Status.ERROR);                //stop();
                throw new IllegalStateException(String.format("Server expected to be  %s, but is %s",  expected, status));
            }
    }


    @Override
    public void init(Properties properties, List<IRequestHandler> handlers) {
        lock.lock();
        try {
            checkStatus(Status.INIT);
            Map<String, String> map = properties.stringPropertyNames().stream().collect(Collectors.toMap(k -> k, properties::getProperty));
            init(map, handlers);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void init(Map<String, String> map, List<IRequestHandler> handlers) {
        lock.lock();
        try {
            checkStatus(Status.INIT);
            this.port = Integer.parseInt(map.getOrDefault(PORT, PORT_DEFAULT_VALUE));
            this.handlers= new ArrayList<>(handlers);
            this.status=Status.INIT_OK;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void start() throws InterruptedException {
        lock.lock();
        try {
            checkStatus(Status.INIT_OK);
            service.submit(new Task());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        service.shutdownNow();
        try {
            lock.lock();
            status = Status.STOP;
            startWaiting.signalAll();
            if (channelFuture!=null) {
                channelFuture.channel().close();
                channelFuture.awaitUninterruptibly();
            }
        } finally{
            lock.unlock();
        }
    }

    @Override
    public Status getStatus() {
        lock.lock();
        try {
            return this.status;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void waitForRun() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            checkStatus(Status.INIT_OK);
            while (status==Status.INIT_OK) {
                startWaiting.await();
            }
            checkStatus(Status.RUN );
        } finally {
            lock.unlock();
        }
    }
}
