package com.noname.server;


public interface Client extends Service {

    /**
     * Warning: blocking!
     */
    HttpResponse send(HttpRequest httpRequest) throws Exception;


}
