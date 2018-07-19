package com.noname.server;

public interface ICodec {
    Object decode(byte[] bytes) throws Exception;
    byte[] encode(Object obj) throws Exception;
}
