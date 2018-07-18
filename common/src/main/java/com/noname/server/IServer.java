package com.noname.server;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IServer extends Runnable {

    String  WORKER_COUNTS ="server.workers.count";
    String  PORT ="server.port";
    String  HOST ="server.host";


    enum Status {
        INIT, RUN, ERROR, STOP
    }

    void init(Properties properties, List<IRequestHandler> handlers);
    void init(Map<String,String> map, List<IRequestHandler> handlers);


    void stop();
    Status getStatus();

    /**
     * Warning: block current thread until server will start
     */
    void waitForRun();



}
