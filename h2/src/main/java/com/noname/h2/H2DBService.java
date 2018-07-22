package com.noname.h2;

import com.noname.server.DBService;
import com.noname.server.Service;
import org.h2.tools.Server;

import java.sql.SQLException;

public class H2DBService implements DBService {
    private final int port;
    private final Server server;

    public H2DBService(Integer port) throws SQLException {
        this.port = port;
        this.server =  Server.createTcpServer("-tcpPort", port.toString());
    }

    @Override
    public void start() throws Exception {
         server.start();
    }

    @Override
    public void stop() {
        server.stop();
    }

    @Override
    public Status getStatus() {
       throw new UnsupportedOperationException();
    }

    @Override
    public void waitForRun() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void waitForStop() throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}
