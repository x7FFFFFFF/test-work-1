package com.noname.server;

public interface IResponse extends IHttpRequestResponse{
    int getResultCode();
    void setResultCode(int code);
}
