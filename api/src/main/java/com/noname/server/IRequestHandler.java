package com.noname.server;



public interface IRequestHandler {
    Methods getMethod();
    String getUrl();
    ICodec getCodec();
    boolean process(IRequest request, IResponse response);
}
