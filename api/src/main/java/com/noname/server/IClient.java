package com.noname.server;


public interface IClient extends IService {

    /**
     * Warning: blocking!
     */
    IResponse send(IRequest request) throws Exception;


}
