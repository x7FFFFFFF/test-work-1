package com.noname.server;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IClient extends IService {
    String PORT = "server.port";
    String PORT_DEFAULT_VALUE = "8888";
    String HOST = "server.host";



    /**
     * Warning: blocking!
     */
    IResponse send(IRequest request) throws Exception;


}
