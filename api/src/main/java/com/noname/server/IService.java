package com.noname.server;

public interface IService {
    enum Status {
        INIT_BEGIN, INIT_END, START_BEGIN, START_END, STOP_BEGIN, STOP_END
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
