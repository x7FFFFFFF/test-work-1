package com.noname.server;



public interface IRequestHandler {
    Methods getMethod();
    String getUrl();
    boolean process(IRequest request, IResponse response);
}
