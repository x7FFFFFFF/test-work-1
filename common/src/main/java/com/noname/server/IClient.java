package com.noname.server;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IClient {
    String PORT = "server.port";
    String PORT_DEFAULT_VALUE = "8888";
    String HOST = "server.host";


    enum Status {
        INIT, INIT_OK, RUN, STOP, STOP_OK
    }

 /*   void init(Properties properties);

    void init(Map<String, String> map);*/

    void start() throws InterruptedException;

    void stop();

    IClient.Status getStatus();

    /**
     * Warning: blocking!
     */
    IResponse send(IRequest request) throws Exception;

    /**
     * Warning: block current thread until server will start
     */
    void waitForRun() throws InterruptedException;

    /**
     * Warning: block current thread until server will stop
     */
    void waitForStop() throws InterruptedException;
}
