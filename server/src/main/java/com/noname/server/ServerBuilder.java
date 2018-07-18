package com.noname.server;

import java.util.ArrayList;
import java.util.List;

public class ServerBuilder {
    private int port;
    private final List<UriHandlerBased> handlers = new ArrayList<>();

    public ServerBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public ServerBuilder addHandler(UriHandlerBased handler) {
        this.handlers.add(handler);
        return this;
    }


    public Server createServer() {
        return new Server(port, handlers);
    }
}