package com.noname.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractService implements IService {

    private static final int TIMEOUT = 2;
    private static final int NUMBER_ATTEMPTS = 10;
    private final AtomicReference<Status> status = new AtomicReference<>(Status.INIT);
    private final ExecutorService service = Executors.newSingleThreadExecutor(new ServerThreadFactory(this.getClass().getSimpleName()));

    private final  AtomicReference<Boolean> started =  new AtomicReference<>(Boolean.FALSE);

    @Override
    public Status getStatus() {
        return status.get();
    }


    protected void setStatus(Status expected, Status newStatus) {
        if (!status.compareAndSet(expected, newStatus)) {
            throw getException(expected, newStatus);
        }
    }


    protected void checkStatus(Status expected) {
        final Status status = this.status.get();
        if (expected != status) {
            throw getException(expected, status);
        }
    }

    private IllegalStateException getException(Status expected, Status status) {
        return new IllegalStateException(String.format("Server expected to be  %s, but is %s", expected, status));
    }


    @Override
    public void waitForRun() throws InterruptedException {
        checkStatus(Status.INIT_OK);
        int counter = 0;
        while (status.get() == Status.INIT_OK && counter <= NUMBER_ATTEMPTS) {
            TimeUnit.SECONDS.sleep(TIMEOUT);
            counter++;
        }
        checkStatus(Status.RUN);
    }

    @Override
    public void waitForStop() throws InterruptedException {
        if (status.get()==Status.STOP_OK) {
            return;
        }
        checkStatus(Status.STOP);
        int counter = 0;
        while (!getFullStopCondidtion() && !service.isTerminated() && counter <= NUMBER_ATTEMPTS) {
            TimeUnit.SECONDS.sleep(TIMEOUT);
            counter++;
        }
        setStatus(Status.STOP, Status.STOP_OK);
    }

    protected abstract boolean getFullStopCondidtion();


    @Override
    public void start() throws InterruptedException {
        checkStatus(Status.INIT_OK);
        service.submit(getTask());
        doStart();
    }

    @Override
    public void stop() {
        setStatus(Status.RUN, Status.STOP);
        doStop();
        service.shutdownNow();
    }

    protected <T> void cas(AtomicReference<T> ref, T exp, T upd) {
        if (!ref.compareAndSet(exp, upd)){
            throw new  IllegalStateException(String.format("Expected to be  %s, but is %s", exp.toString(), upd.toString()));
        }
    }

    protected abstract void doStart();

    protected abstract Runnable getTask();

    protected abstract void doStop();
}
