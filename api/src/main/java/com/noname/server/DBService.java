package com.noname.server;

public interface DBService extends Service {



    @Override
    default Status getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void waitForRun() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    default void waitForStop() throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}
