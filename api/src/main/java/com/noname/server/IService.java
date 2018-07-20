package com.noname.server;

public interface IService {
    enum Status {
        INIT, INIT_OK, RUN, STOP, STOP_OK
    }

    void start() throws InterruptedException;

    void stop();

    Status getStatus();

    /**
     * Warning: block current thread until server will start
     */
    void waitForRun() throws InterruptedException;

    /**
     * Warning: block current thread until server will stop
     */
    void waitForStop() throws InterruptedException;

}
