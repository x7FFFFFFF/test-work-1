package com.noname.misc;

import com.noname.server.Service;

import java.util.concurrent.ConcurrentHashMap;

public enum Environment implements AutoCloseable {
    INSTANCE;

    private final ConcurrentHashMap<Class<?>, AutoCloseable> mapAutoCloseable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, Service> mapService = new ConcurrentHashMap<>();




    public  <T extends AutoCloseable> void register(Class<?> clz, T instance) {
        if (mapAutoCloseable.putIfAbsent(clz, instance) != null) {
            throw new RuntimeException("Already registered, class = " + clz);
        }
    }

    public  <T extends Service> void register(Class<?> clz, T instance) {
        if (mapService.putIfAbsent(clz, instance) != null) {
            throw new RuntimeException("Already registered, class = " + clz);
        }
    }

    public  <T> T getResource(Class<T> clz){
        return (T)mapAutoCloseable.get(clz);      //FIXME:
    }

    public  <T> T getService(Class<T> clz){
        return (T)mapService.get(clz);      //FIXME:
    }



    @Override
    public void close() throws Exception {
        for (AutoCloseable autoCloseable : mapAutoCloseable.values()) {
            autoCloseable.close();
        }

        for (Service service : mapService.values()) {
            service.stop();
        }

    }
}
