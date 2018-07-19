package com.noname.server;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IServer  {

    String  WORKER_COUNTS ="server.workers.count";
    String  PORT ="server.port";
    String  PORT_DEFAULT_VALUE ="8888";
    String  HOST ="server.host";


    enum Status {
        INIT, INIT_OK, RUN, ERROR, STOP, STOP_OK
    }

    void init(Properties properties, List<IRequestHandler> handlers);
    void init(Map<String,String> map, List<IRequestHandler> handlers);

    void start() throws InterruptedException;
    void stop();
    Status getStatus();

    /**
     * Warning: block current thread until server will start
     */
    void waitForRun() throws InterruptedException;



}
